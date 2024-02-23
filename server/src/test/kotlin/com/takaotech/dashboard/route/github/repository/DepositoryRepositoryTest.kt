package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.configuration.CredentialConfig
import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.configuration.GithubConfiguration
import com.takaotech.dashboard.configuration.SessionConfig
import com.takaotech.dashboard.di.getGeneralModule
import com.takaotech.dashboard.route.github.data.GithubDepositoryTable
import com.takaotech.dashboard.route.github.data.GithubDepositoryTagsTable
import com.takaotech.dashboard.route.github.data.GithubUserTable
import com.takaotech.dashboard.route.github.data.TagsTable
import com.takaotech.dashboard.route.github.model.GHRepository
import com.takaotech.dashboard.route.github.model.GHUser
import com.takaotech.dashboard.utils.LOGGER
import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ksp.generated.defaultModule
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertTrue

class DepositoryRepositoryTest : FunSpec(), KoinTest {
	override fun extensions() = listOf(
		KoinExtension(
			listOf(
				getGeneralModule(
					log = LOGGER,
					dbConfiguration = DbConfiguration(
						url = System.getenv("DB_URL"),
						driver = System.getenv("DB_DRIVER"),
						user = System.getenv("DB_USER"),
						password = System.getenv("DB_PASSWORD")
					),
					githubConfiguration = GithubConfiguration(
						githubToken = System.getenv("GITHUB_TOKEN")
					),
					credentialConfig = CredentialConfig(
						digestAlgorithm = System.getenv("auth.digest.alg"),
						digest = System.getenv("auth.digest"),
						username = System.getenv("auth.username"),
						password = System.getenv("auth.password"),
						sessionConfig = SessionConfig(
							name = System.getenv("auth.session.name"),
							realm = System.getenv("auth.session.realm")
						)
					)
				), defaultModule
			)
		)
	)

	init {
		test("Save data in DB") {
			val database by inject<Database>()
			val depositoryRepository by inject<DepositoryRepository>()
			transaction(database) {
				addLogger(StdOutSqlLogger)
				SchemaUtils.create(TagsTable, GithubDepositoryTable, GithubUserTable, GithubDepositoryTagsTable)
				commit()
			}

			val repository = listOf(
				GHRepository(
					id = 1254,
					name = "Tracy Henson",
					fullName = "Clay Olsen",
					url = "https://www.bing.com/search?q=splendide",
					user = GHUser(
						id = 1744, name = "Katharine Hampton", url = "https://duckduckgo.com/?q=blandit"
					),
					languages = mapOf(
						"Kotlin" to 20
					)
				)
			)

			depositoryRepository.saveRepositoriesToDB(repository)
			val recoveredRepo = depositoryRepository.getAllDepository()
			assertTrue { recoveredRepo.isNotEmpty() }
			assertTrue { recoveredRepo.map { it.id.value } == repository.map { it.id } }
		}
	}

}