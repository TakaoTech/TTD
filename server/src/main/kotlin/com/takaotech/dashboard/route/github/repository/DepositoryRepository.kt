package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.model.TakaoPaging
import com.takaotech.dashboard.model.github.GHRepositoryDao
import com.takaotech.dashboard.model.github.GHRepositoryMiniDao
import com.takaotech.dashboard.model.github.MainCategory
import com.takaotech.dashboard.route.github.data.*
import com.takaotech.dashboard.route.github.repository.utils.convertToGHRepository
import com.takaotech.dashboard.route.github.repository.utils.convertToGHRepositoryMini
import com.takaotech.dashboard.utils.HikariDatabase
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.exposed.sql.EmptySizedIterable
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.selectAll
import org.koin.core.annotation.Factory

@Factory
class DepositoryRepository(
	private val database: HikariDatabase,
	private val colorController: GithubColorController
) {

	suspend fun saveRepositoriesToDB(repositoryList: List<GHRepositoryDao>) {
		database.dbExec {
			var repositoryListFiltered = repositoryList
			if (!GithubDepositoryTable.selectAll().empty()) {
				repositoryListFiltered = repositoryList.map {
					it.id
				}.let {
					GithubDepositoryTable.slice(GithubDepositoryTable.id)
						.selectAll()
						.map {
							it[GithubDepositoryTable.id].value
						}
				}.let { exclusionList ->
					repositoryList.filterNot {
						exclusionList.contains(it.id)
					}
				}
			}

			for (repository in repositoryListFiltered) {
				repository.user.let { user ->
					//TODO Procedure buggata, non permette un allineamento dei dati tra GH e il mio depo
					GithubUserEntity.findById(user.id) ?: GithubUserEntity.new(user.id) {
						name = user.name
						url = user.url
						avatarUrl = user.avatarUrl
					}
				}
			}

			commit()

			val users = GithubUserEntity.all()

			for (repo in repositoryList) {
				val searchedUser = users.first { user ->
					user.id.value == repo.user.id
				}

				GithubDepositoryEntity.new(repo.id) {
					name = repo.name
					fullName = repo.fullName
					description = repo.description
					url = repo.url
					user = searchedUser
					languages = repo.languages
					//TODO Make "Kotlin" costant
					category = if (repo.languages.find { it.name == "Kotlin" } != null) {
						MainCategory.KOTLIN
					} else {
						MainCategory.NONE
					}

					license = repo.license
					licenseUrl = repo.licenseUrl
					updatedAt = repo.updatedAt

				}
			}
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