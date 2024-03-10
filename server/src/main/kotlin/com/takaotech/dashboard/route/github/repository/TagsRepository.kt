package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.model.TagDao
import com.takaotech.dashboard.model.TagNewDao
import com.takaotech.dashboard.route.github.data.TagsEntity
import com.takaotech.dashboard.route.github.repository.utils.convertToTagDao
import com.takaotech.dashboard.utils.HikariDatabase
import org.koin.core.annotation.Factory

@Factory
class TagsRepository(private val database: HikariDatabase) {

	suspend fun addTag(tag: TagNewDao) {
		database.dbExec {
			TagsEntity.new {
				name = tag.name
				description = tag.description
				color = tag.color
			}
		}
	}

	suspend fun updateTag(tag: TagDao) {
		database.dbExec {
			TagsEntity.findById(tag.id)?.let {
				it.name = tag.name
				it.description = tag.description
				it.color = tag.color
			}
		}
	}

	suspend fun getTags(): List<TagDao> {
		return database.dbExec {
			TagsEntity.all()
				.toList().map {
					it.convertToTagDao()
				}
		}
	}

	suspend fun removeTag(tagId: Int) {
		database.dbExec {
			TagsEntity.findById(tagId)?.delete()
		}
	}

	suspend fun getTagById(id: Int): TagDao? {
		return database.dbExec {
			TagsEntity.findById(id)?.convertToTagDao()
		}
	}
}