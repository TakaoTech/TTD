package com.takaotech.dashboard.ui.credits

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.readResourceBytes

class CreditScreen : Screen {
	@OptIn(InternalResourceApi::class)
	@Composable
	override fun Content() {
		var aboutJson: String? by remember {
			mutableStateOf(null)
		}

		LaunchedEffect(Unit) {
			aboutJson = readResourceBytes("res/raw/aboutlibraries.json").decodeToString()
		}

		if (aboutJson != null) {
			LibrariesContainer(aboutJson!!, Modifier.fillMaxSize())
		}

	}
}