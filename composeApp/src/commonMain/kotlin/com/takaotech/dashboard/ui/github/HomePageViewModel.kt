package com.takaotech.dashboard.ui.github

import cafe.adriel.voyager.core.model.ScreenModel
import com.takaotech.dashboard.model.GHRepositoryDao
import com.takaotech.dashboard.model.GHUser
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.model.TagDao
import com.takaotech.dashboard.repository.GHRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory

@Factory
class HomePageViewModel(
	private val ghRepository: GHRepository
) : ScreenModel {
	private val mUiState = MutableStateFlow(HomePageUi())
	val uiState = mUiState.asStateFlow()

}

data class HomePageUi(
	val tags: List<TagDao> = listOf(
		TagDao(
			id = 4993, name = "Ben Miranda", description = null, color = null

		),
		TagDao(
			id = 7766, name = "Anderson Wong", description = null, color = null
		),
		TagDao(
			id = 9209, name = "Weldon Phillips", description = null, color = null
		),
		TagDao(id = 6250, name = "Moises Fowler", description = null, color = null),
		TagDao(id = 4255, name = "Sharlene Carney", description = null, color = null),
		TagDao(id = 9450, name = "Justine Britt", description = null, color = null),
		TagDao(id = 6018, name = "Tammy Gaines", description = null, color = null),
		TagDao(id = 5122, name = "Toni Dixon", description = null, color = null),
	),
	val repositoryList: List<GHRepositoryDao> = listOf(
		GHRepositoryDao(
			id = 6695,
			name = "Mercedes Steele",
			fullName = "Gene Santos",
			description = null,
			url = "http://www.bing.com/search?q=nullam",
			license = null,
			licenseUrl = null,
			user = GHUser(
				id = 7414,
				name = "Kara Patton",
				url = "https://www.google.com/#q=libero"
			),
			languages = mapOf(),
			updatedAt = Clock.System.now(),
			tags = listOf(),
			mainCategory = MainCategory.OTHER
		),
		GHRepositoryDao(
			id = 9400,
			name = "Rickie O'Brien",
			fullName = "Jamal Contreras",
			description = null,
			url = "https://duckduckgo.com/?q=arcu",
			license = null,
			licenseUrl = null,
			user = GHUser(
				id = 2041,
				name = "Lynette Mejia",
				url = "https://www.google.com/#q=ac"
			),
			languages = mapOf(),
			updatedAt = Clock.System.now(),
			tags = listOf(),
			mainCategory = MainCategory.OTHER
		),
		GHRepositoryDao(
			id = 5165,
			name = "Frieda Oliver",
			fullName = "Cecilia Watson",
			description = null,
			url = "https://www.google.com/#q=legere",
			license = null,
			licenseUrl = null,
			user = GHUser(
				id = 7835,
				name = "Duane McBride",
				url = "https://search.yahoo.com/search?p=definitionem"
			),
			languages = mapOf(),
			updatedAt = Clock.System.now(),
			tags = listOf(),
			mainCategory = MainCategory.OTHER
		),
		GHRepositoryDao(
			id = 6844,
			name = "Manuel Palmer",
			fullName = "Samantha Mayer",
			description = null,
			url = "http://www.bing.com/search?q=ut",
			license = null,
			licenseUrl = null,
			user = GHUser(
				id = 8159,
				name = "Micah Cummings",
				url = "http://www.bing.com/search?q=neglegentur"
			),
			languages = mapOf(),
			updatedAt = Clock.System.now(),
			tags = listOf(),
			mainCategory = MainCategory.OTHER

		),
		GHRepositoryDao(
			id = 1601,
			name = "Seth Floyd",
			fullName = "Wilford Fox",
			description = null,
			url = "https://duckduckgo.com/?q=omnesque",
			license = null,
			licenseUrl = null,
			user = GHUser(
				id = 2481,
				name = "Rufus Boyle",
				url = "http://www.bing.com/search?q=definiebas"
			),
			languages = mapOf(),
			updatedAt = Clock.System.now(),
			tags = listOf(),
			mainCategory = MainCategory.OTHER

		),
		GHRepositoryDao(
			id = 7202,
			name = "Lydia Stokes",
			fullName = "Lavonne Lowery",
			description = null,
			url = "https://search.yahoo.com/search?p=moderatius",
			license = null,
			licenseUrl = null,
			user = GHUser(
				id = 6519,
				name = "Hunter Silva",
				url = "http://www.bing.com/search?q=sententiae"
			),
			languages = mapOf(),
			updatedAt = Clock.System.now(),
			tags = listOf(),
			mainCategory = MainCategory.OTHER

		),
	)
)