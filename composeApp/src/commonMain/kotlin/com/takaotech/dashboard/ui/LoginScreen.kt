package com.takaotech.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import co.touchlab.kermit.Logger
import com.takaotech.dashboard.ui.credits.CreditScreen
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import ttd.composeapp.generated.resources.Res
import ttd.composeapp.generated.resources.credit_opensource_licence_label

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

        val logger = get<Logger>()


        LoginScreenUi(
            modifier = Modifier.fillMaxSize(),
            onCreditClicked = {
                navigator.parent?.push(CreditScreen())
            },
            onGoogleLoginClicked = {

            },
            onAppleLoginClicked = {
            }

        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun LoginScreenUi(
    modifier: Modifier = Modifier,
    onGoogleLoginClicked: () -> Unit,
    onAppleLoginClicked: () -> Unit,
    onCreditClicked: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //TODO Replace with ProviderIcon + Text
        OutlinedButton(
            onClick = onGoogleLoginClicked, //Login with Google,
            content = {

            }
        )
        Button(
            onClick = onAppleLoginClicked, //Login with Twitch,
            content = {

            }
        )

        TextButton(onClick = onCreditClicked) {
            Text(stringResource(Res.string.credit_opensource_licence_label))
        }
    }
}