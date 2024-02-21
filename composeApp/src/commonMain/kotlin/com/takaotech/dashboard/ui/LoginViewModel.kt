package com.takaotech.dashboard.ui

import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class LoginViewModel : ScreenModel {


	private val mUiState = MutableStateFlow(LoginUiState())
	val uiState = mUiState.asStateFlow()

	fun onUsernameChanged(usernameValue: TextFieldValue) {
		screenModelScope.launch {
			mUiState.update {
				it.copy(username = usernameValue)
			}
		}
	}

	fun onPasswordChanged(passwordValue: TextFieldValue) {
		screenModelScope.launch {
			mUiState.update {
				it.copy(password = passwordValue)
			}
		}
	}
}

data class LoginUiState(
	val username: TextFieldValue = TextFieldValue(),
	val password: TextFieldValue = TextFieldValue()
)