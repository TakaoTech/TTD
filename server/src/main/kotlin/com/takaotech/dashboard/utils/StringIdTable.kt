package com.takaotech.dashboard.utils

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

//copy of https://github.com/vsouhrada/kotlin-exposed-demo/blob/master/src/main/kotlin/com/github/vsouhrada/kotlin/exposed_demo/dao/StringIdTable.kt
open class StringIdTable(name: String = "", idColumnName: String = "id", idColumnSize: Int = 32) :
	IdTable<String>(name) {
	override val id: Column<EntityID<String>> = varchar(idColumnName, idColumnSize).entityId()
}

abstract class StringEntity(id: EntityID<String>) : Entity<String>(id)

abstract class StringEntityClass<E : StringEntity>(table: IdTable<String>) : EntityClass<String, E>(table)