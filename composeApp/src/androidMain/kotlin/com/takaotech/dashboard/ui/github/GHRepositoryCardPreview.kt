package com.takaotech.dashboard.ui.github

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.takaotech.dashboard.model.github.GHRepositoryMiniDto
import com.takaotech.dashboard.ui.preview.RepositoryProvider
import com.takaotech.dashboard.ui.theme.AppTheme

@Preview
@Composable
fun GHRepositoryCardPreview(
    @PreviewParameter(RepositoryProvider::class) data: GHRepositoryMiniDto,
) {
    AppTheme {
        GHRepositoryCard(
            fullName = "Malcolm Nelson",
            tags = data.tags,
            modifier = Modifier.fillMaxWidth(),
            languages = data.languages,
            onCardClicked = {},
            onTagClicked = {},
        )
    }
}
