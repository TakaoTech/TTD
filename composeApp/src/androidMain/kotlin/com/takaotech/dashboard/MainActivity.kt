package com.takaotech.dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import com.takaotech.dashboard.ui.platform.AndroidUriHandler
import com.takaotech.dashboard.ui.platform.LocalTTDUriHandler

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContent {
			CompositionLocalProvider(LocalTTDUriHandler provides AndroidUriHandler(this)) {
				App()
			}
		}
	}
}

@Preview
@Composable
fun AppAndroidPreview() {
	App()
}