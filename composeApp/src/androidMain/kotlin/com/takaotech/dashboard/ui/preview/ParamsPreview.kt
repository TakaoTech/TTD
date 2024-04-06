package com.takaotech.dashboard.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.takaotech.dashboard.model.github.GHLanguageDao
import com.takaotech.dashboard.model.github.GHRepositoryMiniDao
import com.takaotech.dashboard.model.github.TagDao
import kotlinx.datetime.Clock

class RepositoryProvider : PreviewParameterProvider<GHRepositoryMiniDao> {
	override val values = sequenceOf(
		GHRepositoryMiniDao(
			id = 5644,
			fullName = "Drew Thomas",
			license = null,
			languages = listOf(
				GHLanguageDao(name = "Donnell Johnson", lines = 8096, weight = 40f, colorCode = "123456"),
				GHLanguageDao(name = "Naomi Hanson", lines = 9524, weight = 40f, colorCode = "531636"),
				GHLanguageDao(name = "Courtney Lester", lines = 5216, weight = 20f, colorCode = "756234")
			),
			updatedAt = Clock.System.now(),
			tags = listOf(
				TagDao(id = 3452, name = "Claude Rivera", description = null, color = "123456"),
				TagDao(id = 7568, name = "Claude Rivera", description = null, color = "531636"),
				TagDao(id = 14162, name = "Claude Rivera", description = null, color = "756234"),
				TagDao(id = 65481, name = "Claude Rivera", description = null, color = "756234"),
			)
		)
	)
}