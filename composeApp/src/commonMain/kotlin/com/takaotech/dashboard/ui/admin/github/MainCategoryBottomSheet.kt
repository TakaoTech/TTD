package com.takaotech.dashboard.ui.admin.github

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.takaotech.dashboard.model.MainCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainCategoryBottomSheet(
	categoryList: List<MainCategory?>,
	onDismissRequest: () -> Unit,
	onCategoryClicked: (MainCategory?) -> Unit
) {
	ModalBottomSheet(
		onDismissRequest = onDismissRequest
	) {
		categoryList.forEach {
			Row(modifier = Modifier.fillMaxWidth()
				.clickable {
					onDismissRequest()
					onCategoryClicked(it)
				}
				.padding(16.dp)
			) {
				Text(
					text = AnnotatedString(it?.name ?: "--")
				)
			}
		}
	}
}