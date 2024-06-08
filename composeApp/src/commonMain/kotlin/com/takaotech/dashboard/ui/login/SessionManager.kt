package com.takaotech.dashboard.ui.login

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.isSuccess
import com.github.kittinunf.result.onSuccess
import com.takaotech.dashboard.model.session.TokenPair
import com.takaotech.dashboard.repository.AuthApi
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
abstract class SessionManager(
    protected val googleLogin: GoogleLogin,
    protected val authApi: AuthApi,
) {
    //https://github.com/android/kotlin-multiplatform-samples/tree/main/DiceRoller

    //https://github.com/philipplackner/AndroidCrypto
    protected val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val SESSION_KEY = byteArrayPreferencesKey("SESSION_LOGIN")
    private lateinit var sessionDatastore: DataStore<Preferences>

    lateinit var sessionFlow: StateFlow<TokenPair?>


    fun init() {
        sessionDatastore = initSessionDatastore()
        sessionFlow = sessionDatastore.data.map {
            val sessionEncrypted = it[SESSION_KEY]
            if (sessionEncrypted != null) {
                decryptTokens(sessionEncrypted)
            } else {
                null
            }
        }.stateIn(coroutineScope, SharingStarted.Eagerly, null)
    }

    abstract fun initSessionDatastore(): DataStore<Preferences>
    abstract fun decryptTokens(sessionEncrypted: ByteArray): TokenPair?
    abstract fun encryptTokens(tokenPair: TokenPair): ByteArray


    fun startGoogleLogin() {
        coroutineScope.launch(Dispatchers.IO) {
            val googleLoginResult = googleLogin.startLogin()
            if (googleLoginResult.isSuccess()) {
                Result.of<TokenPair, Exception> {
                    authApi.signup(
                        hashedNonce = googleLoginResult.value.first,
                    ) {
                        bearerAuth(googleLoginResult.value.second)
                    }
                }.onSuccess { tokenPair ->
                    sessionDatastore.edit {
                        it[SESSION_KEY] = encryptTokens(tokenPair)
                    }
                }
            } else {
                //TODO Error on login
            }

        }
    }


    fun startGoogleSignup() {
        coroutineScope.launch(Dispatchers.IO) {
            val googleLoginResult = googleLogin.startLogin()
            if (googleLoginResult.isSuccess()) {
                Result.of<TokenPair, Exception> {
                    authApi.signup(
                        hashedNonce = googleLoginResult.value.first,
                    ) {
                        bearerAuth(googleLoginResult.value.second)
                    }
                }.onSuccess { tokenPair ->
                    sessionDatastore.edit {
                        it[SESSION_KEY] = encryptTokens(tokenPair)
                    }
                }


            } else {
                //TODO Error on login
            }

        }
    }

    fun logout() {
        coroutineScope.launch {
            sessionDatastore.edit {
                it.remove(SESSION_KEY)
            }
        }
    }
}


