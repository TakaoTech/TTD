package com.takaotech.dashboard.ui.admin.github

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.takaotech.dashboard.model.github.MainCategoryDto
import com.takaotech.dashboard.model.github.TagDto

@Preview
@Composable
private fun GHRepositoryListPreview() {
}

@Preview(showSystemUi = true)
@Composable
private fun GHRepositoryCardPreview() {
    Column {
        AdminGHRepositoryCard(
            modifier = Modifier.fillMaxWidth(),
            fullName = "JetBrains/kotlin",
            tags =
                buildList {
                    add(TagDto(1, name = "Annmarie Whitfield", description = null))
                    add(TagDto(id = 2111, name = "Lorraine Sherman", description = null))
                    add(TagDto(id = 1645, name = "Buddy Simpson", description = null))
                    add(TagDto(id = 9869, name = "Tia Holcomb", description = null))
                    add(TagDto(id = 9137, name = "Kelly Barker", description = null))
                },
            mainCategory = MainCategoryDto.KOTLIN,
            onMainCategoryClicked = {},
            onTagEditClicked = {},
        ) {}
    }
}
