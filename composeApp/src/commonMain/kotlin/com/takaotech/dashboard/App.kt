package com.takaotech.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.takaotech.dashboard.ui.LoginScreen
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
	MaterialTheme {
		TabNavigator(LoginScreen) {
			Scaffold(
				content = {
					Box(modifier = Modifier.padding(it)) {
						CurrentTab()
					}
				},
				bottomBar = {
					BottomNavigation {
						TabNavigationItem(LoginScreen)
					}
				}
			)
		}
	}
}


@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
	val tabNavigator = LocalTabNavigator.current

	BottomNavigationItem(
		selected = tabNavigator.current == tab,
		onClick = { tabNavigator.current = tab },
		icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) }
	)
}