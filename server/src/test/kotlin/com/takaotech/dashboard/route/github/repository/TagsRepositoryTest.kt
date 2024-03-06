package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.di.connectToDatabase
import com.takaotech.dashboard.route.github.data.TagsTable
import com.takaotech.dashboard.utils.HikariDatabase
import com.takaotech.dashboard.utils.dbTables
import com.takaotech.dashboard.utils.getBaseTestKoin
import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.selectAll
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TagsRepositoryTest : FunSpec(), KoinTest {
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

		test("Add Tag") {
			val tagsRepository by inject<TagsRepository>()
			val database by inject<HikariDatabase>()
			tagsRepository.addTag("Kotlin Official")

			val isNotEmpty = database.dbExec {
				TagsTable.selectAll().toList().isNotEmpty()
			}

			assertTrue(isNotEmpty)

		}

		test("Add&Get Tag") {
			val tagsRepository by inject<TagsRepository>()

			val testList = listOf("Kotlin Official", "Recommended")

			tagsRepository.addTag(testList[0])
			tagsRepository.addTag(testList[1])

			val tags = tagsRepository.getTags()

			assertEquals(testList, tags.map { it.id.value })

		}
	}
}