package com.takaotech.dashboard.route.administration.data.user

import com.takaotech.dashboard.utils.tables.StringIdTable
import org.jetbrains.exposed.sql.Column

object UserTable: StringIdTable(){
    override val primaryKey: PrimaryKey = PrimaryKey(id)

    /**
     * Display Name
     */
    val displayName: Column<String> = varchar("displayName", 200)

    val profileImageUrl : Column<String> = text("profileImageUrl")
}