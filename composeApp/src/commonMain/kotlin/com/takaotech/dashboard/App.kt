package com.takaotech.dashboard

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
	MaterialTheme {
		Navigator(MainNavigator) {
			CurrentScreen()
		}
	}
}


@Composable
fun RowScope.TabNavigationItem(tab: Tab) {
	val tabNavigator = LocalTabNavigator.current

	BottomNavigationItem(
		selected = tabNavigator.current == tab,
		onClick = { tabNavigator.current = tab },
		icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) }
	)
}