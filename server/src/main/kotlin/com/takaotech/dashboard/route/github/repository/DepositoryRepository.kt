package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.model.TakaoPaging
import com.takaotech.dashboard.model.github.GHRepositoryDao
import com.takaotech.dashboard.model.github.GHRepositoryMiniDao
import com.takaotech.dashboard.model.github.GHUser
import com.takaotech.dashboard.model.github.MainCategory
import com.takaotech.dashboard.route.github.data.*
import com.takaotech.dashboard.route.github.repository.utils.convertToGHRepository
import com.takaotech.dashboard.route.github.repository.utils.convertToGHRepositoryMini
import com.takaotech.dashboard.utils.HikariDatabase
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.exposed.sql.EmptySizedIterable
import org.jetbrains.exposed.sql.SizedCollection
import org.koin.core.annotation.Factory

@Factory
class DepositoryRepository(
	private val database: HikariDatabase,
	private val colorController: GithubColorController
) {

	/**
	 * Procedura di salvataggio repository GH in DB
	 *
	 * Questa procedura esegue:
	 * - Verificare i repository già aggiunti da quelli nuovi
	 * - Verifica gli utenti già aggiunti da quelli nuovi
	 *
	 * - Aggiornare i dati utente
	 * - Aggiungere i nuovi utenti
	 *
	 * - Aggiornare i repository già aggiunti con dati nuovi
	 * - Aggiungere i nuovi repository
	 *
	 * @param repositoryList
	 */
	suspend fun saveRepositoriesToDB(repositoryList: List<GHRepositoryDao>) {
		val ghUsers = repositoryList.map {
			it.user
		}.toSet()
			.map {
				updateOrCreateGHUser(it)
			}

		repositoryList.forEach {
			updateOrCreateGHRepository(it, ghUsers)
		}
	}

	internal suspend fun updateOrCreateGHUser(user: GHUser): GithubUserEntity {
		return database.dbExec {
			val updateLambda: GithubUserEntity.() -> Unit = {
				name = user.name
				avatarUrl = user.avatarUrl
				url = user.url
			}

			val userEntity = GithubUserEntity.findById(user.id)?.apply(updateLambda)
				?: GithubUserEntity.new(user.id, updateLambda)
			//apply new or update to db
			commit()

			return@dbExec userEntity
		}
	}

	private suspend fun updateOrCreateGHRepository(
		repository: GHRepositoryDao,
		userPool: List<GithubUserEntity>
	) {
		database.dbExec {
			val ghDepository = GithubDepositoryEntity.findById(repository.id)
			val isNotCreated = ghDepository == null

			val updateLambda: GithubDepositoryEntity.() -> Unit = {
				name = repository.name
				fullName = repository.fullName
				description = repository.description
				url = repository.url
				user = userPool.first { it.id.value == repository.user.id }
				languages = repository.languages

				if (isNotCreated) {
					category = if (repository.languages.find { it.name == "Kotlin" } != null) {
						MainCategory.KOTLIN
					} else {
						MainCategory.NONE
					}
				}

				license = repository.license
				licenseUrl = repository.license

				updatedAt = repository.updatedAt
			}

			ghDepository?.apply(updateLambda) ?: GithubDepositoryEntity.new(repository.id, updateLambda)
		}
	}

	suspend fun setTagsAtRepository(repositoryId: Long, tags: List<TagsEntity>) {
		database.dbExec {
			GithubDepositoryEntity.findById(repositoryId)?.let {
				it.tags = SizedCollection(tags)
			}
		}
	}

	suspend fun getGHRepository(
		category: MainCategory? = null
	): List<GHRepositoryDao> {
		return database.dbExec {
			GithubDepositoryEntity.all().let {
				if (category != null) {
					it.filter { it.category == category }
				} else {
					it
				}
			}.toList()
		}.map {
			it.convertToGHRepository(database, colorController)
		}
	}

	suspend fun getGHRepositoryById(id: Long): GHRepositoryDao? {
		return database.dbExec {
			GithubDepositoryEntity.findById(id)
				?.convertToGHRepository(database, colorController)
		}
	}

	/**
	 * Check if a repository exists in db
	 */
	suspend fun ghRepositoryExist(id: Long): Boolean {
		return database.dbExec {
			GithubDepositoryEntity.findById(id) != null
		}
	}


	suspend fun updateGhRepositoryMainCategory(id: Long, mainCategory: MainCategory) {
		database.dbExec {
			GithubDepositoryEntity.findById(id)?.category = mainCategory
		}
	}

	suspend fun getGHRepositoryMini(
		mainCategory: MainCategory,
		page: Int,
		size: Int
	): TakaoPaging<GHRepositoryMiniDao> {
		//https://proandroiddev.com/pagination-sorting-and-custom-plugins-in-ktor-a2575e2da83a
		val limit: Int = size
		val pageSize: Int = size
		val skip: Int = (page - 1) * pageSize

		return database.dbExec {
			GithubDepositoryMiniEntity.find {
				GithubDepositoryTable.category eq mainCategory
			}.run {
				val totalPages = (count() / pageSize)
				totalPages to limit(offset = skip.toLong(), n = limit)
			}.run {
				TakaoPaging(
					data = second.map { it.convertToGHRepositoryMini(database, colorController) },
					page = page,
					totalPage = first
				)
			}
		}
	}

	@ApiStatus.Experimental
	internal suspend fun getGHRepositoryByTag(
		tagId: Int,
		page: Int,
		size: Int
	): TakaoPaging<GHRepositoryMiniDao> {
		val limit: Int = size
		val pageSize: Int = size
		val skip: Int = (page - 1) * pageSize

		return database.dbExec {
			//Here we can skip filtering KOTLIN
			(TagsEntityFollowRepo.findById(tagId)
				?.repositories ?: EmptySizedIterable())
				.run {
					val totalPages = (count() / pageSize)
					totalPages to limit(offset = skip.toLong(), n = limit)
				}.run {
					TakaoPaging(
						data = second.map { it.convertToGHRepositoryMini(database, colorController) },
						page = page,
						totalPage = first
					)
				}
		}
	}


}