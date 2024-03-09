package com.takaotech.dashboard.ui.admin.tags.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ttd.composeapp.generated.resources.Res
import ttd.composeapp.generated.resources.ghrepository_tag_edit_description_label
import ttd.composeapp.generated.resources.ghrepository_tag_edit_title_label

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TagEdit(
	titleTag: TextFieldValue,
	descriptionTag: TextFieldValue,
	onTitleTagChanged: (TextFieldValue) -> Unit,
	onDescriptionTagChanged: (TextFieldValue) -> Unit
) {
	Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
		OutlinedTextField(
			label = {
				Text(stringResource(Res.string.ghrepository_tag_edit_title_label))
			},
			value = titleTag,
			onValueChange = onTitleTagChanged
		)

		Spacer(Modifier.height(8.dp))

		OutlinedTextField(
			label = {
				Text(stringResource(Res.string.ghrepository_tag_edit_description_label))
			},
			value = descriptionTag,
			onValueChange = onDescriptionTagChanged
		)
	}
}
