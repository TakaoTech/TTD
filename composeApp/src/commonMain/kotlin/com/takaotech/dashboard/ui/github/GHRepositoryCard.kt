package com.takaotech.dashboard.ui.github

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.takaotech.dashboard.model.GHLanguageDao
import com.takaotech.dashboard.model.TagDao
import net.sergeych.sprintf.sprintf
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalMaterialApi::class, ExperimentalResourceApi::class, ExperimentalLayoutApi::class)
@Composable
internal fun GHRepositoryCard(
	fullName: String,
	tags: List<TagDao>,
	languages: List<GHLanguageDao>,
	modifier: Modifier = Modifier,
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
					Chip(onClick = {}) {
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
				) {
					var start = 0f
					//https://github.com/ozh/github-colors/blob/master/colors.json

					languages.forEachIndexed { index, ghLanguageDao ->
						val k = start + (((maxWidth.toPx()) * (ghLanguageDao.weight)) / 100)

						//TODO WIP but need base on GH Colors for interpretation
						val color = when (index) {
							1 -> Color.Red
							2 -> Color.Blue
							3 -> Color.Green
							4 -> Color.Cyan
							5 -> Color.LightGray
							else -> Color.Magenta
						}

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

			FlowRow(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start)
			) {
				languages.forEach {
					Text("${it.name} ${"%.1f".sprintf(it.weight)}%")
				}
			}
		}
	}
}