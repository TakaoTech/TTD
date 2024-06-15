package com.takaotech.dashboard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.takaotech.dashboard.ui.credits.CreditScreen
import com.takaotech.dashboard.ui.platform.components.SignInButton
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.component.KoinComponent
import ttd.composeapp.generated.resources.*

@OptIn(ExperimentalResourceApi::class)
object LoginScreen : Tab, KoinComponent {
//object LoginScreen : Screen {


    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.login)
            val icon = rememberVectorPainter(Icons.Filled.Person)

            return remember {
                TabOptions(
                    index = 4u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<LoginViewModel>()

        val session by viewModel.takaoSession.collectAsState(null)

        var isLogin by remember { mutableStateOf(true) }

        if (session == null) {
            LoginScreenUi(
                modifier = Modifier.fillMaxSize(),
                isLogin = isLogin,
                onCreditClicked = {
                    navigator.parent?.push(CreditScreen())
                },
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
                }
            )
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Button(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = {
                        viewModel.logout()
                    }
                ) {
                    Text(stringResource(Res.string.logout))
                }
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
    onLoginSwitch: () -> Unit
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
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
                    isLoading = false

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
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            onClick = onCreditClicked
        ) {
            Text(stringResource(Res.string.credit_opensource_licence_label))
        }
    }
}