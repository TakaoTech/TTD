package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.model.Tag
import com.takaotech.dashboard.route.github.data.TagsEntity
import com.takaotech.dashboard.utils.HikariDatabase
import org.koin.core.annotation.Factory

@Factory
class TagsRepository(private val database: HikariDatabase) {

	suspend fun addTag(tag: Tag) {
		database.dbExec {
			TagsEntity.new(id = tag.name) {

			}
		}
	}

	suspend fun getTags(): List<Tag> {
		return database.dbExec {
			TagsEntity.all()
				.toList().map {
					Tag(
						name = it.id.value
					)
				}
		}
	}

	suspend fun removeTag(tagName: String) {
		database.dbExec {
			TagsEntity.findById(tagName)?.delete()
		}
	}

	suspend fun getTagById(id: String): Tag? {
		return database.dbExec {
			TagsEntity.findById(id)?.let {
				Tag(
					name = it.id.value
				)
			}
		}
	}
}