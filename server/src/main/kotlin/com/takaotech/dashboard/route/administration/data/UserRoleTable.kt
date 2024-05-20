package com.takaotech.dashboard.route.administration.data

import com.takaotech.dashboard.route.administration.data.role.RoleTable
import com.takaotech.dashboard.route.administration.data.user.UserTable
import org.jetbrains.exposed.sql.Table

object UserRoleTable : Table() {
    val user = reference("user_id", UserTable)
    val role = reference("role_id", RoleTable)

    override val primaryKey = PrimaryKey(user, role)
}