package com.takaotech.dashboard.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun LoginScreenUiPreview() {
    LoginScreenUi(
        isLogin = true,
        onGoogleLoginClicked = {},
        onAppleLoginClicked = {},
        onCreditClicked = {},
        modifier = Modifier,
        onGoogleSignupClicked = {},
        onAppleSignupClicked = {},
        onLoginSwitch = {}
    )
}