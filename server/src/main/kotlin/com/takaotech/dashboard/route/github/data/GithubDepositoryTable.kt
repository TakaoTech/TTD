package com.takaotech.dashboard.route.github.data

import com.takaotech.dashboard.models.GHLanguageDao
import com.takaotech.dashboard.models.MainCategory
import kotlinx.serialization.json.Json
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
    // TODO Use Ktor JSON
    val languages: Column<List<GHLanguageDao>> = json("languages", Json.Default)

    val category: Column<MainCategory> = enumerationByName("category", 20)

    /**
     *  GitHub repository is Updated at time.
     */
    val updatedAt = timestamp("updatedAt")

    /**
     * Last data refresh
     */
    val refreshedAt = timestamp("refreshedAt")

    /**
     * The Repository is Deprecated because it has been overtaken by another library
     * or out of date
     */
    val deprecated = bool("deprecated").default(false)

    /**
     * Archived
     */
    val archived = bool("archived").default(false)

    override val primaryKey = PrimaryKey(id)
}
