package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.model.TagDao
import com.takaotech.dashboard.model.TagNewDao
import com.takaotech.dashboard.route.github.data.TagsEntity
import com.takaotech.dashboard.utils.HikariDatabase
import org.koin.core.annotation.Factory

@Factory
class TagsRepository(private val database: HikariDatabase) {

	suspend fun addTag(tag: TagNewDao) {
		database.dbExec {
			TagsEntity.new {
				name = tag.name
				description = tag.description
			}
		}
	}

	suspend fun updateTag(tag: TagDao) {
		database.dbExec {
			TagsEntity.findById(tag.id)?.let {
				it.name = tag.name
				it.description = tag.description
			} ?: TagsEntity.new {
				name = tag.name
				description = tag.description
			}
		}
	}

	suspend fun getTags(): List<TagDao> {
		return database.dbExec {
			TagsEntity.all()
				.toList().map {
					TagDao(
						id = it.id.value,
						name = it.name,
						description = it.description
					)
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
			TagsEntity.findById(id)?.let {
				TagDao(
					id = it.id.value,
					name = it.name,
					description = it.description
				)
			}
		}
	}
}