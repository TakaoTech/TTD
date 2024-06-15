package com.takaotech.dashboard.route.administration.data.role

import com.takaotech.dashboard.model.role.TakaoRole
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object RoleTable : IdTable<TakaoRole>() {
    override val id: Column<EntityID<TakaoRole>> = enumerationByName<TakaoRole>("ROLE_ID", 50).entityId()
    override val primaryKey: PrimaryKey = PrimaryKey(id)

}