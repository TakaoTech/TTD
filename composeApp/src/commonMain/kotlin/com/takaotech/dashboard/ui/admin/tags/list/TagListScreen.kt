package com.takaotech.dashboard.ui.admin.tags.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.takaotech.dashboard.ui.admin.tags.edit.TagEditScreen
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ttd.composeapp.generated.resources.Res
import ttd.composeapp.generated.resources.ghrepository_add_new_tag

class TagListScreen : Screen {

	@OptIn(ExperimentalResourceApi::class)
	@Composable
	override fun Content() {
		val navigator = LocalNavigator.currentOrThrow
		val viewModel = getScreenModel<TagListViewModel>()
		val uiState by viewModel.uiState.collectAsState()

		Scaffold(
			topBar = {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.End
				) {
					IconButton(
						onClick = {
							navigator.push(TagEditScreen(editMode = false))
						}
					) {
						Icon(Icons.Filled.Add, stringResource(Res.string.ghrepository_add_new_tag))
					}
				}
			}
		) {
			TagList(
				tagListUi = uiState.tagUi,
				onTagClicked = {
					navigator.push(TagEditScreen(tagId = it, editMode = true))
				}
			)
		}
	}
}