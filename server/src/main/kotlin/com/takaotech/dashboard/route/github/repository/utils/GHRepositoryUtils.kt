package com.takaotech.dashboard.route.github.repository.utils

import com.takaotech.dashboard.model.github.*
import com.takaotech.dashboard.route.github.data.GithubDepositoryEntity
import com.takaotech.dashboard.route.github.data.GithubDepositoryMiniEntity
import com.takaotech.dashboard.route.github.data.TagsEntity
import com.takaotech.dashboard.route.github.repository.GithubColorController
import com.takaotech.dashboard.utils.HikariDatabase
import kotlinx.datetime.toKotlinInstant
import org.kohsuke.github.GHRepository as GHRepositoryExternal

internal suspend fun GithubDepositoryEntity.convertToGHRepository(
	database: HikariDatabase,
	colorController: GithubColorController
): GHRepositoryDao {
	return GHRepositoryDao(
		id = id.value,
		name = name,
		fullName = fullName,
		description = description,
		url = url,
		license = license,
		licenseUrl = licenseUrl,
		user = database.dbExec {
			with(user) {
				GHUser(
					id = id.value,
					name = name,
					url = url,
					avatarUrl = user.avatarUrl
				)
			}
		},
		languages = languages.map {
			it.copy(
				colorCode = colorController.getColorLanguageByName(it.name)
			)
		},
		tags = database.dbExec {
			tags.map { entity ->
				TagDao(
					id = entity.id.value,
					name = entity.name,
					description = entity.description
				)
			}
		},
		mainCategory = category,
		updatedAt = updatedAt
	)
}

internal suspend fun GithubDepositoryMiniEntity.convertToGHRepositoryMini(
	database: HikariDatabase,
	colorController: GithubColorController
): GHRepositoryMiniDao {
	return GHRepositoryMiniDao(
		id = id.value,
		fullName = fullName,
		license = license,
//		user = database.dbExec {
//			with(user) {
//				GHUser(
//					id = id.value,
//					name = name,
//					url = url
//				)
//			}
//		},
		languages = languages.map {
			it.copy(
				colorCode = colorController.getColorLanguageByName(it.name)
			)
		}.sortedByDescending {
			//Kotlin First
			if (it.name == "Kotlin") {
				Float.MAX_VALUE
			} else {
				it.weight
			}
		},
		tags = database.dbExec {
			tags.map { entity ->
				TagDao(
					id = entity.id.value,
					name = entity.name,
					description = entity.description
				)
			}
		},
		updatedAt = updatedAt
	)
}

internal suspend fun GHRepositoryExternal.convertToGHRepositoryWithDefaults(): GHRepositoryDao? {
	val repoOwner = try {
		owner.let { user ->
			GHUser(
				id = user.id,
				name = user.login,
				url = user.url.toString(),
				avatarUrl = user.avatarUrl
			)
		}
	} catch (ex: Exception) {
		null
	} ?: return null


	return GHRepositoryDao(
		id = id,
		name = name,
		fullName = fullName,
		description = description,
		url = htmlUrl.toString(),
		license = license?.name,
		licenseUrl = license?.htmlUrl?.toString(),
		user = repoOwner,
		languages = listLanguages().mapToLanguageDao(),
		//Use default on data recovery
		mainCategory = MainCategory.NONE,
		//Use default on data recovery
		tags = listOf(),
		updatedAt = updatedAt.toInstant().toKotlinInstant()
	)
}

internal fun Map<String, Long>.mapToLanguageDao(): List<GHLanguageDao> {
	val totalLines = values.sumOf { it }.toFloat()

	return map {
		val weight = (it.value * 100) / totalLines
		GHLanguageDao(name = it.key, lines = it.value, weight = weight)
	}.sortedByDescending { it.weight }
}

internal fun TagsEntity.convertToTagDao(): TagDao = TagDao(
	id = id.value,
	name = name,
	description = description,
	color = color
)
