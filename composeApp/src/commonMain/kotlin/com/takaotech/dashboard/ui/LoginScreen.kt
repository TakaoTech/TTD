package com.takaotech.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.takaotech.dashboard.ui.admin.github.GHRepositoryScreen

//object LoginScreen : Tab {
object LoginScreen : Screen {


//	override val options: TabOptions
//		@Composable
//		get() {
//			val title = "Ciao"
//			val icon = rememberVectorPainter(Icons.Filled.Person)
//
//			return remember {
//				TabOptions(
//					index = 0u,
//					title = title,
//					icon = icon
//				)
//			}
//		}

	@Composable
	override fun Content() {
		val navigator = LocalNavigator.currentOrThrow
		val viewModel = getScreenModel<LoginViewModel>()

		val uiState by viewModel.uiState.collectAsState()

		LoginScreenUi(
			username = uiState.username,
			password = uiState.password,
			modifier = Modifier.fillMaxSize(),
			onUsernameChanged = {
				viewModel.onUsernameChanged(it)
			},
			onPasswordChanged = {
				viewModel.onPasswordChanged(it)
			},
			loginClicked = {
				navigator.push(GHRepositoryScreen())
			}
		)
	}
}

@Composable
internal fun LoginScreenUi(
	username: TextFieldValue,
	password: TextFieldValue,
	modifier: Modifier = Modifier,
	onUsernameChanged: (TextFieldValue) -> Unit,
	onPasswordChanged: (TextFieldValue) -> Unit,
	loginClicked: () -> Unit
) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		var passwordVisible by remember {
			mutableStateOf(false)
		}

		OutlinedTextField(
			value = username,
			onValueChange = onUsernameChanged
		)

		OutlinedTextField(
			value = password,
			onValueChange = onPasswordChanged,
			trailingIcon = {
				IconToggleButton(
					passwordVisible,
					onCheckedChange = {
						passwordVisible = it
					}
				) {
					Icon(
						if (passwordVisible) {
							Icons.Default.Visibility
						} else {
							Icons.Default.VisibilityOff
						}, if (passwordVisible) {
							"Hide Password"
						} else {
							"Show Password"
						}
					)
				}
			},
			visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
		)

		Button(onClick = loginClicked) {
			//TODO Convert to strings
			Text("Login")
		}
	}
}