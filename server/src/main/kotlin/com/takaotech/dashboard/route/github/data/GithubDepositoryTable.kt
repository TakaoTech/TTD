package com.takaotech.dashboard.route.github.data

import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.route.github.data.TagsTable.clientDefault
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object GithubDepositoryTable : IdTable<Long>() {
	/**
	 * GitHub Repository Id
	 */
	override val id: Column<EntityID<Long>> = long("id").entityId()

	/**
	 * Repository name
	 */
	val name: Column<String> = varchar("name", 100)

	/**
	 * Full name of repository
	 * <user/organization>/<name repository>
	 */
	val fullName: Column<String> = varchar("full_name", 100)

	/**
	 * Full name of repository
	 * <user/organization>/<name repository>
	 */
	val description: Column<String?> = text("description").nullable()

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
	 *
	 */
	val license = text("license").nullable()

	/**
	 *
	 */
	val licenseUrl = text("license_url").nullable()

	/**
	 * Map of repository programming languages
	 */
	//TODO Use Ktor JSON
	val languages: Column<Map<String, Long>> = json("languages", Json.Default)

	val category: Column<MainCategory> = enumerationByName("category", 20)

	val updatedAt = timestamp("updatedAt")

	override val primaryKey = PrimaryKey(id)
}

class GithubDepositoryEntity(id: EntityID<Long>) : LongEntity(id) {
	companion object : LongEntityClass<GithubDepositoryEntity>(GithubDepositoryTable)

	var name by GithubDepositoryTable.name
	var fullName by GithubDepositoryTable.fullName
	var description by GithubDepositoryTable.description
	var url by GithubDepositoryTable.url
	var user by GithubUserEntity referencedOn GithubDepositoryTable.user

	//val userRef by GithubUserEntity referrersOn GithubUserTable.id
	var languages by GithubDepositoryTable.languages

	//Mitigation because default enu on db isn't currently supported
	var category by GithubDepositoryTable.category.clientDefault { MainCategory.NONE }

	var tags by TagsEntity via GithubDepositoryTagsTable

	var license by GithubDepositoryTable.license
	var licenseUrl by GithubDepositoryTable.licenseUrl

	var updatedAt by GithubDepositoryTable.updatedAt

}