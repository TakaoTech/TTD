package com.takaotech.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.takaotech.dashboard.ui.platform.components.SignInButton
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ttd.composeapp.generated.resources.Res
import ttd.composeapp.generated.resources.already_account
import ttd.composeapp.generated.resources.credit_opensource_licence_label
import ttd.composeapp.generated.resources.ic_google_logo
import ttd.composeapp.generated.resources.login_with_google
import ttd.composeapp.generated.resources.logout
import ttd.composeapp.generated.resources.new_account
import ttd.composeapp.generated.resources.signup_with_google

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel(),
    onCreditClicked: () -> Unit
) {

    val session by viewModel.takaoSession.collectAsState(null)

    var isLogin by remember { mutableStateOf(true) }

    if (session == null) {
        LoginScreenUi(
            modifier = modifier.fillMaxSize(),
            isLogin = isLogin,
            onCreditClicked = onCreditClicked,
            onGoogleLoginClicked = {
                viewModel.startGoogleLogin()
            },
            onGoogleSignupClicked = {
                viewModel.startGoogleSignup()
            },
            onAppleLoginClicked = {
            },
            onAppleSignupClicked = {
            },
            onLoginSwitch = {
                isLogin = !isLogin
            },
        )
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            Button(
                modifier = Modifier.align(Alignment.Center),
                onClick = {
                    viewModel.logout()
                },
            ) {
                Text(stringResource(Res.string.logout))
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun LoginScreenUi(
    modifier: Modifier = Modifier,
    isLogin: Boolean,
    onGoogleLoginClicked: () -> Unit,
    onGoogleSignupClicked: () -> Unit,
    onAppleLoginClicked: () -> Unit,
    onAppleSignupClicked: () -> Unit,
    onCreditClicked: () -> Unit,
    onLoginSwitch: () -> Unit,
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isLogin) {
                SignInButton(
                    onClick = onGoogleLoginClicked,
                    text = stringResource(Res.string.login_with_google),
                    icon = painterResource(Res.drawable.ic_google_logo),
                    isLoading = false,
                )
//            Button(
//                onClick = onAppleLoginClicked, //Login with Twitch,
//                content = {
//
//                }
//            )
            } else {
                SignInButton(
                    onClick = onGoogleSignupClicked,
                    text = stringResource(Res.string.signup_with_google),
                    icon = painterResource(Res.drawable.ic_google_logo),
                    isLoading = false,
                )
            }

            TextButton(onClick = onLoginSwitch) {
                if (isLogin) {
                    Text(stringResource(Res.string.new_account))
                } else {
                    Text(stringResource(Res.string.already_account))
                }
            }
        }

        TextButton(
            modifier =
                Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
            onClick = onCreditClicked,
        ) {
            Text(stringResource(Res.string.credit_opensource_licence_label))
        }
    }
}
