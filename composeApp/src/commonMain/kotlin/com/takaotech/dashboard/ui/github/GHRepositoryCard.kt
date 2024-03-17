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
import com.takaotech.dashboard.model.TagDao
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalMaterialApi::class, ExperimentalResourceApi::class)
@Composable
internal fun GHRepositoryCard(
	fullName: String,
	tags: List<TagDao>,
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

			BoxWithConstraints(
				modifier = Modifier
					.fillMaxWidth()
			) {
				Canvas(
					modifier = Modifier.fillMaxSize()
						.height(8.dp)
				) {
					val k = ((maxWidth.toPx()) * (87.toFloat())) / 100
//					val k = (maxWidth.toPx()) * (87.toFloat()  / 100)

//					drawLine(
//						color = Color.Red,
//						start = Offset(0f, 0f),
//						end = Offset(k, 0f),
//						strokeWidth = 25f,
//						cap = StrokeCap.Round
//					)
//					drawLine(
//						color = Color.Black,
//						start = Offset(k, 0f),
//						end = Offset(maxWidth.toPx(), 0f),
//						strokeWidth = 25f,
//						cap = StrokeCap.Round
//					)

					//TODO Prototype Languages Line Charts

					//https://github.com/ozh/github-colors/blob/master/colors.json

					drawPath(
						color = Color.Red,
						path = Path().apply {
							addRoundRect(
								RoundRect(
									left = 0F,
									top = 0F,
									right = k,
									bottom = 8.dp.toPx(),
									topLeftCornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
									bottomLeftCornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
									topRightCornerRadius = CornerRadius.Zero,
									bottomRightCornerRadius = CornerRadius.Zero,
								)
							)
						}
					)

					drawPath(
						color = Color.Blue,
						path = Path().apply {
							addRoundRect(
								RoundRect(
									left = k,
									top = 0F,
									right = maxWidth.toPx(),
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
			}
			//TODO Chart as Github
		}
	}
}