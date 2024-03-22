package com.takaotech.dashboard.route.github.data

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object GithubUserTable : IdTable<Long>() {
	override val id: Column<EntityID<Long>> = long("id").entityId()
	val name: Column<String> = text("name")
	val url: Column<String> = text("url")
	val avatarUrl: Column<String?> = text("avatarUrl").nullable()

	override val primaryKey = PrimaryKey(id)
}

class GithubUserEntity(id: EntityID<Long>) : LongEntity(id) {
	companion object : LongEntityClass<GithubUserEntity>(GithubUserTable)

	var name by GithubUserTable.name
	var url by GithubUserTable.url
	var avatarUrl by GithubUserTable.avatarUrl
}