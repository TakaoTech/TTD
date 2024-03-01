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

	suspend fun getDepository(
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
			GHRepositoryDao(
				id = it.id.value,
				name = it.name,
				fullName = it.fullName,
				description = it.description,
				url = it.url,
				license = it.license,
				licenseUrl = it.licenseUrl,
				user = database.dbExec {
					with(it.user) {
						GHUser(
							id = id.value,
							name = name,
							url = url
						)
					}
				},
				languages = it.languages,
				tags = database.dbExec {
					it.tags.map { entity ->
						Tag(entity.id.value)
					}
				},
				mainCategory = it.category
			)
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
}