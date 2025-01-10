package com.takaotech.dashboard.route.administration.data.user

import com.takaotech.dashboard.utils.tables.StringIdTable
import org.jetbrains.exposed.sql.Column

object UserTable : StringIdTable(name = "KotlinUser", idColumnSize = 256) {
    override val primaryKey: PrimaryKey = PrimaryKey(id)

    val email: Column<String> = varchar("email", 264)

    /**
     * Display Name
     */
    val displayName: Column<String> = varchar("displayName", 200)

    val profileImageUrl: Column<String> = text("profileImageUrl")
}
