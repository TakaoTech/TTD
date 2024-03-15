package com.takaotech.dashboard

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.takaotech.dashboard.ui.admin.github.GHRepositoryScreen

object AdminNavigator : Screen {
	@Composable
	override fun Content() {
		Navigator(GHRepositoryScreen()) {
			CurrentScreen()
		}
	}
}