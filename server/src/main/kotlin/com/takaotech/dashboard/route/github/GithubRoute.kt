package com.takaotech.dashboard.route.github

import com.takaotech.dashboard.model.MainCategory
import io.ktor.resources.*

@Resource("/github")
class GithubRoute(
	val category: MainCategory? = null
) {
	@Resource("{id}")
	class Id(val parent: GithubRoute = GithubRoute(), val id: Long? = null) {
		@Resource("updateCategory")
		//TODO newCategory as query param?
		class UpdateCategory(val parent: Id = Id(), val newCategory: MainCategory)
	}

	@Resource("refresh")
	class Refresh(val parent: GithubRoute = GithubRoute(), val mock: Boolean = false) {
		@Resource("cancel")
		class Cancel(val parent: Refresh = Refresh())

		@Resource("status")
		class Status(val parent: Refresh = Refresh())
	}

	@Resource("tags")
	class Tags(val parent: GithubRoute = GithubRoute()) {
		@Resource("{id}")
		class Id(val parent: Tags = Tags(), val id: Int)
	}
}