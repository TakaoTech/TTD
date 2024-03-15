package com.takaotech.dashboard.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun LoginScreenUiPreview() {
	LoginScreenUi(
		username = TextFieldValue(),
		password = TextFieldValue(),
		onUsernameChanged = {},
		onPasswordChanged = {},
		onCreditClicked = {},
		loginClicked = {}
	)
}