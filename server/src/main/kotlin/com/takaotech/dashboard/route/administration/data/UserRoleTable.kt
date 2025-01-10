package com.takaotech.dashboard.route.administration.data

import com.takaotech.dashboard.route.administration.data.role.RoleTable
import com.takaotech.dashboard.route.administration.data.user.UserTable
import org.jetbrains.exposed.sql.Table

object UserRoleTable : Table() {
    val user = reference("kotlin_user", UserTable)
    val role = reference("role", RoleTable)

    override val primaryKey = PrimaryKey(user, role, name = "PK_USER_ROLE")
}
