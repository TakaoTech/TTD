package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.di.connectToDatabase
import com.takaotech.dashboard.model.github.GHLanguageDao
import com.takaotech.dashboard.model.github.GHRepositoryDao
import com.takaotech.dashboard.model.github.GHUser
import com.takaotech.dashboard.model.github.MainCategory
import com.takaotech.dashboard.utils.HikariDatabase
import com.takaotech.dashboard.utils.dbTables
import com.takaotech.dashboard.utils.getBaseTestKoin
import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DepositoryRepositoryTest : FunSpec(), KoinTest {
	override fun extensions() = listOf(
		KoinExtension(getBaseTestKoin())
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

		context("Save Data in DB") {
			test("Fresh Insert") {
				val depositoryRepository by inject<DepositoryRepository>()
				val updatedAtRepo1 = Clock.System.now()
				val updatedAtRepo2 = Clock.System.now()

				val inputRepository = listOf(
					GHRepositoryDao(
						id = 1,
						name = "Tracy Henson",
						fullName = "Clay Olsen",
						description = "Test description",
						url = "https://www.bing.com/search?q=splendide",
						license = "Apache 2",
						licenseUrl = "https://test.com",
						user = GHUser(
							id = 1744,
							name = "Katharine Hampton",
							url = "https://duckduckgo.com/?q=blandit",
							avatarUrl = "https://duckduckgo.com/?q=avatar"
						),
						languages = listOf(
							GHLanguageDao("Kotlin", 100)
						),
						tags = listOf(),
						mainCategory = MainCategory.NONE,
						updatedAt = updatedAtRepo1
					),
					GHRepositoryDao(
						id = 2,
						name = "Tracy Henson",
						fullName = "Clay Olsen",
						description = "Test description",
						url = "https://www.bing.com/search?q=splendide",
						license = "Apache 2",
						licenseUrl = "https://test.com",
						user = GHUser(
							id = 1744,
							name = "Katharine Hampton",
							url = "https://duckduckgo.com/?q=blandit",
							avatarUrl = "https://duckduckgo.com/?q=avatar"
						),
						languages = listOf(
							GHLanguageDao("Bash", 100)
						),
						tags = listOf(),
						mainCategory = MainCategory.NONE,
						updatedAt = updatedAtRepo2
					)
				)

				depositoryRepository.saveRepositoriesToDB(inputRepository)
				val recoveredRepos = depositoryRepository.getGHRepository()
				assertTrue { recoveredRepos.isNotEmpty() }
				//TODO Fix test, recoveredRepo is populated with langauges colors
				for (inputRepoIndexed in inputRepository.withIndex()) {
					val inputRepo = recoveredRepos[inputRepoIndexed.index]
					val outputRepo = inputRepoIndexed.value

					assertEquals(inputRepo.id, outputRepo.id)
					assertEquals(inputRepo.name, outputRepo.name)
					assertEquals(inputRepo.fullName, outputRepo.fullName)
					assertEquals(inputRepo.description, outputRepo.description)
					assertEquals(inputRepo.url, outputRepo.url)
					assertEquals(inputRepo.license, outputRepo.license)
					assertEquals(inputRepo.user, outputRepo.user)
					inputRepo.languages.forEachIndexed { index, languageInput ->
						val languageOutput = outputRepo.languages[index]

						assertEquals(languageInput.name, languageOutput.name)
						assertEquals(languageInput.lines, languageOutput.lines)
						//weight and color are skipped
					}
					assertEquals(inputRepo.tags, outputRepo.tags)
				}

				assertTrue { recoveredRepos.find { it.id == 1L }!!.mainCategory == MainCategory.KOTLIN }
				assertTrue { recoveredRepos.find { it.id == 2L }!!.mainCategory == MainCategory.NONE }
			}

			test("Append Insert 1") {
				val depositoryRepository by inject<DepositoryRepository>()

				val updatedAtRepo1 = Clock.System.now()
				val updatedAtRepo2 = Clock.System.now()

				val inputRepository = listOf(
					GHRepositoryDao(
						id = 1,
						name = "Tracy Henson",
						fullName = "Clay Olsen",
						description = "Test description",
						url = "https://www.bing.com/search?q=splendide",
						license = "Apache 2",
						licenseUrl = "https://test.com",
						user = GHUser(
							id = 1744,
							name = "Katharine Hampton",
							url = "https://duckduckgo.com/?q=blandit",
							avatarUrl = "https://duckduckgo.com/?q=avatar"
						),
						languages = listOf(
							GHLanguageDao("Kotlin", 100)
						),
						tags = listOf(),
						mainCategory = MainCategory.NONE,
						updatedAt = updatedAtRepo1
					),
					GHRepositoryDao(
						id = 2,
						name = "Tracy Henson",
						fullName = "Clay Olsen",
						description = "Test description",
						url = "https://www.bing.com/search?q=splendide",
						license = "Apache 2",
						licenseUrl = "https://test.com",
						user = GHUser(
							id = 1744,
							name = "Katharine Hampton",
							url = "https://duckduckgo.com/?q=blandit",
							avatarUrl = "https://duckduckgo.com/?q=avatar"
						),
						languages = listOf(
							GHLanguageDao("Bash", 100)
						),
						tags = listOf(),
						mainCategory = MainCategory.NONE,
						updatedAt = updatedAtRepo2
					)
				)

				depositoryRepository.saveRepositoriesToDB(inputRepository)
			}


		}

		test("ghRepositoryExist Repository Not Exist") {
			val depositoryRepository by inject<DepositoryRepository>()

			val repository = listOf(
				GHRepositoryDao(
					id = 1,
					name = "Tracy Henson",
					fullName = "Clay Olsen",
					description = "Test description",
					url = "https://www.bing.com/search?q=splendide",
					license = "Apache 2",
					licenseUrl = "https://test.com",
					user = GHUser(
						id = 1,
						name = "Katharine Hampton",
						url = "https://duckduckgo.com/?q=blandit",
						avatarUrl = "https://duckduckgo.com/?q=avatar"
					),
					languages = listOf(
						GHLanguageDao("Kotlin", 100)
					),
					tags = listOf(),
					mainCategory = MainCategory.NONE,
					updatedAt = Clock.System.now()
				)
			)

			depositoryRepository.saveRepositoriesToDB(repository)
			val recoveredRepo = depositoryRepository.getGHRepository()
			assertTrue { recoveredRepo.isNotEmpty() }
			assertFalse { depositoryRepository.ghRepositoryExist(2) }

		}

		test("ghRepositoryExist Repository Exist") {
			val depositoryRepository by inject<DepositoryRepository>()

			val repository = listOf(
				GHRepositoryDao(
					id = 1,
					name = "Tracy Henson",
					fullName = "Clay Olsen",
					description = "Test description",
					url = "https://www.bing.com/search?q=splendide",
					license = "Apache 2",
					licenseUrl = "https://test.com",
					user = GHUser(
						id = 1,
						name = "Katharine Hampton",
						url = "https://duckduckgo.com/?q=blandit",
						avatarUrl = "https://duckduckgo.com/?q=avatar"
					),
					languages = listOf(
						GHLanguageDao("Kotlin", 100)
					),
					tags = listOf(),
					mainCategory = MainCategory.NONE,
					updatedAt = Clock.System.now()
				),
				GHRepositoryDao(
					id = 2,
					name = "Tracy Henson",
					fullName = "Clay Olsen",
					description = "Test description",
					url = "https://www.bing.com/search?q=splendide",
					license = "Apache 2",
					licenseUrl = "https://test.com",
					user = GHUser(
						id = 2,
						name = "Katharine Hampton",
						url = "https://duckduckgo.com/?q=blandit",
						avatarUrl = "https://duckduckgo.com/?q=avatar"
					),
					languages = listOf(
						GHLanguageDao("Kotlin", 100)
					),
					tags = listOf(),
					mainCategory = MainCategory.NONE,
					updatedAt = Clock.System.now()
				)
			)

			depositoryRepository.saveRepositoriesToDB(repository)
			val recoveredRepo = depositoryRepository.getGHRepository()
			assertTrue { recoveredRepo.isNotEmpty() }
			assertTrue { depositoryRepository.ghRepositoryExist(2) }
		}
	}

}