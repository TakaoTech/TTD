package com.takaotech.dashboard.ui.admin.tags.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.takaotech.dashboard.ui.admin.tags.list.TagListViewModel

@Composable
fun TagEditPage(
    tagListViewModel: TagListViewModel,
    tagEditViewModel: TagEditViewModel,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(Unit) {
        tagEditViewModel.exitChannel.collect {
            tagListViewModel.refreshTagList()
            // TODO Add navigate pop
//            navigator.pop()
        }
    }

    val uiState by tagEditViewModel.uiState.collectAsState()

    TagEdit(
        modifier = modifier,
        titleTag = uiState.name,
        descriptionTag = uiState.description,
        colorTag = uiState.color,
        onTitleTagChanged = tagEditViewModel::onTitleChange,
        onDescriptionTagChanged = tagEditViewModel::onDescriptionChange,
        onColorTagChanged = tagEditViewModel::onColorChange,
    ) {
        tagEditViewModel.saveTag()
    }
}
