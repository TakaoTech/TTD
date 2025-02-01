package com.takaotech.dashboard.ui.admin.tags.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import ttd.composeapp.generated.resources.Res
import ttd.composeapp.generated.resources.ghrepository_add_new_tag

@Composable
fun TagListPage(
    viewModel: TagListViewModel,
    onTagEditClick: (edit: Boolean, id: Int?) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues()),
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(
                    onClick = {
                        onTagEditClick(false, null)
                    },
                ) {
                    Icon(Icons.Filled.Add, stringResource(Res.string.ghrepository_add_new_tag))
                }
            }
        },
    ) {
        TagList(
            tagListUi = uiState.tagUi,
            onTagClicked = {
                onTagEditClick(true, it)
            },
        )
    }
}
