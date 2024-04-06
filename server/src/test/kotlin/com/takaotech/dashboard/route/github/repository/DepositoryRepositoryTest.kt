package com.takaotech.dashboard.route.github.repository

import com.github.kittinunf.result.isFailure
import com.github.kittinunf.result.isSuccess
import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.di.connectToDatabase
import com.takaotech.dashboard.model.github.GHLanguageDao
import com.takaotech.dashboard.model.github.GHRepositoryDao
import com.takaotech.dashboard.model.github.GHUser
import com.takaotech.dashboard.model.github.MainCategory
import com.takaotech.dashboard.route.github.data.GithubDepositoryEntity
import com.takaotech.dashboard.route.github.data.GithubUserEntity
import com.takaotech.dashboard.route.github.data.TagsEntity
import com.takaotech.dashboard.utils.HikariDatabase
import com.takaotech.dashboard.utils.dbTables
import com.takaotech.dashboard.utils.getBaseTestKoin
import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.ktor.util.logging.*
import io.mockk.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.inject
import java.io.File
import java.nio.file.Paths
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

		val testResourcePath = Paths.get("").toAbsolutePath().toString() + "/src/test/resources/"
		val colors = Json.parseToJsonElement(File(testResourcePath, "githubColors.json").readText()).jsonObject
		var depositoryRepository = DepositoryRepository(
			mockk<HikariDatabase>(),
			mockk<Logger>(),
			GithubColorControllerImpl(colors)
		)

		//TODO Sistemare Test, Nella zona dei context il DB non deve essere resettato
		beforeEach {
			val dbConfiguration by inject<DbConfiguration>()
			val database by inject<HikariDatabase>()
			val logger by inject<Logger>()
			connectToDatabase(dbConfiguration)
			database.dbExec {
				addLogger(StdOutSqlLogger)
				SchemaUtils.drop(*dbTables)
				SchemaUtils.create(*dbTables)
				commit()
			}

			depositoryRepository = DepositoryRepository(
				database,
				logger,
				GithubColorControllerImpl(colors)
			)
		}

		context("Save Data in DB") {
			test("Fresh Insert") {
				val spyDepositoryRepository = spyk(depositoryRepository, recordPrivateCalls = true)
				val userSlot = mutableListOf<GHUser>()

				coEvery {
					spyDepositoryRepository.updateOrCreateGHUser(capture(userSlot))
				} coAnswers {
					callOriginal()
				}

				spyDepositoryRepository.saveRepositoriesToDB(inputRepositories)
				val outputRepositories = spyDepositoryRepository.getGHRepository()
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
				val spyDepositoryRepository = spyk(depositoryRepository, recordPrivateCalls = true)
				val userSlot = mutableListOf<GHUser>()

				val repo1DescriptionUpdated = inputRepository1.copy(
					description = "Description updated"
				)

				val inputRepository = listOf(
					repo1DescriptionUpdated,
					inputRepository2
				)

				coEvery {
					spyDepositoryRepository.updateOrCreateGHUser(capture(userSlot))
				} coAnswers {
					callOriginal()
				}

				spyDepositoryRepository.saveRepositoriesToDB(inputRepository)


				val recoveredRepos = spyDepositoryRepository.getGHRepository()
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
				val spyDepositoryRepository = spyk(depositoryRepository)
				val userSlot = mutableListOf<GHUser>()

				val repo2DescriptionUpdated = inputRepository2.copy(
					user = user3
				)

				val inputRepository = listOf(
					repo2DescriptionUpdated
				)

				coEvery {
					spyDepositoryRepository.updateOrCreateGHUser(capture(userSlot))
				} coAnswers {
					callOriginal()
				}

				spyDepositoryRepository.saveRepositoriesToDB(inputRepository)

				val recoveredRepos = spyDepositoryRepository.getGHRepository()
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
					//TODO Missing test tag
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

		context("ghRepositoryExist") {
			beforeTest {
				depositoryRepository.saveRepositoriesToDB(inputRepositories)
			}

			test("ghRepositoryExist Repository Not Exist") {
				assertFalse { depositoryRepository.ghRepositoryExist(5) }
			}
			test("ghRepositoryExist Repository Exist") {
				assertTrue { depositoryRepository.ghRepositoryExist(inputRepository1.id) }
			}
		}

		context("Set category at repository") {

			test("At existing repository") {
				val repositoryTest = inputRepository2

				depositoryRepository.saveRepositoriesToDB(inputRepositories)

				MainCategory.entries.forEach {
					depositoryRepository.updateGhRepositoryMainCategory(repositoryTest.id, it)
					assertEquals(
						it, depositoryRepository.getGHRepositoryById(repositoryTest.id)?.mainCategory
					)
				}
			}

			//Test excluded because verify calculate input repository filling in call counter
			xtest("At not exist") {

				depositoryRepository.saveRepositoriesToDB(inputRepositories)
				mockkObject(GithubDepositoryEntity)

				every {
					GithubDepositoryEntity.findById(any<Long>())
				} returns null

				MainCategory.entries.forEach {
					depositoryRepository.updateGhRepositoryMainCategory(2, it)
				}

				verify(exactly = 0) {
					GithubDepositoryEntity.findById(any<Long>())!!.category
				}

				unmockkObject(GithubDepositoryEntity)
			}
		}



		context("Set tags at repository") {
			val namesList = listOf("Tag1", "Tag2", "Tag3", "Tag4", "Tag5")

			beforeTest {
				depositoryRepository.saveRepositoriesToDB(inputRepositories)
			}

			test("Set Tag at Repository") {
				val database by inject<HikariDatabase>()

				val tagsIds = database.dbExec {
					namesList.map {
						TagsEntity.new {
							name = it
						}
					}
				}

				val result = depositoryRepository.setTagsAtRepository(inputRepository1.id, tagsIds)

				assertTrue { result.isSuccess() }

				assertEquals(
					tagsIds.map { it.id.value },
					depositoryRepository.getGHRepositoryById(inputRepository1.id)!!.tags.map { it.id })
			}

			test("Set Duplicated Tag at Repository, throw exception") {
				val database by inject<HikariDatabase>()

				val tagsIds = database.dbExec {
					namesList.map {
						TagsEntity.new {
							name = it
						}
					}
				}.toMutableList()

				tagsIds.add(tagsIds.first())

				val result = depositoryRepository.setTagsAtRepository(inputRepository1.id, tagsIds)

				assertTrue { result.isFailure() }
				assertTrue { result.failure() is ExposedSQLException }
			}

			test("Set Empty Tag at Repository") {
				val database by inject<HikariDatabase>()

				val tagsIds = database.dbExec {
					namesList.map {
						TagsEntity.new {
							name = it
						}
					}
				}.toMutableList()

				val resultSet = depositoryRepository.setTagsAtRepository(inputRepository1.id, tagsIds)

				assertTrue { resultSet.isSuccess() }
				assertTrue { depositoryRepository.getGHRepositoryById(inputRepository1.id)!!.tags.isNotEmpty() }

				val result = depositoryRepository.setTagsAtRepository(inputRepository1.id, listOf())

				assertTrue { result.isSuccess() }
				assertTrue { depositoryRepository.getGHRepositoryById(inputRepository1.id)!!.tags.isEmpty() }
			}


		}

		//TODO Missing test getGHRepository with different category
	}

}