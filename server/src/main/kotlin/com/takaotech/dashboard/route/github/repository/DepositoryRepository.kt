package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.route.github.data.GithubDepositoryEntity
import com.takaotech.dashboard.route.github.data.GithubDepositoryTable
import com.takaotech.dashboard.route.github.data.GithubUserEntity
import com.takaotech.dashboard.route.github.data.TagsEntity
import com.takaotech.dashboard.route.github.model.GHRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Factory

@Factory
class DepositoryRepository(private val database: Database) {

	fun saveAllRepositoryToDB(repositoryList: List<GHRepository>) {
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
				}.id

				GithubDepositoryEntity.new(repo.id) {
					name = repo.name
					fullName = repo.fullName
					url = repo.url
					user = searchedUser
					languages = repo.languages
				}
			}
		}
	}

	fun setTagsAtRepository(repositoryId: Long, tags: List<TagsEntity>) {
		transaction {
			GithubDepositoryEntity.findById(repositoryId)?.let {
				it.tags = SizedCollection(tags)
			}
		}
	}

	fun getAllDepository(): List<GithubDepositoryEntity> {
		return transaction {
			GithubDepositoryEntity.all()
				.toList()
		}
	}
}