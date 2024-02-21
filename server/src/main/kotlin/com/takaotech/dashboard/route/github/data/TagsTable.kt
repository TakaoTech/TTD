package com.takaotech.dashboard.route.github.data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object TagsTable : IntIdTable() {
	val name: Column<String> = varchar("name", 50)
}

class TagsEntity(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<TagsEntity>(TagsTable)

	var name by TagsTable.name
}