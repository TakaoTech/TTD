package com.takaotech.dashboard.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.takaotech.dashboard.model.github.GHLanguageDto
import com.takaotech.dashboard.model.github.GHRepositoryMiniDto
import com.takaotech.dashboard.model.github.TagDto
import kotlinx.datetime.Clock

class RepositoryProvider : PreviewParameterProvider<GHRepositoryMiniDto> {
    override val values =
        sequenceOf(
            GHRepositoryMiniDto(
                id = 5644,
                fullName = "Drew Thomas",
                license = null,
                languages =
                    listOf(
                        GHLanguageDto(name = "Donnell Johnson", lines = 8096, weight = 40f, colorCode = "123456"),
                        GHLanguageDto(name = "Naomi Hanson", lines = 9524, weight = 40f, colorCode = "531636"),
                        GHLanguageDto(name = "Courtney Lester", lines = 5216, weight = 20f, colorCode = "756234"),
                    ),
                updatedAt = Clock.System.now(),
                tags =
                    listOf(
                        TagDto(id = 3452, name = "Claude Rivera", description = null, color = "123456"),
                        TagDto(id = 7568, name = "Claude Rivera", description = null, color = "531636"),
                        TagDto(id = 14162, name = "Claude Rivera", description = null, color = "756234"),
                        TagDto(id = 65481, name = "Claude Rivera", description = null, color = "756234"),
                    ),
            ),
        )
}
