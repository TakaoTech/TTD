package com.takaotech.dashboard.route.github

import io.ktor.resources.*

@Resource("/github")
class GithubRoute(val page: Int? = null, val size: Int? = null, val tagId: Int? = null) {
	@Resource("{id}")
	class Id(val parent: GithubRoute = GithubRoute(), val id: Long)

	@Resource("tags")
	class Tags(val parent: GithubRoute = GithubRoute())
}