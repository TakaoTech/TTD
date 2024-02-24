package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.model.GHRepository
import com.takaotech.dashboard.model.GHUser
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.route.github.data.GithubDepositoryEntity
import com.takaotech.dashboard.route.github.data.GithubDepositoryTable
import com.takaotech.dashboard.route.github.data.GithubUserEntity
import com.takaotech.dashboard.route.github.data.TagsEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Factory

@Factory
class DepositoryRepository(private val database: Database) {

	fun saveRepositoriesToDB(repositoryList: List<GHRepository>) {
		transaction(database) {
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
					url = repo.url
					user = searchedUser
					languages = repo.languages
					category = if (repo.languages.containsKey("Kotlin")) {
						MainCategory.KOTLIN
					} else {
						MainCategory.NONE
					}
				}
			}
		}
	}

	fun setTagsAtRepository(repositoryId: Long, tags: List<TagsEntity>) {
		transaction(database) {
			GithubDepositoryEntity.findById(repositoryId)?.let {
				it.tags = SizedCollection(tags)
			}
		}
	}

	fun getAllDepository(): List<GHRepository> {
		return transaction(database) {
			GithubDepositoryEntity.all()
				.toList()
		}.map {
			GHRepository(
				id = it.id.value,
				name = it.name,
				fullName = it.fullName,
				url = it.url,
				user = transaction(database) {
					with(it.user) {
						GHUser(
							id = id.value,
							name = name,
							url = url
						)
					}
				},
				languages = it.languages,
				mainCategory = it.category,
				tags = transaction(database) {
					it.tags.toList().map { it.name }
				}
			)
		}
	}

	/**
	 * Check if a repository exists in db
	 */
	fun ghRepositoryExist(id: Long): Boolean {
		return transaction(database) {
			GithubDepositoryEntity.findById(id) != null
		}
	}
}