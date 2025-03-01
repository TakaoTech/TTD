package com.takaotech.dashboard.ui.admin.github

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.takaotech.dashboard.model.github.GHRepositoryDto
import com.takaotech.dashboard.model.github.GHUserDto
import com.takaotech.dashboard.model.github.MainCategoryDto
import com.takaotech.dashboard.model.github.TagDto
import com.takaotech.dashboard.ui.utils.assistChipColors
import com.takaotech.dashboard.ui.utils.toColor
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.stringResource
import ttd.composeapp.generated.resources.Res
import ttd.composeapp.generated.resources.ghrepository_no_tags
import ttd.composeapp.generated.resources.ghrepository_tags_label
import kotlin.random.Random

@Composable
fun AdminGHRepositoryList(
    ghRepositoryState: GHRepositoryListUiState.GhRepositoryListState,
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
    onCardClicked: (url: String) -> Unit,
    onTagEditClicked: (repoId: Long) -> Unit,
    onCategoryChangeClicked: (repoId: Long, newCategory: MainCategoryDto) -> Unit,
) {
    when (ghRepositoryState) {
        GHRepositoryListUiState.GhRepositoryListState.Error -> {
            // TODO
        }

        GHRepositoryListUiState.GhRepositoryListState.Loading -> {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
            )
        }

        is GHRepositoryListUiState.GhRepositoryListState.Success -> {
            val repoList = ghRepositoryState.ghRepositoryData

            val columns = when (windowSizeClass.windowWidthSizeClass) {
                WindowWidthSizeClass.COMPACT -> {
                    1
                }

                WindowWidthSizeClass.MEDIUM -> {
                    2
                }

                WindowWidthSizeClass.EXPANDED -> {
                    4
                }

                else -> {
                    1
                }
            }

            LazyVerticalGrid(
                modifier = modifier,
                columns = GridCells.Fixed(columns),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    key = { it.id },
                    items = repoList,
                ) {
                    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

                    if (openBottomSheet) {
                        MainCategoryBottomSheet(
                            categoryList = MainCategoryDto.entries,
                            onDismissRequest = {
                                openBottomSheet = false
                            },
                            onCategoryClicked = { newCategory ->
                                // not null because parameter entries not have nulls
                                onCategoryChangeClicked(it.id, newCategory!!)
                            },
                        )
                    }

                    AdminGHRepositoryCard(
                        modifier = Modifier.wrapContentWidth(),
                        fullName = it.fullName,
                        tags = it.tags,
                        mainCategory = it.mainCategory,
                        onMainCategoryClicked = {
                            openBottomSheet = true
                        },
                        onTagEditClicked = {
                            onTagEditClicked(it.id)
                        },
                        onCardClicked = {
                            onCardClicked(it.url)
                        },
                    )
                }
            }
        }
    }
}

@Composable
internal fun AdminGHRepositoryCard(
    fullName: String,
    mainCategory: MainCategoryDto,
    tags: List<TagDto>,
    onMainCategoryClicked: () -> Unit,
    onTagEditClicked: () -> Unit,
    modifier: Modifier = Modifier,
    onCardClicked: () -> Unit,
) {
    ElevatedCard(modifier = modifier, onClick = onCardClicked) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .basicMarquee(),
                    text = fullName,
                )
                AssistChip(
                    onClick = {
                        onMainCategoryClicked()
                    },
                    label = {
                        Text(mainCategory.name)
                    },
                )
            }

            Text(stringResource(Res.string.ghrepository_tags_label))
            Row {
                if (tags.isEmpty()) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(Res.string.ghrepository_no_tags),
                    )
                } else {
                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(tags) {
                            AssistChip(
                                colors = it.color?.toColor().assistChipColors(),
                                onClick = {},
                                label = {
                                    Text(it.name)
                                },
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onTagEditClicked,
                ) {
                    // TODO contentDesc
                    Icon(Icons.Filled.Edit, "Edit tags")
                }
            }
            // TODO Chart as Github
        }
    }
}

@Preview(
    device = Devices.PIXEL_TABLET,
)
@Composable
private fun AdminGHRepositoryListPreview() {
    val state = GHRepositoryListUiState.GhRepositoryListState.Success(
        List(15) {
            GHRepositoryDto(
                id = Random.nextLong(),
                name = LoremIpsum().values.first(),
                fullName = LoremIpsum().values.first().substring(0..25),
                description = LoremIpsum().values.first(),
                url = LoremIpsum().values.first(),
                license = LoremIpsum().values.first(),
                licenseUrl = LoremIpsum().values.first(),
                user = GHUserDto(
                    id = Random.nextLong(),
                    name = LoremIpsum().values.first(),
                    url = LoremIpsum().values.first(),
                    avatarUrl = null
                ),
                languages = listOf(),
                updatedAt = Clock.System.now(),
                tags = listOf(),
                mainCategory = MainCategoryDto.NONE
            )
        }
    )

    AdminGHRepositoryList(
        ghRepositoryState = state,
        onCardClicked = {},
        onTagEditClicked = {},
        onCategoryChangeClicked = { _, _ -> }
    )
}
