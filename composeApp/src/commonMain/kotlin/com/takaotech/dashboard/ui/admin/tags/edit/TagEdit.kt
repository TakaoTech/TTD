package com.takaotech.dashboard.ui.admin.tags.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.takaotech.dashboard.ui.utils.toColor
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ttd.composeapp.generated.resources.*

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TagEdit(
	titleTag: TextFieldValue,
	descriptionTag: TextFieldValue,
	colorTag: TextFieldValue,
	onTitleTagChanged: (TextFieldValue) -> Unit,
	onDescriptionTagChanged: (TextFieldValue) -> Unit,
	onColorTagChanged: (TextFieldValue) -> Unit,
	onSaveClicked: () -> Unit
) {
	Scaffold(
		bottomBar = {
			Button(
				modifier = Modifier.fillMaxWidth()
					.padding(16.dp),
				onClick = onSaveClicked
			) {
				Text(stringResource(Res.string.save))
			}
		}
	) {
		Column(
			modifier = Modifier
				.verticalScroll(rememberScrollState())
				.padding(horizontal = 8.dp)
		) {
			OutlinedTextField(
				modifier = Modifier.fillMaxWidth(),
				singleLine = true,
				label = {
					Text(stringResource(Res.string.ghrepository_tag_edit_title_label))
				},
				value = titleTag,
				onValueChange = onTitleTagChanged
			)

			Spacer(Modifier.height(8.dp))

			OutlinedTextField(
				modifier = Modifier.fillMaxWidth(),
				label = {
					Text(stringResource(Res.string.ghrepository_tag_edit_description_label))
				},
				value = descriptionTag,
				onValueChange = onDescriptionTagChanged
			)

			Spacer(Modifier.height(8.dp))

			OutlinedTextField(
				modifier = Modifier.fillMaxWidth(),
				singleLine = true,
				label = {
					Text(stringResource(Res.string.ghrepository_tag_edit_color_label))
				},
				leadingIcon = {
					Box(
						modifier = Modifier
							.size(24.dp)
							.border(1.dp, Color.Black)
							.background(
								try {
									colorTag.text.toColor()
								} catch (ex: Exception) {
									Color.Transparent
								}
							)
					)
				},
				value = colorTag,
				onValueChange = onColorTagChanged
			)

		}
	}
}
