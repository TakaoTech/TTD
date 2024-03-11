package com.takaotech.dashboard.ui.admin.tags

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.takaotech.dashboard.ui.admin.tags.list.TagListScreen

class AdminTagsScreen : Screen {

	@Composable
	override fun Content() {
		Navigator(TagListScreen()) {
			CurrentScreen()
		}
	}
}