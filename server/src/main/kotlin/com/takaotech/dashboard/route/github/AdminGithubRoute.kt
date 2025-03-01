package com.takaotech.dashboard.route.github

import com.takaotech.dashboard.model.github.MainCategoryDto
import io.ktor.resources.*

@Resource("/github")
class AdminGithubRoute(
    val category: MainCategoryDto? = null,
) {
    @Resource("{id}")
    class Id(
        val parent: AdminGithubRoute = AdminGithubRoute(),
        val id: Long? = null,
    ) {
        // TODO newCategory as query param?
        @Resource("updateCategory")
        class UpdateCategory(
            val parent: Id = Id(),
            val newCategory: MainCategoryDto,
        )

        // TODO newCategory as query param?
        @Resource("updateTags")
        class UpdateTags(
            val parent: Id = Id(),
        )
    }

    @Resource("refresh")
    class Refresh(
        val parent: AdminGithubRoute = AdminGithubRoute(),
        val mock: Boolean = false,
    ) {
        @Resource("cancel")
        class Cancel(
            val parent: Refresh = Refresh(),
        )

        @Resource("status")
        class Status(
            val parent: Refresh = Refresh(),
        )
    }

    @Resource("tags")
    class Tags(
        val parent: AdminGithubRoute = AdminGithubRoute(),
    ) {
        @Resource("{id}")
        class Id(
            val parent: Tags = Tags(),
            val id: Int,
        )
    }
}
