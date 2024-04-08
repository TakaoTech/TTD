package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.model.github.TagNewDao
import com.takaotech.dashboard.route.github.data.TagsEntity
import com.takaotech.dashboard.utils.HikariDatabase
import com.takaotech.dashboard.utils.dbTables
import com.takaotech.dashboard.utils.getDbConfiguration
import io.kotest.core.spec.style.FunSpec
import io.ktor.util.logging.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import kotlin.reflect.jvm.jvmName
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class TagsRepositoryTest : FunSpec() {
	init {
		val logger = KtorSimpleLogger(this::class.jvmName)

		val dbConfiguration = getDbConfiguration()
		val database = HikariDatabase(
			dbConfiguration,
			logger
		)

		val tagsRepository = TagsRepository(database)

		beforeEach {
			database.dbExec {
				addLogger(StdOutSqlLogger)
				SchemaUtils.drop(*dbTables)
				SchemaUtils.create(*dbTables)
				commit()
			}
		}

		test("Title only") {
			val tagInput = TagNewDao(name = "Kotlin Official")
			tagsRepository.addTag(tagInput)

			val tagListOutput = database.dbExec {
				TagsEntity.all().toList()
			}

			assertTrue(tagListOutput.isNotEmpty())
			val tagOutput = tagListOutput.first()
			assertEquals(tagInput.name, tagOutput.name)
			assertEquals(tagInput.description, tagOutput.description)
			assertEquals(tagInput.color, tagOutput.color)

		}

		test("Add&Get Tag No Paging") {
			val testList = listOf(
				TagNewDao("Kotlin Official"),
				TagNewDao("Recommended", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
				TagNewDao(
					"Strange",
					"Proident obcaecat ea in duis ut consequat laoreet aliquip eum excepteur",
					"#123456"
				)
			)

			testList.forEach {
				tagsRepository.addTag(it)
			}

			val outputTags = tagsRepository.getTags(null, null)

			outputTags.forEachIndexed { index, outputTag ->
				val inputTag = testList[index]
				assertEquals(inputTag.name, outputTag.name)
				assertEquals(inputTag.description, outputTag.description)
				assertEquals(inputTag.color, outputTag.color)
			}
		}

		test("Add&Get Tag Paging") {
			val pageSize = 2

			val testList = listOf(
				TagNewDao("Kotlin Official"),
				TagNewDao("Recommended", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
				TagNewDao(
					"Strange",
					"Proident obcaecat ea in duis ut consequat laoreet aliquip eum excepteur",
					"#123456"
				)
			)

			testList.forEach {
				tagsRepository.addTag(it)
			}

			val outputTags = tagsRepository.getTags(1, pageSize)

			outputTags.forEachIndexed { index, outputTag ->
				val inputTag = testList[index]
				assertEquals(inputTag.name, outputTag.name)
				assertEquals(inputTag.description, outputTag.description)
				assertEquals(inputTag.color, outputTag.color)
			}

			assertEquals(pageSize, outputTags.size)

		}

		test("Add&Get&Remove Tag") {
			val tagRemovedTest = TagNewDao("Recommended")

			val testList = listOf(TagNewDao("Kotlin Official"), tagRemovedTest)

			testList.forEach {
				tagsRepository.addTag(it)
			}

			val tags = tagsRepository.getTags(null, null)

			tags.forEachIndexed { index, outputTag ->
				val inputTag = testList[index]
				assertEquals(inputTag.name, outputTag.name)
				assertEquals(inputTag.description, outputTag.description)
				assertEquals(inputTag.color, outputTag.color)
			}

			tagsRepository.removeTag(tags.find { it.name == tagRemovedTest.name }!!.id)

			val tags2 = tagsRepository.getTags(null, null)

			assertTrue { tags2.find { it.name == tagRemovedTest.name } == null }

		}

		test("getTagById") {
			val tagForNew =
				TagNewDao("Recommended", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", color = "#123456")

			database.dbExec {
				TagsEntity.new(123) {
					name = tagForNew.name
					description = tagForNew.description
					color = tagForNew.color
				}
			}

			val tagOutput = tagsRepository.getTagById(123)

			assertTrue { tagOutput != null }
			assertEquals(tagForNew.name, tagOutput?.name)
			assertEquals(tagForNew.description, tagOutput?.description)
			assertEquals(tagForNew.color, tagOutput?.color)

		}

		test("getTagByIdInternal") {
			val tagForNew =
				TagNewDao("Recommended", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", color = "#123456")

			database.dbExec {
				TagsEntity.new(123) {
					name = tagForNew.name
					description = tagForNew.description
					color = tagForNew.color
				}
			}

			val tagOutput = tagsRepository.getTagByIdInternal(123)

			assertTrue { tagOutput != null }
			assertEquals(tagForNew.name, tagOutput?.name)
			assertEquals(tagForNew.description, tagOutput?.description)
			assertEquals(tagForNew.color, tagOutput?.color)

		}

		test("Update Tag") {
			val tagForUpdate = TagNewDao("Recommended", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.")


			val testList = listOf(
				TagNewDao("Kotlin Official"),
				tagForUpdate,
				TagNewDao(
					"Strange",
					"Proident obcaecat ea in duis ut consequat laoreet aliquip eum excepteur",
					"#123456"
				)
			)

			testList.forEach {
				tagsRepository.addTag(it)
			}

			val outputTags = tagsRepository.getTags(null, null)

			outputTags.forEachIndexed { index, outputTag ->
				val inputTag = testList[index]
				assertEquals(inputTag.name, outputTag.name)
				assertEquals(inputTag.description, outputTag.description)
				assertEquals(inputTag.color, outputTag.color)
			}

			tagsRepository.getTags(null, null).find {
				it.name == tagForUpdate.name
			}?.let {
				val updatedTag = it.copy(description = "Description Updated")
				tagsRepository.updateTag(updatedTag)
				updatedTag to tagsRepository.getTags(null, null).find { it.name == updatedTag.name }
			}?.let {
				assertEquals(it.first.id, it.second?.id)
				assertEquals(it.first.name, it.second?.name)
				assertEquals(tagForUpdate.name, it.second?.name)
				assertEquals(it.first.description, it.second?.description)
				assertNotEquals(tagForUpdate.description, it.second?.description)
			}

		}
	}
}