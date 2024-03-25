package com.takaotech.dashboard.route.github.data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object TagsTable : IntIdTable() {

	val name = varchar("tagName", 20)

	/**
	 * The description of tag
	 */
	val description = text("description").nullable()

	val color = varchar("color", 10).nullable()
}

class TagsEntity(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<TagsEntity>(TagsTable)

	var name by TagsTable.name
	var description by TagsTable.description
	var color by TagsTable.color
}

class TagsEntityFollowRepo(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<TagsEntityFollowRepo>(TagsTable)

	var name by TagsTable.name
	var description by TagsTable.description
	var color by TagsTable.color

	val repositories by GithubDepositoryMiniEntity via GithubDepositoryTagsTable
}