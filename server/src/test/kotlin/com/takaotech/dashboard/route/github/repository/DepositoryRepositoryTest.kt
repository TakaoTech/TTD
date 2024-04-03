package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.di.connectToDatabase
import com.takaotech.dashboard.model.github.GHLanguageDao
import com.takaotech.dashboard.model.github.GHRepositoryDao
import com.takaotech.dashboard.model.github.GHUser
import com.takaotech.dashboard.model.github.MainCategory
import com.takaotech.dashboard.route.github.data.GithubUserEntity
import com.takaotech.dashboard.utils.HikariDatabase
import com.takaotech.dashboard.utils.dbTables
import com.takaotech.dashboard.utils.getBaseTestKoin
import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.mockk.coEvery
import io.mockk.spyk
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.inject
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DepositoryRepositoryTest : FunSpec(), KoinTest {
	override fun extensions() = listOf(
		KoinExtension(getBaseTestKoin())
	)

	init {
		//region Base Test Data

		val updatedAtRepo1 = Clock.System.now()
		val updatedAtRepo2 = Clock.System.now()

		val user1 = GHUser(
			id = 1744,
			name = "Katharine Hampton",
			url = "https://duckduckgo.com/?q=blandit1744",
			avatarUrl = "https://duckduckgo.com/?q=avatar1744"
		)

		val user2 = GHUser(
			id = 17455,
			name = "Katharine Hampton",
			url = "https://duckduckgo.com/?q=blandit17455",
			avatarUrl = "https://duckduckgo.com/?q=avatar17455"
		)

		val user3 = GHUser(
			id = 640,
			name = "Gino Pilotino",
			url = "https://duckduckgo.com/?q=R2lubyBQaWxvdGlubw==",
			avatarUrl = "https://duckduckgo.com/?q=R2lubyBQaWxvdGlubyBhdmF0YXI="
		)

		val inputRepository1 = GHRepositoryDao(
			id = 1,
			name = "Tracy Henson",
			fullName = "Clay Olsen",
			description = "Test description",
			url = "https://www.bing.com/search?q=splendide",
			license = "Apache 2",
			licenseUrl = "https://test.com",
			user = user1,
			languages = listOf(
				GHLanguageDao("Kotlin", 100)
			),
			tags = listOf(),
			mainCategory = MainCategory.NONE,
			updatedAt = updatedAtRepo1
		)

		val inputRepository2 = GHRepositoryDao(
			id = 2,
			name = "Tracy Henson",
			fullName = "Clay Olsen",
			description = "Test description",
			url = "https://www.bing.com/search?q=splendide",
			license = "Apache 2",
			licenseUrl = "https://test.com",
			user = user2,
			languages = listOf(
				GHLanguageDao("Bash", 100)
			),
			tags = listOf(),
			mainCategory = MainCategory.NONE,
			updatedAt = updatedAtRepo2
		)

		val inputRepository3 = GHRepositoryDao(
			id = 3,
			name = "Erat aliqua",
			fullName = "Erat aliqua ad iriure",
			description = "Test description 3 ",
			url = "https://www.bing.com/search?q=splendide3",
			license = "Apache 2",
			licenseUrl = "https://test3.com",
			user = user1,
			languages = listOf(
				GHLanguageDao("Bash", 100)
			),
			tags = listOf(),
			mainCategory = MainCategory.NONE,
			updatedAt = updatedAtRepo2
		)

		val inputRepositories = listOf(
			inputRepository1,
			inputRepository2,
			inputRepository3
		)

		//endregion Base Test Data

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
				val depositoryRepository = spyk(get<DepositoryRepository>())
				val userSlot = mutableListOf<GHUser>()

				coEvery {
					depositoryRepository.updateOrCreateGHUser(capture(userSlot))
				} coAnswers {
					callOriginal()
				}

				depositoryRepository.saveRepositoriesToDB(inputRepositories)
				val outputRepositories = depositoryRepository.getGHRepository()
				assertTrue { outputRepositories.isNotEmpty() }
				for (inputRepo in inputRepositories) {
					val outputRepo = outputRepositories.single { it.id == inputRepo.id }

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

				assertTrue { outputRepositories.find { it.id == 1L }!!.mainCategory == MainCategory.KOTLIN }
				assertTrue { outputRepositories.find { it.id == 2L }!!.mainCategory == MainCategory.NONE }

				assertTrue {
					userSlot.size == userSlot.distinctBy { it.id }.size
				}
			}

			test("Insert same Repository, but updated description") {
				val depositoryRepository = spyk(get<DepositoryRepository>(), recordPrivateCalls = true)
				val userSlot = mutableListOf<GHUser>()

				val repo1DescriptionUpdated = inputRepository1.copy(
					description = "Description updated"
				)

				val inputRepository = listOf(
					repo1DescriptionUpdated,
					inputRepository2
				)

				coEvery {
					depositoryRepository.updateOrCreateGHUser(capture(userSlot))
				} coAnswers {
					callOriginal()
				}

				depositoryRepository.saveRepositoriesToDB(inputRepository)


				val recoveredRepos = depositoryRepository.getGHRepository()
				assertTrue { recoveredRepos.isNotEmpty() }

				for (inputRepo in inputRepository) {
					val outputRepo = recoveredRepos.single { it.id == inputRepo.id }

					assertEquals(inputRepo.id, outputRepo.id)
					assertEquals(inputRepo.name, outputRepo.name)
					assertEquals(inputRepo.fullName, outputRepo.fullName)
					assertEquals(inputRepo.description, outputRepo.description)
					if (outputRepo.id == 1L) {
						assertNotEquals(inputRepository1.description, outputRepo.description)
					}
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

				assertTrue {
					userSlot.size == userSlot.distinctBy { it.id }.size
				}
			}

			test("Insert Same Repository 2, but change user") {
				val depositoryRepository = spyk(get<DepositoryRepository>())
				val userSlot = mutableListOf<GHUser>()

				val repo2DescriptionUpdated = inputRepository2.copy(
					user = user3
				)

				val inputRepository = listOf(
					repo2DescriptionUpdated
				)

				coEvery {
					depositoryRepository.updateOrCreateGHUser(capture(userSlot))
				} coAnswers {
					callOriginal()
				}

				depositoryRepository.saveRepositoriesToDB(inputRepository)

				val recoveredRepos = depositoryRepository.getGHRepository()
				assertTrue { recoveredRepos.isNotEmpty() }

				for (inputRepo in inputRepository) {
					val outputRepo = recoveredRepos.single { it.id == inputRepo.id }

					assertEquals(inputRepo.id, outputRepo.id)
					assertEquals(inputRepo.name, outputRepo.name)
					assertEquals(inputRepo.fullName, outputRepo.fullName)
					assertEquals(inputRepo.description, outputRepo.description)


					assertEquals(inputRepo.url, outputRepo.url)
					assertEquals(inputRepo.license, outputRepo.license)
					if (outputRepo.id == 2L) {
						assertNotEquals(inputRepository2.user, outputRepo.user)
					}
					assertEquals(inputRepo.user, outputRepo.user)
					inputRepo.languages.forEachIndexed { index, languageInput ->
						val languageOutput = outputRepo.languages[index]

						assertEquals(languageInput.name, languageOutput.name)
						assertEquals(languageInput.lines, languageOutput.lines)
						//weight and color are skipped
					}
					assertEquals(inputRepo.tags, outputRepo.tags)
				}

				assertTrue {
					userSlot.size == userSlot.distinctBy { it.id }.size
				}
			}
		}

		context("Save User") {
			val inputUsers = listOf(
				user1,
				user2,
				user3
			)

			test("Fresh Insert") {
				val database = get<HikariDatabase>()
				val depositoryRepository = get<DepositoryRepository>()
				inputUsers.forEach {
					depositoryRepository.updateOrCreateGHUser(it)
				}

				val outputUsers = database.dbExec {
					GithubUserEntity.all().notForUpdate().toList()
				}

				outputUsers.forEach { outputUser ->
					val inputUser = inputUsers.single { it.id == outputUser.id.value }
					assertEquals(inputUser.id, outputUser.id.value)
					assertEquals(inputUser.name, outputUser.name)
					assertEquals(inputUser.url, outputUser.url)
					assertEquals(inputUser.avatarUrl, outputUser.avatarUrl)
				}
			}

			test("Insert same user, but change user 3") {
				val database = get<HikariDatabase>()
				val depositoryRepository = get<DepositoryRepository>()
				val inputUsers = listOf(
					user1,
					user2,
					user3.copy(
						name = "User 3 Changed",
						url = "https://www.example.com/random-path/abcdef12345",
						avatarUrl = "https://www.example.com/random-path/abcdef12345.png"
					)
				)

				inputUsers.forEach {
					depositoryRepository.updateOrCreateGHUser(it)
				}

				val outputUsers = database.dbExec {
					GithubUserEntity.all().notForUpdate().toList()
				}

				outputUsers.forEach { outputUser ->
					val inputUser = inputUsers.single { it.id == outputUser.id.value }
					assertEquals(inputUser.id, outputUser.id.value)
					if (outputUser.id.value == user3.id) {
						assertNotEquals(user3.name, outputUser.name)
						assertNotEquals(user3.url, outputUser.url)
						assertNotEquals(user3.avatarUrl, outputUser.avatarUrl)
					}

					assertEquals(inputUser.name, outputUser.name)
					assertEquals(inputUser.url, outputUser.url)
					assertEquals(inputUser.avatarUrl, outputUser.avatarUrl)

				}
			}
		}

		test("ghRepositoryExist Repository Not Exist") {
			val depositoryRepository = get<DepositoryRepository>()

			depositoryRepository.saveRepositoriesToDB(inputRepositories)
			assertFalse { depositoryRepository.ghRepositoryExist(5) }

		}

		test("ghRepositoryExist Repository Exist") {
			val depositoryRepository = get<DepositoryRepository>()

			depositoryRepository.saveRepositoriesToDB(inputRepositories)
			assertTrue { depositoryRepository.ghRepositoryExist(inputRepository1.id) }
		}
	}

}