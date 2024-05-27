package com.takaotech.dashboard.route.administration.data.user

import com.takaotech.dashboard.route.administration.data.UserRoleTable
import com.takaotech.dashboard.route.administration.data.role.RoleEntity
import com.takaotech.dashboard.utils.tables.StringEntity
import com.takaotech.dashboard.utils.tables.StringEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserEntity(id: EntityID<String>) : StringEntity(id) {
    companion object : StringEntityClass<UserEntity>(UserTable)

    var email by UserTable.email
    var displayName by UserTable.displayName
    var profileImage by UserTable.profileImageUrl
    var roles by RoleEntity via UserRoleTable
}