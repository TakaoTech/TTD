package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.route.github.data.TagsEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Factory

@Factory
class TagsRepository(private val database: Database) {

	fun addTag(tagName: String) {
		transaction(database) {
			TagsEntity.new(id = tagName) { }
		}
	}

	fun getTags(): List<TagsEntity> {
		return transaction(database) {
			TagsEntity.all()
				.toList()
		}
	}
}