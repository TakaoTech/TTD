package com.takaotech.dashboard.ui.login

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.isSuccess
import com.github.kittinunf.result.onFailure
import com.github.kittinunf.result.onSuccess
import com.takaotech.dashboard.model.session.AccessToken
import com.takaotech.dashboard.model.session.RefreshTokenDao
import com.takaotech.dashboard.model.session.TokenPairDao
import com.takaotech.dashboard.repository.AuthApi
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration.Companion.minutes

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

    lateinit var sessionFlow: StateFlow<TokenPairDao?>

    private lateinit var authKtor: HttpClient


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
    abstract fun decryptTokens(sessionEncrypted: ByteArray): TokenPairDao?
    abstract fun encryptTokens(tokenPair: TokenPairDao): ByteArray
    abstract fun decodeToken(token: AccessToken)


    fun startGoogleLogin() {
        coroutineScope.launch(Dispatchers.IO) {
            val googleLoginResult = googleLogin.startLogin()
            if (googleLoginResult.isSuccess()) {
                Result.of<TokenPairDao, Exception> {
                    authApi.login(
                        hashedNonce = googleLoginResult.value.first,
                    ) {
                        timeout {
                            requestTimeoutMillis = 1.minutes.inWholeMilliseconds
                            connectTimeoutMillis = 1.minutes.inWholeMilliseconds
                        }

                        bearerAuth(googleLoginResult.value.second)
                    }
                }.onSuccess { tokenPair ->
                    sessionDatastore.edit {
                        it[SESSION_KEY] = encryptTokens(tokenPair)
                    }
                    decodeToken(tokenPair.accessToken)
                    installBearer()
                }.onFailure {
                    //TODO Error Takao Login
                    it
                }
            } else {
                //TODO Error google on login
            }

        }
    }


    fun startGoogleSignup() {
        coroutineScope.launch(Dispatchers.IO) {
            val googleLoginResult = googleLogin.startLogin()
            if (googleLoginResult.isSuccess()) {
                Result.of<TokenPairDao, Exception> {
                    authApi.signup(
                        hashedNonce = googleLoginResult.value.first,
                    ) {
                        bearerAuth(googleLoginResult.value.second)
                    }
                }.onSuccess { tokenPair ->
                    sessionDatastore.edit {
                        it[SESSION_KEY] = encryptTokens(tokenPair)
                    }

                    installBearer()
                }
            } else {
                //TODO Error on login
            }

        }
    }

    fun logout() {
        coroutineScope.launch {
            uninstallBearer()
            sessionDatastore.edit {
                it.remove(SESSION_KEY)
            }
        }
    }

    fun bindKtor(baseKtor: HttpClient) {
        authKtor = baseKtor
        installBearer()
    }

    private fun uninstallBearer() {
        authKtor.plugin(Auth).providers.let {
            it.remove(
                it.filterIsInstance<BearerAuthProvider>().first()
            )
        }
    }

    private fun installBearer() {
        authKtor.plugin(Auth).providers.add(
            BearerAuthProvider(
                refreshTokens = {
                    val mOldToken = oldTokens ?: return@BearerAuthProvider null
                    authApi.refresh(RefreshTokenDao(mOldToken.refreshToken),
                        ext = {
                            timeout {
                                requestTimeoutMillis = 1.minutes.inWholeMilliseconds
                                connectTimeoutMillis = 1.minutes.inWholeMilliseconds
                            }
                        }
                    ).let {
                        BearerTokens(it.accessToken, it.refreshToken)
                    }
                },
                loadTokens = {
                    val pair = sessionFlow.value
                    if (pair != null) {
                        BearerTokens(pair.accessToken, pair.refreshToken)
                    } else {
                        null
                    }
                },
                realm = "TTD"
            )
        )
    }
}


