package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.model.GHRepositoryDao
import com.takaotech.dashboard.model.GHUser
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.model.Tag
import com.takaotech.dashboard.route.github.data.GithubDepositoryEntity
import com.takaotech.dashboard.route.github.data.GithubDepositoryTable
import com.takaotech.dashboard.route.github.data.GithubUserEntity
import com.takaotech.dashboard.route.github.data.TagsEntity
import com.takaotech.dashboard.utils.HikariDatabase
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.selectAll
import org.koin.core.annotation.Factory

@Factory
class DepositoryRepository(
	private val database: HikariDatabase
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
					GithubUserEntity.findById(user.id) ?: GithubUserEntity.new(user.id) {
						name = user.name
						url = user.url
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
					category = if (repo.languages.containsKey("Kotlin")) {
						MainCategory.KOTLIN
					} else {
						MainCategory.NONE
					}

					license = repo.license
					licenseUrl = repo.licenseUrl

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
			it.convertGRepository()
		}
	}

	suspend fun getGHRepositoryById(id: Long): GHRepositoryDao? {
		return database.dbExec {
			GithubDepositoryEntity.findById(id)
				?.convertGRepository()
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


	private suspend fun GithubDepositoryEntity.convertGRepository(): GHRepositoryDao {
		return GHRepositoryDao(
			id = id.value,
			name = name,
			fullName = fullName,
			description = description,
			url = url,
			license = license,
			licenseUrl = licenseUrl,
			user = database.dbExec {
				with(user) {
					GHUser(
						id = id.value,
						name = name,
						url = url
					)
				}
			},
			languages = languages,
			tags = database.dbExec {
				tags.map { entity ->
					Tag(entity.id.value)
				}
			},
			mainCategory = category
		)
	}


}