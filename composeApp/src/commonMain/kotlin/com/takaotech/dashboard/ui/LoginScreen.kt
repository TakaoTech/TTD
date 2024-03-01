package com.takaotech.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object LoginScreen : Tab {


	override val options: TabOptions
		@Composable
		get() {
			val title = "Ciao"
			val icon = rememberVectorPainter(Icons.Filled.Person)

			return remember {
				TabOptions(
					index = 0u,
					title = title,
					icon = icon
				)
			}
		}

	@Composable
	override fun Content() {
		val viewModel = getScreenModel<LoginViewModel>()

		val uiState by viewModel.uiState.collectAsState()

		LoginScreenUi(
			modifier = Modifier.fillMaxSize(),
			username = uiState.username,
			password = uiState.password,
			onUsernameChanged = {
				viewModel.onUsernameChanged(it)
			},
			onPasswordChanged = {
				viewModel.onPasswordChanged(it)
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
	onPasswordChanged: (TextFieldValue) -> Unit
) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		var passwordVisible by remember {
			mutableStateOf(false)
		}
//		Text(string.hello.toString(Locale("it")))

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
	}
}