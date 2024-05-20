package com.takaotech.dashboard.route.administration.data.role

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class RoleEntity(id: EntityID<TakaoRole>) : Entity<TakaoRole>(id) {
    companion object : EntityClass<TakaoRole, RoleEntity>(RoleTable)

}