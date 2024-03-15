package com.takaotech.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.takaotech.dashboard.ui.LoginScreen

object MainNavigator : Screen {
	@OptIn(ExperimentalFoundationApi::class)
	@Composable
	override fun Content() {
		val navigator = LocalNavigator.currentOrThrow

		TabNavigator(LoginScreen) {
			Scaffold(
				content = {
					CurrentScreen()
				},
				bottomBar = {
					BottomNavigation {
//						TabNavigationItem(HomeTab)
//						TabNavigationItem(FavoritesTab)
						TabNavigationItem(LoginScreen)
					}
					IconButton(onClick = {
						navigator.push(
							AdminNavigator
						)
					}) {
						Icon(Icons.Filled.AdminPanelSettings, "")
					}
				}
			)
		}
	}
}