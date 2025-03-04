package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.models.GHLanguageDao
import com.takaotech.dashboard.models.GHRepositoryDao
import com.takaotech.dashboard.models.MainCategory
import com.takaotech.dashboard.route.github.data.GithubDepositoryEntity
import com.takaotech.dashboard.route.github.data.TagsEntity
import com.takaotech.dashboard.utils.GHLanguageLinesModifier
import com.takaotech.dashboard.utils.GHLanguageNameModifier
import com.takaotech.dashboard.utils.HikariDatabase
import com.takaotech.dashboard.utils.RedisDatabase
import com.takaotech.dashboard.utils.dbTables
import com.takaotech.dashboard.utils.generateLanguages
import com.takaotech.dashboard.utils.getBaseTestKoin
import com.takaotech.dashboard.utils.getDbConfiguration
import com.takaotech.dashboard.utils.getGHLanguagesColor
import com.takaotech.dashboard.utils.getGHLanguagesGenerator
import com.takaotech.dashboard.utils.getGHRepositoryGenerator
import com.takaotech.dashboard.utils.getGHUserGenerator
import com.takaotech.dashboard.utils.getTagsEntityGenerator
import com.takaotech.dashboard.utils.installPostgres
import com.takaotech.dashboard.utils.installRedis
import io.github.serpro69.kfaker.Faker
import io.kotest.common.DelicateKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.datatest.withData
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.distinct
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.ktor.util.logging.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkObject
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import kotlin.math.abs
import kotlin.reflect.jvm.jvmName
import io.github.serpro69.kfaker.lorem.Faker as FakerLorem

@OptIn(DelicateKotest::class)
class DepositoryRepositoryTest : FunSpec() {
    override fun testCaseOrder(): TestCaseOrder = TestCaseOrder.Sequential
    override fun extensions() =
        listOf(
            KoinExtension(getBaseTestKoin()),
        )

    private val logger = KtorSimpleLogger(this::class.jvmName)
    private val faker = Faker()
    private val fakerLorem = FakerLorem()
    private val timestamp = Clock.System.now()

    val languagesGenerator: (languageNameModifier: GHLanguageNameModifier) -> Arb<String> = {
        getGHLanguagesGenerator(it).distinct()
    }
    val ghUserGenerator = getGHUserGenerator().distinct()
    val githubColorController = mockk<GithubColorControllerImpl>()

    lateinit var database: HikariDatabase
    lateinit var depositoryRepository: DepositoryRepository

    lateinit var dbConfiguration: DbConfiguration

    init {
        val redis = installRedis()

        val postgres = installPostgres()

        beforeSpec {
            dbConfiguration = getDbConfiguration(
                postgres,
                redis.redisURI
            )


            database = HikariDatabase(
                dbConfiguration.sqlDbConfiguration,
                logger,
            ).also {
                it.connect()
            }

            depositoryRepository = DepositoryRepository(
                database,
                RedisDatabase(
                    dbConfiguration.redisConfiguration
                ).also {
                    runBlocking {
                        it.connect()
                    }
                },
                logger,
                githubColorController
            )
        }

        beforeContainer {
            database.dbExec {
                addLogger(StdOutSqlLogger)
                SchemaUtils.drop(*dbTables)
                SchemaUtils.create(*dbTables)
                commit()
            }
        }

        context("Save Data in DB") {
            val languages = Arb.list(
                languagesGenerator(GHLanguageNameModifier.WITHOUT_KOTLIN),
                2..20
            ).next()
                .map { language ->
                    GHLanguageDao(
                        name = language,
                        lines = abs(faker.random.nextLong()),
                        weight = abs(faker.random.nextFloat()),
                        colorCode = getGHLanguagesColor(language),
                    )
                }
            val users = MutableList(3) {
                ghUserGenerator.next()
            }.toMutableList()
            val ghRepositoryGenerator: Arb<GHRepositoryDao> = getGHRepositoryGenerator(
                ghUserDaos = users,
                languages = languages,
                updatedAt = timestamp,
                tags = listOf(),
                mainCategory = MainCategory.NONE
            ).distinct()
            var ghRepositories = MutableList(5) {
                ghRepositoryGenerator.next()
            }

            test("Fresh Insert") {
                val spyDepositoryRepository = spyk(depositoryRepository, recordPrivateCalls = true)

                val languageSlot = slot<String>()

                coEvery {
                    githubColorController.getColorLanguageByName(capture(languageSlot))
                } answers {
                    getGHLanguagesColor(languageSlot.captured)
                }

                spyDepositoryRepository.saveRepositoriesToDB(timestamp, ghRepositories)

                coVerify(exactly = ghRepositories.map { it.user }.distinctBy { it.id }.size) {
                    spyDepositoryRepository.updateOrCreateGHUser(any())
                }

                coVerify(exactly = ghRepositories.size) {
                    spyDepositoryRepository.updateOrCreateGHRepository(any(), any(), any())
                }
            }

            test("check data inserted") {
                val outputRepositories = depositoryRepository.getGHRepository()

                outputRepositories.forEach { outRepo ->
                    outRepo shouldBe ghRepositories.first { it.id == outRepo.id }
                }

            }

            test("WHEN Update User field ARE changed User in db with same id") {
                val oldUser = users.first()
                val newUser = oldUser.copy(
                    name = faker.name.name()
                )

                ghRepositories = ghRepositories.map {
                    if (it.user.id == oldUser.id) {
                        it.copy(
                            user = newUser
                        )
                    } else {
                        it
                    }
                }.toMutableList()

                depositoryRepository.updateOrCreateGHUser(newUser)

                val outputRepositories = depositoryRepository.getGHRepository()

                outputRepositories.forEach { outRepo ->
                    outRepo shouldBe ghRepositories.first { it.id == outRepo.id }
                }

                users.indexOfFirst { it.id == newUser.id }.let {
                    users[it] = newUser
                }
            }

            test("WHEN Create GHRepository with Kotlin languages, Kotlin Category Assigned") {
                val spyDepositoryRepository = spyk(depositoryRepository, recordPrivateCalls = true)

                val kotlinRepositoryForSave = ghRepositoryGenerator.next()
                    .copy(
                        languages = generateLanguages(5, "Kotlin", languageModifier = GHLanguageLinesModifier.MAX)
                    )

                spyDepositoryRepository.saveRepositoriesToDB(timestamp, listOf(kotlinRepositoryForSave))

                coVerify(exactly = 1) {
                    spyDepositoryRepository.updateOrCreateGHUser(any())
                }

                coVerify(exactly = 1) {
                    spyDepositoryRepository.updateOrCreateGHRepository(any(), any(), any())
                }

                depositoryRepository.getGHRepository().first {
                    it.id == kotlinRepositoryForSave.id
                }.also {
                    val kotlinRepositoryForEq = with(kotlinRepositoryForSave) {
                        copy(
                            mainCategory = MainCategory.KOTLIN,
                            languages = this.languages.map { color ->
                                color.copy(
                                    colorCode = getGHLanguagesColor(color.name)
                                )
                            }
                        )
                    }

                    it shouldBe kotlinRepositoryForEq

                    // Append new repo test
                    ghRepositories.add(kotlinRepositoryForEq)
                }
            }

            test("WHEN Update GHRepository, GHRepository is updated") {
                val spyDepositoryRepository = spyk(depositoryRepository, recordPrivateCalls = true)

                ghRepositories = ghRepositories.mapIndexed { index, ghRepositoryDao ->
                    if (index == 0) {
                        ghRepositoryDao.copy(
                            description = fakerLorem.lorem.words()
                        )
                    } else {
                        ghRepositoryDao
                    }
                }.toMutableList()

                val ghRepositoryForSave = ghRepositories.first()

                spyDepositoryRepository.saveRepositoriesToDB(timestamp, listOf(ghRepositoryForSave))

                coVerify(exactly = 1) {
                    spyDepositoryRepository.updateOrCreateGHUser(any())
                }

                coVerify(exactly = 1) {
                    spyDepositoryRepository.updateOrCreateGHRepository(any(), any(), any())
                }

                depositoryRepository.getGHRepository().first {
                    it.id == ghRepositoryForSave.id
                }.also {
                    it shouldBe ghRepositoryForSave
                }
            }

            test("Get repository by ID exist RETURN repository") {
                ghRepositories.forEach {
                    depositoryRepository.getGHRepositoryById(it.id) shouldBe it
                }
            }

            test("Get repository by not exist ID RETURN null") {
                depositoryRepository.getGHRepositoryById(123) shouldBe null
            }

            test("IS repository EXIST by ID") {
                ghRepositories.forEach {
                    depositoryRepository.ghRepositoryExist(it.id) shouldBe true
                }
            }

            test("IS repository NOT exist by ID") {
                depositoryRepository.ghRepositoryExist(123) shouldBe false
            }

            test("WHEN get GHRepository Without Category IS all equals") {
                val recoveredRepositories = depositoryRepository.getGHRepository()
                    .sortedBy { it.id }

                recoveredRepositories shouldBe ghRepositories.sortedBy { it.id }
            }

            test("WHEN get GHRepository with Category KOTLIN IS equals") {
                val recoveredRepositories = depositoryRepository.getGHRepository(
                    MainCategory.KOTLIN
                ).sortedBy { it.id }

                recoveredRepositories shouldBe ghRepositories.sortedBy { it.id }
                    .filter { it.mainCategory == MainCategory.KOTLIN }
            }

        }

        context("Categories") {
            val ghUser = ghUserGenerator.next()
            var ghRepository = getGHRepositoryGenerator(
                ghUserDaos = listOf(ghUser),
                languages = listOf(),
                updatedAt = timestamp,
                tags = listOf(),
                mainCategory = MainCategory.NONE
            ).distinct().next()

            depositoryRepository.saveRepositoriesToDB(timestamp, listOf(ghRepository))

            withData(MainCategory.entries) { category ->
                ghRepository = ghRepository.copy(
                    mainCategory = category
                )

                depositoryRepository.updateGhRepositoryMainCategory(ghRepository.id, category)

                depositoryRepository.getGHRepository(category).first() shouldBe ghRepository
            }

            test("Update category at nothing") {
                depositoryRepository.updateGhRepositoryMainCategory(123, MainCategory.KOTLIN)

                depositoryRepository.getGHRepositoryById(123)?.mainCategory shouldBe null
            }

        }

        context("Tags") {
            val ghUser = ghUserGenerator.next()
            val ghRepository = getGHRepositoryGenerator(
                ghUserDaos = listOf(ghUser),
                languages = listOf(),
                updatedAt = timestamp,
                tags = listOf(),
                mainCategory = MainCategory.NONE
            ).distinct().next()
            val tagsRepository = TagsRepository(database)

            val ghTagEntity = getTagsEntityGenerator().next().let {
                tagsRepository.addTag(it)

                database.dbExec {
                    TagsEntity.all().first()
                }
            }

            val ghTag = tagsRepository.getTagById(ghTagEntity.id.value)!!

            depositoryRepository.saveRepositoriesToDB(timestamp, listOf(ghRepository))


            test("Set tag at repository") {
                depositoryRepository.setTagsAtRepository(ghRepository.id, listOf(ghTagEntity))
            }

            test("Check tag at repository") {
                val ghRepositoryTest = ghRepository.copy(
                    tags = listOf(ghTag)
                )

                depositoryRepository.getGHRepositoryById(ghRepository.id) shouldBe ghRepositoryTest
            }

        }

        beforeContainer {
            mockkObject(Clock.System)
            every { Clock.System.now() } returns timestamp
        }

        afterContainer {
            unmockkObject(Clock.System)
        }

        context("Update TimeStamp") {
            test("Get Empty") {
                depositoryRepository.getUpdateTimestamp() shouldBe null
            }

            test("Detach") {
                depositoryRepository.detachUpdateTimestamp() shouldBe timestamp
            }

            test("Get") {
                depositoryRepository.getUpdateTimestamp() shouldBe timestamp
            }
        }

        context("getGHRepositoryByTag") {
            beforeTest {
                database.dbExec {
                    addLogger(StdOutSqlLogger)
                    SchemaUtils.drop(*dbTables)
                    SchemaUtils.create(*dbTables)
                    commit()
                }
            }

            test("should return empty list when tag does not exist") {
                val result = depositoryRepository.getGHRepositoryByTag(
                    tagId = -1,
                    page = 1,
                    size = 10
                )

                result.data.shouldBe(emptyList())
                result.page.shouldBe(1)
                result.totalPage.shouldBe(0)
            }

            test("should return empty list when tag exists but has no repositories") {
                val tag = getTagsEntityGenerator().next()
                database.dbExec {
                    TagsEntity.new {
                        name = tag.name
                        description = tag.description
                    }
                }

                val result = depositoryRepository.getGHRepositoryByTag(
                    tagId = 1,
                    page = 1,
                    size = 10
                )

                result.data.shouldBe(emptyList())
                result.page.shouldBe(1)
                result.totalPage.shouldBe(0)
            }

            test("should handle empty repository list") {
                val tag = getTagsEntityGenerator().next()
                val tagId = database.dbExec {
                    val createdTag = TagsEntity.new {
                        name = tag.name
                        description = tag.description
                    }
                    createdTag.id.value
                }

                val result = depositoryRepository.getGHRepositoryByTag(
                    tagId = tagId,
                    page = 1,
                    size = 10
                )

                result.data.shouldBe(emptyList())
                result.page.shouldBe(1)
                result.totalPage.shouldBe(0)
            }

            test("should handle invalid page number") {
                val users = MutableList(3) { ghUserGenerator.next() }
                val ghRepositoryGenerator = getGHRepositoryGenerator(
                    ghUserDaos = users,
                    languages = emptyList(),
                    updatedAt = timestamp,
                    tags = listOf(),
                    mainCategory = MainCategory.KOTLIN
                ).distinct()

                val repositories = MutableList(5) { ghRepositoryGenerator.next() }
                val tag = getTagsEntityGenerator().next()

                // Save repositories to DB
                depositoryRepository.saveRepositoriesToDB(timestamp, repositories)

                // Create and associate tag
                val tagId = database.dbExec {
                    val createdTag = TagsEntity.new {
                        name = tag.name
                        description = tag.description
                    }

                    GithubDepositoryEntity.all().forEach { repo ->
                        repo.tags = SizedCollection(listOf(createdTag))
                    }

                    createdTag.id.value
                }

                val result = depositoryRepository.getGHRepositoryByTag(
                    tagId = tagId,
                    page = 999,
                    size = 10
                )

                result.data.shouldBe(emptyList())
                result.page.shouldBe(999)
                // Total page calculation is 0
                result.totalPage.shouldBe(1)
            }

            test("should return repositories associated with tag") {
                val users = MutableList(3) { ghUserGenerator.next() }
                val ghRepositoryGenerator = getGHRepositoryGenerator(
                    ghUserDaos = users,
                    languages = emptyList(),
                    updatedAt = timestamp,
                    tags = listOf(),
                    mainCategory = MainCategory.KOTLIN
                ).distinct()

                val repositories = MutableList(5) { ghRepositoryGenerator.next() }
                val tag = getTagsEntityGenerator().next()

                // Save repositories to DB
                depositoryRepository.saveRepositoriesToDB(timestamp, repositories)

                // Create tag and associate repositories
                val tagId = database.dbExec {
                    // Create tag
                    val createdTag = TagsEntity.new {
                        name = tag.name
                        description = tag.description
                    }

                    // Associate repositories with tag through the join table
                    GithubDepositoryEntity.all().forEach { repo ->
                        repo.tags = SizedCollection(listOf(createdTag))
                    }

                    createdTag.id.value
                }

                // Test first page
                val result = depositoryRepository.getGHRepositoryByTag(
                    tagId = tagId,
                    page = 1,
                    size = 3
                )

                result.data.size.shouldBe(3)
                result.page.shouldBe(1)
                result.totalPage.shouldBe(2)

                // Test second page
                val secondPageResult = depositoryRepository.getGHRepositoryByTag(
                    tagId = 1,
                    page = 2,
                    size = 3
                )

                secondPageResult.data.size.shouldBe(2)
                secondPageResult.page.shouldBe(2)
                secondPageResult.totalPage.shouldBe(2)
            }
        }
    }
}
