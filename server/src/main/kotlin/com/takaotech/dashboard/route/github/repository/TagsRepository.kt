package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.route.github.data.TagsEntity
import com.takaotech.dashboard.utils.HikariDatabase
import org.koin.core.annotation.Factory

@Factory
class TagsRepository(private val database: HikariDatabase) {

	suspend fun addTag(tagName: String) {
		database.dbExec {
			TagsEntity.new(id = tagName) {

			}
		}
	}

	suspend fun getTags(): List<TagsEntity> {
		return database.dbExec {
			TagsEntity.all()
				.toList()
		}
	}
}