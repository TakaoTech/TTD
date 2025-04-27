package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.models.TagNewDao
import com.takaotech.dashboard.route.github.data.TagsEntity
import com.takaotech.dashboard.utils.HikariDatabase
import com.takaotech.dashboard.utils.dbTables
import com.takaotech.dashboard.utils.getSqlDbConfiguration
import com.takaotech.dashboard.utils.installPostgres
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.util.logging.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import kotlin.reflect.jvm.jvmName

class TagsRepositoryTest : BehaviorSpec({
    val logger = KtorSimpleLogger(TagsRepositoryTest::class.jvmName)
    val postgres = installPostgres()
    val dbConfiguration = getSqlDbConfiguration(postgres)
    val database = HikariDatabase(
        dbConfiguration,
        logger,
    ).also {
        it.connect()
    }

    val tagsRepository = TagsRepository(database)

    beforeContainer {
        database.dbExec {
            addLogger(StdOutSqlLogger)
            SchemaUtils.drop(*dbTables)
            SchemaUtils.create(*dbTables)
            commit()
        }
    }

    Given("tag repository operations") {
        When("adding a tag with only a name") {
            val tagInput = TagNewDao(name = "Kotlin Official")
            tagsRepository.addTag(tagInput)

            Then("tag should be saved with correct properties") {
                val tagListOutput = database.dbExec {
                    TagsEntity.all().toList()
                }

                tagListOutput.isEmpty() shouldBe false
                val tagOutput = tagListOutput.first()
                tagOutput.name shouldBe tagInput.name
                tagOutput.description shouldBe tagInput.description
                tagOutput.color shouldBe tagInput.color
            }
        }

        When("adding multiple tags and retrieving without pagination") {
            val testList = listOf(
                TagNewDao("Kotlin Official"),
                TagNewDao("Recommended", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
                TagNewDao(
                    "Strange",
                    "Proident obcaecat ea in duis ut consequat laoreet aliquip eum excepteur",
                    "#123456",
                ),
            )

            testList.forEach {
                tagsRepository.addTag(it)
            }

            Then("all tags should be retrieved with correct properties") {
                val outputTags = tagsRepository.getTags(null, null).data

                outputTags.forEachIndexed { index, outputTag ->
                    val inputTag = testList[index]
                    outputTag.name shouldBe inputTag.name
                    outputTag.description shouldBe inputTag.description
                    outputTag.color shouldBe inputTag.color
                }
            }
        }

        When("adding multiple tags and retrieving with pagination") {
            val pageSize = 2
            val testList = listOf(
                TagNewDao("Kotlin Official"),
                TagNewDao("Recommended", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
                TagNewDao(
                    "Strange",
                    "Proident obcaecat ea in duis ut consequat laoreet aliquip eum excepteur",
                    "#123456",
                ),
            )

            testList.forEach {
                tagsRepository.addTag(it)
            }

            Then("should return correct number of tags for the specified page") {
                val outputTags = tagsRepository.getTags(1, pageSize).data

                outputTags.forEachIndexed { index, outputTag ->
                    val inputTag = testList[index]
                    outputTag.name shouldBe inputTag.name
                    outputTag.description shouldBe inputTag.description
                    outputTag.color shouldBe inputTag.color
                }

                outputTags.size shouldBe pageSize
            }
        }

        When("removing a tag") {
            val tagRemovedTest = TagNewDao("Recommended")
            val testList = listOf(TagNewDao("Kotlin Official"), tagRemovedTest)

            testList.forEach {
                tagsRepository.addTag(it)
            }

            val tags = tagsRepository.getTags(null, null).data
            tags.forEachIndexed { index, outputTag ->
                val inputTag = testList[index]
                outputTag.name shouldBe inputTag.name
                outputTag.description shouldBe inputTag.description
                outputTag.color shouldBe inputTag.color
            }

            val tagToRemove = tags.find { it.name == tagRemovedTest.name }!!
            tagsRepository.removeTag(tagToRemove.id)

            Then("removed tag should no longer be present") {
                val tags2 = tagsRepository.getTags(null, null).data
                tags2.find { it.name == tagRemovedTest.name } shouldBe null
            }
        }
    }

    Given("tag retrieval operations") {
        When("retrieving a tag by ID") {
            val tagForNew = TagNewDao(
                "Recommended",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                color = "#123456"
            )

            database.dbExec {
                TagsEntity.new(123) {
                    name = tagForNew.name
                    description = tagForNew.description
                    color = tagForNew.color
                }
            }

            Then("should return the correct tag") {
                val tagOutput = tagsRepository.getTagById(123)

                tagOutput shouldNotBe null
                tagOutput?.name shouldBe tagForNew.name
                tagOutput?.description shouldBe tagForNew.description
                tagOutput?.color shouldBe tagForNew.color
            }
        }

        When("retrieving a tag by ID using internal method") {
            val tagForNew = TagNewDao(
                "Recommended",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                color = "#123456"
            )

            database.dbExec {
                TagsEntity.new(123) {
                    name = tagForNew.name
                    description = tagForNew.description
                    color = tagForNew.color
                }
            }

            Then("should return the correct tag") {
                val tagOutput = tagsRepository.getTagByIdInternal(123)

                tagOutput shouldNotBe null
                tagOutput?.name shouldBe tagForNew.name
                tagOutput?.description shouldBe tagForNew.description
                tagOutput?.color shouldBe tagForNew.color
            }
        }
    }

    Given("tag update operations") {
        When("updating a tag's description") {
            val tagForUpdate = TagNewDao("Recommended", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.")

            val testList = listOf(
                TagNewDao("Kotlin Official"),
                tagForUpdate,
                TagNewDao(
                    "Strange",
                    "Proident obcaecat ea in duis ut consequat laoreet aliquip eum excepteur",
                    "#123456",
                ),
            )

            testList.forEach {
                tagsRepository.addTag(it)
            }

            val outputTags = tagsRepository.getTags(null, null).data

            outputTags.forEachIndexed { index, outputTag ->
                val inputTag = testList[index]
                outputTag.name shouldBe inputTag.name
                outputTag.description shouldBe inputTag.description
                outputTag.color shouldBe inputTag.color
            }

            val tagToUpdate = outputTags.find { it.name == tagForUpdate.name }!!
            val updatedTag = tagToUpdate.copy(description = "Description Updated")
            tagsRepository.updateTag(updatedTag)

            Then("tag should be updated with new description") {
                val retrievedTag = tagsRepository.getTags(null, null).data.find { it.name == updatedTag.name }

                retrievedTag shouldNotBe null
                retrievedTag?.id shouldBe updatedTag.id
                retrievedTag?.name shouldBe updatedTag.name
                retrievedTag?.name shouldBe tagForUpdate.name
                retrievedTag?.description shouldBe updatedTag.description
                retrievedTag?.description shouldNotBe tagForUpdate.description
            }
        }
    }
})
