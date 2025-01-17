package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.model.github.GHLanguageDao
import com.takaotech.dashboard.model.github.GHRepositoryDao
import com.takaotech.dashboard.model.github.GHUser
import com.takaotech.dashboard.model.github.MainCategory
import com.takaotech.dashboard.utils.*
import io.github.serpro69.kfaker.Faker
import io.kotest.common.DelicateKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.distinct
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.ktor.util.logging.*
import io.mockk.*
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import java.util.*
import kotlin.math.abs
import kotlin.reflect.jvm.jvmName

@OptIn(DelicateKotest::class)
class DepositoryRepositoryTest2 : FunSpec() {
    override fun testCaseOrder(): TestCaseOrder = TestCaseOrder.Sequential
    override fun extensions() =
        listOf(
            KoinExtension(getBaseTestKoin()),
        )

    private val logger = KtorSimpleLogger(this::class.jvmName)
    private val faker = Faker()
    private val timestamp = Clock.System.now()
    private val date = Date()

    //    val testResourcePath = Paths.get("").toAbsolutePath().toString() + "/src/test/resources/"
//    val colors = Json.parseToJsonElement(File(testResourcePath, "githubColors.json").readText()).jsonObject
    val languagesGenerator = getGHLanguagesGenerator().distinct()
    val ghUserGenerator = getGHUserGenerator().distinct()
    val githubColorController = mockk<GithubColorControllerImpl>()

    lateinit var database: HikariDatabase
    lateinit var depositoryRepository: DepositoryRepository

    //    lateinit var redisDatabase: RedisDatabase
    lateinit var dbConfiguration: DbConfiguration

    init {
//        val redis = install(
//            ContainerExtension(
//                container = RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG)),
//                mode = ContainerLifecycleMode.Spec,
//            ),
//        ) {
//            dbConfiguration = getDbConfiguration(redisURI)
//            redisDatabase = RedisDatabase(
//                dbConfiguration.redisConfiguration
//            ).also {
////                runBlocking {
////                    it.connect()
//////                if(it.client.isDisconnected){
//////                    throw Exception("Redis not connected")
//////                }
////                }
//            }
//        }


        beforeSpec {
            dbConfiguration = getDbConfiguration("")
            database = HikariDatabase(
                dbConfiguration.sqlDbConfiguration,
                logger,
            ).also {
                it.connect()
            }

            depositoryRepository = DepositoryRepository(
                database,
                mockk(relaxed = true),
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
            val languages = Arb.list(languagesGenerator, 2..20).next().map { language ->
                GHLanguageDao(
                    name = language,
                    lines = abs(faker.random.nextLong()),
                    weight = abs(faker.random.nextFloat()),
                    colorCode = getGHLanguagesColorsGenerator(language),
                )
            }
            val users: List<GHUser> = MutableList(3) {
                ghUserGenerator.next()
            }
            val ghRepositoryGenerator: Arb<GHRepositoryDao> = getGHRepositoryGenerator(
                ghUsers = users,
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
                    getGHLanguagesColorsGenerator(languageSlot.captured)
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
            }

            //TODO Test Aggiunta repository Kotlin
            //TODO Test Aggiornamento repository già inserito
            xtest("WHEN Update ")
        }
    }

}