package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.model.TakaoPaging
import com.takaotech.dashboard.models.TagDao
import com.takaotech.dashboard.models.TagNewDao
import com.takaotech.dashboard.route.github.data.TagsEntity
import com.takaotech.dashboard.route.github.repository.utils.convertToTagServerDao
import com.takaotech.dashboard.utils.HikariDatabase
import org.koin.core.annotation.Singleton

@Singleton
class TagsRepository(
    private val database: HikariDatabase,
) {
    suspend fun addTag(tag: TagNewDao) {
        database.dbExec {
            TagsEntity.new {
                name = tag.name
                description = tag.description
                color = tag.color
            }
        }
    }

    suspend fun updateTag(tag: TagDao) {
        database.dbExec {
            TagsEntity.findById(tag.id)?.let {
                it.name = tag.name
                it.description = tag.description
                it.color = tag.color
            }
        }
    }

    suspend fun getTags(
        page: Int?,
        size: Int?,
    ): TakaoPaging<TagDao> =
        database.dbExec {
            TagsEntity
                .all()
                .run {
                    if (page != null && size != null) {
                        val limit: Int = size
                        val pageSize: Int = size
                        val skip: Int = (page - 1) * pageSize

                        val totalPages =
                            (count() / pageSize).let {
                                if (it == 0L) {
                                    1
                                } else {
                                    it
                                }
                            }

                        totalPages to limit(count = limit).offset(start = skip.toLong())
                    } else {
                        1L to this
                    }
                }.run {
                    TakaoPaging(
                        data = second.map { it.convertToTagServerDao() },
                        page = page ?: 1,
                        totalPage = first,
                    )
                }
        }

    suspend fun removeTag(tagId: Int) {
        database.dbExec {
            TagsEntity.findById(tagId)?.delete()
        }
    }

    suspend fun getTagById(id: Int): TagDao? =
        database.dbExec {
            TagsEntity.findById(id)?.convertToTagServerDao()
        }

    internal suspend fun getTagByIdInternal(id: Int): TagsEntity? =
        database.dbExec {
            TagsEntity.findById(id)
        }
}
