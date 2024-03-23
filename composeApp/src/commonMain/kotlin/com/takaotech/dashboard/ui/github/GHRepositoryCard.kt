package com.takaotech.dashboard.ui.github

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.takaotech.dashboard.model.github.GHLanguageDao
import com.takaotech.dashboard.model.github.TagDao
import com.takaotech.dashboard.ui.utils.toColor
import net.sergeych.sprintf.sprintf

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
internal fun GHRepositoryCard(
	fullName: String,
	tags: List<TagDao>,
	languages: List<GHLanguageDao>,
	modifier: Modifier = Modifier,
	onTagClicked: (tagId: Int) -> Unit,
	onCardClicked: () -> Unit
) {
	Card(modifier = modifier, onClick = onCardClicked) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
		) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Text(
					modifier = Modifier.weight(1f),
					text = fullName
				)
			}

			LazyRow(
				modifier = Modifier.weight(1f),
				horizontalArrangement = Arrangement.spacedBy(4.dp)
			) {
				items(tags) {
					Chip(
						onClick = {
							onTagClicked(it.id)
						}
					) {
						Text(it.name)
					}
				}
			}

			Spacer(Modifier.height(16.dp))

			BoxWithConstraints(
				modifier = Modifier
					.fillMaxWidth()
			) {
				Canvas(
					modifier = Modifier.fillMaxSize()
						.height(8.dp)
						.clip(RoundedCornerShape(16.dp))
				) {
					var start = 0f
					//https://github.com/ozh/github-colors/blob/master/colors.json

					languages.forEachIndexed { index, ghLanguageDao ->
						val k = start + (((maxWidth.toPx()) * (ghLanguageDao.weight)) / 100)
						val color = ghLanguageDao.colorCode?.replace("#", "")?.toColor() ?: Color.Gray

						when {
							index == 0 -> {
								drawPath(
									color = color,
									path = Path().apply {
										addRoundRect(
											RoundRect(
												left = 0F,
												top = 0F,
												right = k,
												bottom = 8.dp.toPx(),
												topLeftCornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
												bottomLeftCornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
												topRightCornerRadius = if (languages.size == 1) {
													CornerRadius(4.dp.toPx(), 4.dp.toPx())
												} else {
													CornerRadius.Zero
												},
												bottomRightCornerRadius = if (languages.size == 1) {
													CornerRadius(4.dp.toPx(), 4.dp.toPx())
												} else {
													CornerRadius.Zero
												}
											)
										)
									}
								)
							}

							languages.lastIndex == index -> {
								drawPath(
									color = color,
									path = Path().apply {
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
											)
										)
									}
								)
							}

							else -> {
								drawPath(
									color = color,
									path = Path().apply {
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
											)
										)
									}
								)
							}
						}

						start = k
					}
				}
			}

			Spacer(Modifier.height(8.dp))
			FlowRow(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start)
			) {
				languages.forEach {
					//TODO Support RTL
					Text("${it.name} ${"%.1f".sprintf(it.weight)}%")
				}
			}
		}
	}
}