package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.configuration.CredentialConfig
import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.configuration.GithubConfiguration
import com.takaotech.dashboard.configuration.SessionConfig
import com.takaotech.dashboard.di.connectToDatabase
import com.takaotech.dashboard.di.getGeneralModule
import com.takaotech.dashboard.model.GHRepository
import com.takaotech.dashboard.model.GHUser
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.utils.HikariDatabase
import com.takaotech.dashboard.utils.LOGGER
import com.takaotech.dashboard.utils.dbTables
import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.koin.ksp.generated.defaultModule
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
		beforeEach {
			val dbConfiguration by inject<DbConfiguration>()
			val database by inject<HikariDatabase>()
			connectToDatabase(dbConfiguration)
			database.dbExec {
				addLogger(StdOutSqlLogger)
				SchemaUtils.drop(*dbTables)
				SchemaUtils.create(*dbTables)
				commit()
			}
		}

		test("Save data in DB") {
			val depositoryRepository by inject<DepositoryRepository>()

			val inputRepository = listOf(
				GHRepository(
					id = 1,
					name = "Tracy Henson",
					fullName = "Clay Olsen",
					url = "https://www.bing.com/search?q=splendide",
					user = GHUser(
						id = 1744, name = "Katharine Hampton", url = "https://duckduckgo.com/?q=blandit"
					),
					languages = mapOf(
						"Kotlin" to 100
					),
					tags = listOf(),
					mainCategory = MainCategory.NONE
				),
				GHRepository(
					id = 2,
					name = "Tracy Henson",
					fullName = "Clay Olsen",
					url = "https://www.bing.com/search?q=splendide",
					user = GHUser(
						id = 1744, name = "Katharine Hampton", url = "https://duckduckgo.com/?q=blandit"
					),
					languages = mapOf(
						"Bash" to 100
					),
					tags = listOf(),
					mainCategory = MainCategory.NONE
				)
			)

			val outputRepository = listOf(
				GHRepository(
					id = 1,
					name = "Tracy Henson",
					fullName = "Clay Olsen",
					url = "https://www.bing.com/search?q=splendide",
					user = GHUser(
						id = 1744, name = "Katharine Hampton", url = "https://duckduckgo.com/?q=blandit"
					),
					languages = mapOf(
						"Kotlin" to 100
					),
					tags = listOf(),
					mainCategory = MainCategory.KOTLIN
				),
				GHRepository(
					id = 2,
					name = "Tracy Henson",
					fullName = "Clay Olsen",
					url = "https://www.bing.com/search?q=splendide",
					user = GHUser(
						id = 1744, name = "Katharine Hampton", url = "https://duckduckgo.com/?q=blandit"
					),
					languages = mapOf(
						"Bash" to 100
					),
					tags = listOf(),
					mainCategory = MainCategory.NONE
				)
			)

			depositoryRepository.saveRepositoriesToDB(inputRepository)
			val recoveredRepo = depositoryRepository.getDepository()
			assertTrue { recoveredRepo.isNotEmpty() }
			assertEquals(outputRepository, recoveredRepo)
			assertTrue { recoveredRepo.find { it.id == 1L }!!.mainCategory == MainCategory.KOTLIN }
			assertTrue { recoveredRepo.find { it.id == 2L }!!.mainCategory == MainCategory.NONE }
		}

		test("ghRepositoryExist Repository Not Exist") {
			val depositoryRepository by inject<DepositoryRepository>()

			val repository = listOf(
				GHRepository(
					id = 1,
					name = "Tracy Henson",
					fullName = "Clay Olsen",
					url = "https://www.bing.com/search?q=splendide",
					user = GHUser(
						id = 1, name = "Katharine Hampton", url = "https://duckduckgo.com/?q=blandit"
					),
					languages = mapOf(
						"Kotlin" to 20
					),
					tags = listOf(),
					mainCategory = MainCategory.NONE
				)
			)

			depositoryRepository.saveRepositoriesToDB(repository)
			val recoveredRepo = depositoryRepository.getDepository()
			assertTrue { recoveredRepo.isNotEmpty() }
			assertFalse { depositoryRepository.ghRepositoryExist(2) }

		}

		test("ghRepositoryExist Repository Exist") {
			val depositoryRepository by inject<DepositoryRepository>()

			val repository = listOf(
				GHRepository(
					id = 1,
					name = "Tracy Henson",
					fullName = "Clay Olsen",
					url = "https://www.bing.com/search?q=splendide",
					user = GHUser(
						id = 1, name = "Katharine Hampton", url = "https://duckduckgo.com/?q=blandit"
					),
					languages = mapOf(
						"Kotlin" to 20
					),
					tags = listOf(),
					mainCategory = MainCategory.NONE
				),
				GHRepository(
					id = 2,
					name = "Tracy Henson",
					fullName = "Clay Olsen",
					url = "https://www.bing.com/search?q=splendide",
					user = GHUser(
						id = 2, name = "Katharine Hampton", url = "https://duckduckgo.com/?q=blandit"
					),
					languages = mapOf(
						"Kotlin" to 20
					),
					tags = listOf(),
					mainCategory = MainCategory.NONE
				)
			)

			depositoryRepository.saveRepositoriesToDB(repository)
			val recoveredRepo = depositoryRepository.getDepository()
			assertTrue { recoveredRepo.isNotEmpty() }
			assertTrue { depositoryRepository.ghRepositoryExist(2) }
		}
	}

}