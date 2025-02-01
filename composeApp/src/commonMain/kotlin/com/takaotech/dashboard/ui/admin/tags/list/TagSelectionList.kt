package com.takaotech.dashboard.ui.admin.tags.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.takaotech.dashboard.ui.utils.assistChipColors
import com.takaotech.dashboard.ui.utils.toColor

@Composable
fun TagSelectionPage(
    uiState: TagSelectionListUi,
    onTagChanged: (id: Int) -> Unit,
    onSaveClicked: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSaveClicked,
            ) {
                Text("Save")
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
        ) {
            items(uiState.tagList) {
                AssistChip(
                    onClick = {
                        onTagChanged(it.id)
                    },
                    colors = it.color?.toColor().assistChipColors(),
                    leadingIcon = {
                        if (it.selected) {
                            // TODO Add Content Description
                            Icon(Icons.Filled.Check, "ch")
                        }
                    },
                    label = {
                        Text(
                            text = it.name,
                        )
                    },
                )
            }
        }
    }
}
