package com.takaotech.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.takaotech.dashboard.ui.credits.CreditScreen
import com.takaotech.dashboard.ui.login.SessionManager
import com.takaotech.dashboard.ui.platform.components.SignInButton
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import ttd.composeapp.generated.resources.Res
import ttd.composeapp.generated.resources.credit_opensource_licence_label
import ttd.composeapp.generated.resources.ic_google_logo

object LoginScreen : Tab, KoinComponent {
//object LoginScreen : Screen {


    override val options: TabOptions
        @Composable
        get() {
            val title = "Login"
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

        val sessionManager = remember {
            get<SessionManager>()
        }

        val session by sessionManager.takaoSession.collectAsState(null)

        var isLogin by remember { mutableStateOf(false) }

        if (session == null) {
            LoginScreenUi(
                modifier = Modifier.fillMaxSize(),
                isLogin = isLogin,
                onCreditClicked = {
                    navigator.parent?.push(CreditScreen())
                },
                onGoogleLoginClicked = {
                    sessionManager.startGoogleLogin()
                },
                onGoogleSignupClicked = {
                    sessionManager.startGoogleSignup()
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
            Button(
                onClick = {
                    sessionManager.logout()
                }
            ) {
                Text("Logout")
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
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (isLogin) {
            //TODO Replace with ProviderIcon + Text
            SignInButton(
                onClick = onGoogleLoginClicked,
                text = "Login with Google",
                icon = painterResource(Res.drawable.ic_google_logo),
                isLoading = false,

                )
            Button(
                onClick = onAppleLoginClicked, //Login with Twitch,
                content = {

                }
            )
        } else {
            SignInButton(
                onClick = onGoogleSignupClicked,
                text = "Signup with Google",
                icon = painterResource(Res.drawable.ic_google_logo),
                isLoading = false

            )
        }

        TextButton(onClick = onLoginSwitch) {
            Text("New Account")
        }

        TextButton(onClick = onCreditClicked) {
            Text(stringResource(Res.string.credit_opensource_licence_label))
        }
    }
}