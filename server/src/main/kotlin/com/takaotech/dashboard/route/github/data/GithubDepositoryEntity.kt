package com.takaotech.dashboard.route.github.data

import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.route.github.data.TagsTable.clientDefault
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class GithubDepositoryEntity(id: EntityID<Long>) : LongEntity(id) {
	companion object : LongEntityClass<GithubDepositoryEntity>(GithubDepositoryTable)

	var name by GithubDepositoryTable.name
	var fullName by GithubDepositoryTable.fullName
	var description by GithubDepositoryTable.description
	var url by GithubDepositoryTable.url
	var user by GithubUserEntity referencedOn GithubDepositoryTable.user

	//val userRef by GithubUserEntity referrersOn GithubUserTable.id
	var languages by GithubDepositoryTable.languages

	//Mitigation because default enu on db isn't currently supported
	var category by GithubDepositoryTable.category.clientDefault { MainCategory.NONE }

	var tags by TagsEntity via GithubDepositoryTagsTable

	var license by GithubDepositoryTable.license
	var licenseUrl by GithubDepositoryTable.licenseUrl

	var updatedAt by GithubDepositoryTable.updatedAt

}

class GithubDepositoryMiniEntity(id: EntityID<Long>) : LongEntity(id) {
	companion object : LongEntityClass<GithubDepositoryMiniEntity>(GithubDepositoryTable)

	val category by GithubDepositoryTable.category

	var fullName by GithubDepositoryTable.fullName
	var url by GithubDepositoryTable.url
	var languages by GithubDepositoryTable.languages
	var tags by TagsEntity via GithubDepositoryTagsTable
	var license by GithubDepositoryTable.license
	var updatedAt by GithubDepositoryTable.updatedAt

}