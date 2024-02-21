package com.takaotech.dashboard.route.github.data

import com.sun.tools.javac.Main
import com.takaotech.dashboard.route.github.data.TagsTable.clientDefault
import com.takaotech.dashboard.route.github.data.TagsTable.default
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.json.json

object GithubDepositoryTable : IdTable<Long>() {
	/**
	 * GitHub Repository Id
	 */
	override val id: Column<EntityID<Long>> = long("id").entityId()

	/**
	 * Repository name
	 */
	val name: Column<String> = text("name")

	/**
	 * Full name of repository
	 * <user/organization>/<name repository>
	 */
	val fullName: Column<String> = text("full_name")

	/**
	 * Url of repository
	 */
	val url: Column<String> = text("url")

	//    val user: Column<EntityID<Long>> = long("user_id").entityId() references GithubUserTable.id

	/**
	 * User/Organization of repository
	 */
	val user: Column<EntityID<Long>> = reference("user_id", GithubUserTable)

	/**
	 * Map of repository programming languages
	 */
	//TODO Use Ktor JSON
	val languages: Column<Map<String, Long>> = json("languages", Json.Default)

	val category: Column<MainCategory> = enumerationByName("category", 50)

	override val primaryKey = PrimaryKey(id)
}

class GithubDepositoryEntity(id: EntityID<Long>) : LongEntity(id) {
	companion object : LongEntityClass<GithubDepositoryEntity>(GithubDepositoryTable)

	var name by GithubDepositoryTable.name
	var fullName by GithubDepositoryTable.fullName
	var url by GithubDepositoryTable.url
	var user by GithubDepositoryTable.user

	//val userRef by GithubUserEntity referrersOn GithubUserTable.id
	var languages by GithubDepositoryTable.languages

	//Mitigation because default enu on db isn't currently supported
	val category by GithubDepositoryTable.category.clientDefault { MainCategory.NONE }

	var tags by TagsEntity via GithubDepositoryTagsTable

}