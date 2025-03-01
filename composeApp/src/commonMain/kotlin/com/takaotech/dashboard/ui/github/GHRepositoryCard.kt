package com.takaotech.dashboard.ui.github

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.takaotech.dashboard.model.github.GHLanguageDto
import com.takaotech.dashboard.model.github.TagDto
import com.takaotech.dashboard.ui.utils.toColor
import net.sergeych.sprintf.sprintf

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun GHRepositoryCard(
    fullName: String,
    tags: List<TagDto>,
    languages: List<GHLanguageDto>,
    modifier: Modifier = Modifier,
    onTagClicked: (tagId: Int) -> Unit,
    onCardClicked: () -> Unit,
) {
    ElevatedCard(modifier = modifier, onClick = onCardClicked) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = fullName,
                )
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(tags) {
                    AssistChip(
                        onClick = {
                            onTagClicked(it.id)
                        },
                        label = {
                            Text(it.name)
                        },
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            BoxWithConstraints(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
            ) {
                Canvas(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(16.dp)),
                ) {
                    var start = 0f
                    // https://github.com/ozh/github-colors/blob/master/colors.json

                    languages.forEachIndexed { index, ghLanguageDto ->
                        val k = start + (((maxWidth.toPx()) * (ghLanguageDto.weight)) / 100)
                        val color = ghLanguageDto.colorCode?.replace("#", "")?.toColor() ?: Color.Gray

                        when {
                            index == 0 -> {
                                drawPath(
                                    color = color,
                                    path =
                                        Path().apply {
                                            addRoundRect(
                                                RoundRect(
                                                    left = 0F,
                                                    top = 0F,
                                                    right = k,
                                                    bottom = 8.dp.toPx(),
                                                    topLeftCornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                                                    bottomLeftCornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                                                    topRightCornerRadius =
                                                        if (languages.size == 1) {
                                                            CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                                        } else {
                                                            CornerRadius.Zero
                                                        },
                                                    bottomRightCornerRadius =
                                                        if (languages.size == 1) {
                                                            CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                                        } else {
                                                            CornerRadius.Zero
                                                        },
                                                ),
                                            )
                                        },
                                )
                            }

                            languages.lastIndex == index -> {
                                drawPath(
                                    color = color,
                                    path =
                                        Path().apply {
                                            addRoundRect(
                                                RoundRect(
                                                    left = start,
                                                    top = 0F,
                                                    right = k,
                                                    bottom = 8.dp.toPx(),
                                                    topLeftCornerRadius = CornerRadius.Zero,
                                                    bottomLeftCornerRadius = CornerRadius.Zero,
                                                    topRightCornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                                                    bottomRightCornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                                                ),
                                            )
                                        },
                                )
                            }

                            else -> {
                                drawPath(
                                    color = color,
                                    path =
                                        Path().apply {
                                            addRoundRect(
                                                RoundRect(
                                                    left = start,
                                                    top = 0F,
                                                    right = k,
                                                    bottom = 8.dp.toPx(),
                                                    topLeftCornerRadius = CornerRadius.Zero,
                                                    bottomLeftCornerRadius = CornerRadius.Zero,
                                                    topRightCornerRadius = CornerRadius.Zero,
                                                    bottomRightCornerRadius = CornerRadius.Zero,
                                                ),
                                            )
                                        },
                                )
                            }
                        }

                        start = k
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            FlowRow(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
            ) {
                languages.forEach {
                    // TODO Support RTL
                    Text("${it.name} ${"%.1f".sprintf(it.weight)}%")
                }
            }
        }
    }
}
