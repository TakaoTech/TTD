package com.takaotech.dashboard.route.github.data

import com.takaotech.dashboard.utils.StringEntity
import com.takaotech.dashboard.utils.StringEntityClass
import com.takaotech.dashboard.utils.StringIdTable
import org.jetbrains.exposed.dao.id.EntityID

object TagsTable : StringIdTable(name = "tagName") {

}

class TagsEntity(id: EntityID<String>) : StringEntity(id) {
	companion object : StringEntityClass<TagsEntity>(TagsTable)
}