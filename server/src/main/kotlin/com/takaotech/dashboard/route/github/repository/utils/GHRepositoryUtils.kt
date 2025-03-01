@file:JvmName("GHRepositoryUtilsKt")

package com.takaotech.dashboard.route.github.repository.utils

import com.takaotech.dashboard.model.github.exception.GHExternalConversionException
import com.takaotech.dashboard.models.GHLanguageDao
import com.takaotech.dashboard.models.GHRepositoryDao
import com.takaotech.dashboard.models.GHRepositoryMiniDao
import com.takaotech.dashboard.models.GHUserDao
import com.takaotech.dashboard.models.MainCategory
import com.takaotech.dashboard.models.TagDao
import com.takaotech.dashboard.route.github.data.GithubDepositoryEntity
import com.takaotech.dashboard.route.github.data.GithubDepositoryMiniEntity
import com.takaotech.dashboard.route.github.data.TagsEntity
import com.takaotech.dashboard.route.github.repository.GithubColorController
import com.takaotech.dashboard.utils.HikariDatabase
import kotlinx.datetime.toKotlinInstant
import okio.IOException
import org.kohsuke.github.GHRepository as GHRepositoryExternal
import org.kohsuke.github.GHUser as GHUserExternal

//TODO Change to server model
internal suspend fun GithubDepositoryEntity.convertToGHRepositoryServerDao(
    database: HikariDatabase,
    colorController: GithubColorController,
): GHRepositoryDao =
    GHRepositoryDao(
        id = id.value,
        name = name,
        fullName = fullName,
        description = description,
        url = url,
        license = license,
        licenseUrl = licenseUrl,
        user = database.dbExec {
            with(user) {
                GHUserDao(
                    id = id.value,
                    name = name,
                    url = url,
                    avatarUrl = user.avatarUrl,
                )
            }
        },
        languages = languages.map {
            GHLanguageDao(
                name = it.name,
                lines = it.lines,
                weight = it.weight,
                colorCode = colorController.getColorLanguageByName(it.name)
            )
        },
        tags = database.dbExec {
            tags.map { entity ->
                entity.convertToTagServerDao()
            }
        },
        mainCategory = category,
        updatedAt = updatedAt,
    )

//TODO Change to server model
internal suspend fun GithubDepositoryMiniEntity.convertToGHRepositoryMiniServerDao(
    database: HikariDatabase,
    colorController: GithubColorController,
): GHRepositoryMiniDao =
    GHRepositoryMiniDao(
        id = id.value,
        fullName = fullName,
        license = license,
// 		user = database.dbExec {
// 			with(user) {
// 				GHUser(
// 					id = id.value,
// 					name = name,
// 					url = url
// 				)
// 			}
// 		},
        languages = languages
            .map {
                GHLanguageDao(
                    name = it.name,
                    lines = it.lines,
                    weight = it.weight,
                    colorCode = colorController.getColorLanguageByName(it.name)
                )
            }.sortedByDescending {
                // Kotlin First
                if (it.name == "Kotlin") {
                    Float.MAX_VALUE
                } else {
                    it.weight
                }
            },
        tags = database.dbExec {
            tags.map { entity ->
                entity.convertToTagServerDao()
            }
        },
        updatedAt = updatedAt,
    )

internal fun GHRepositoryExternal.convertToGHRepositoryWithDefaults(): GHRepositoryDao {
    val repoOwner = try {
        owner.convertToGHUserServerDao()
    } catch (ex: Exception) {
        when (ex) {
            is NullPointerException, is IOException -> {
                throw GHExternalConversionException(
                    id = id.toString(),
                    name = name,
                    property = "owner",
                    cause = ex
                )
            }

            else -> throw ex
        }
    }

    var mLicense: String? = null
    var mLicenseUrl: String? = null

    try {
        license?.let {
            mLicense = it.name
            mLicenseUrl = it.htmlUrl?.toString()
        }
    } catch (ex: Exception) {
        when (ex) {
            is IOException -> {
                throw GHExternalConversionException(
                    id = id.toString(),
                    name = name,
                    property = "license",
                    cause = ex
                )
            }

            else -> throw ex
        }
    }

    return GHRepositoryDao(
        id = id,
        name = name,
        fullName = fullName,
        description = description,
        url = htmlUrl.toString(),
        license = mLicense,
        licenseUrl = mLicenseUrl,
        user = repoOwner,
        languages = try {
            listLanguages().mapToLanguageServerDao()
        } catch (ex: Exception) {
            when (ex) {
                is NullPointerException, is IOException -> {
                    throw GHExternalConversionException(
                        id = id.toString(),
                        name = name,
                        property = "listLanguages()",
                        cause = ex
                    )
                }

                else -> throw ex
            }
        },
        // Use default on data recovery
        mainCategory = MainCategory.NONE,
        // Use default on data recovery
        tags = listOf(),
        //TODO
        updatedAt = updatedAt.toInstant().toKotlinInstant(),
    )
}

//TODO Change to server model
private fun GHUserExternal.convertToGHUserServerDao() =
    GHUserDao(
        id = id,
        name = login,
        url = url.toString(),
        avatarUrl = avatarUrl,
    )

internal fun Map<String, Long>.mapToLanguageServerDao(): List<GHLanguageDao> {
    val totalLines = values.sumOf { it }.toFloat()

    return map {
        val weight = (it.value * 100) / totalLines
        GHLanguageDao(name = it.key, lines = it.value, weight = weight)
    }.sortedByDescending { it.weight }
}

//TODO Change to server model
internal fun TagsEntity.convertToTagServerDao(): TagDao =
    TagDao(
        id = id.value,
        name = name,
        description = description,
        color = color,
    )
