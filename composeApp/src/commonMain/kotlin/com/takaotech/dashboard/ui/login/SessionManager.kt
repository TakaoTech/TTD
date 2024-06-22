package com.takaotech.dashboard.ui.login

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import co.touchlab.kermit.Logger
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.isSuccess
import com.github.kittinunf.result.onSuccess
import com.takaotech.dashboard.AppBuildKonfig
import com.takaotech.dashboard.model.jwt.TakaoSession
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
import kotlinx.serialization.json.Json
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalEncodingApi::class)
abstract class SessionManager(
    private val json: Json,
    protected val logger: Logger,
    protected val googleLogin: GoogleLogin,
    protected val authApi: AuthApi
) {
    //https://github.com/android/kotlin-multiplatform-samples/tree/main/DiceRoller

    //https://github.com/philipplackner/AndroidCrypto
    protected val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val SESSION_KEY = byteArrayPreferencesKey("SESSION_LOGIN")
    private lateinit var sessionDatastore: DataStore<Preferences>

    private lateinit var sessionFlow: StateFlow<TokenPairDao?>
    lateinit var takaoSession: StateFlow<TakaoSession?>

    private lateinit var authKtor: HttpClient


    @OptIn(ExperimentalCoroutinesApi::class)
    fun init() {
        sessionDatastore = initSessionDatastore()
        sessionFlow = sessionDatastore.data.map {
            val sessionEncrypted = it[SESSION_KEY]
            if (sessionEncrypted != null) {
                try {
                    decryptTokens(sessionEncrypted)
                } catch (ex: Exception) {
                    logger.e(ex) { "Error while decrypt session, logout executed" }
                    null
                }
            } else {
                null
            }
        }.stateIn(coroutineScope, SharingStarted.Eagerly, null)

        takaoSession = sessionFlow.map {
            if (it != null) {
                try {
                    TakaoSession(json, it.accessToken)
                } catch (ex: Exception) {
                    logger.e(ex) { "Error while loading session, logout executed" }
                    logout()
                    null
                }
            } else {
                null
            }
        }.stateIn(coroutineScope, SharingStarted.Eagerly, null)

    }

    abstract fun initSessionDatastore(): DataStore<Preferences>
    abstract fun decryptTokens(sessionEncrypted: ByteArray): TokenPairDao?
    abstract fun encryptTokens(tokenPair: TokenPairDao): ByteArray


    fun startGoogleLogin() {
        coroutineScope.launch(Dispatchers.IO) {
            internalGoogleLogin {
                authApi.login(
                    hashedNonce = it.first,
                ) {
                    timeout {
                        requestTimeoutMillis = 1.minutes.inWholeMilliseconds
                        connectTimeoutMillis = 1.minutes.inWholeMilliseconds
                    }

                    bearerAuth(it.second)
                }
            }
        }
    }


    fun startGoogleSignup() {
        coroutineScope.launch(Dispatchers.IO) {
            internalGoogleLogin {
                authApi.signup(
                    hashedNonce = it.first,
                ) {
                    timeout {
                        requestTimeoutMillis = 1.minutes.inWholeMilliseconds
                        connectTimeoutMillis = 1.minutes.inWholeMilliseconds
                    }

                    bearerAuth(it.second)
                }
            }
        }
    }

    private suspend inline fun internalGoogleLogin(crossinline callService: suspend (googleTokenPair: Pair<Nonce, GoogleToken>) -> TokenPairDao?) {
        val googleLoginResult = googleLogin.startLogin()
        if (googleLoginResult.isSuccess()) {
            Result.of<TokenPairDao, Exception> {
                if (AppBuildKonfig.debug) {
                    delay(1.seconds)
                }
                callService(googleLoginResult.value)
            }.onSuccess { tokenPair ->
                try {
                    sessionDatastore.edit {
                        it[SESSION_KEY] = encryptTokens(tokenPair)
                    }

                    installBearer()
                } catch (ex: Exception) {
                    logger.e(ex) { "Error while login" }
                }
            }
        } else {
            //TODO Error on login
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
        try {
            authKtor.plugin(Auth).providers.let {
                it.remove(
                    it.filterIsInstance<BearerAuthProvider>().first()
                )
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Unsuccessful uninstallBearer, Auth module not found" }
        }
    }

    private fun installBearer() {
        authKtor.plugin(Auth).providers.add(
            BearerAuthProvider(
                refreshTokens = {
                    val mOldToken = oldTokens ?: return@BearerAuthProvider null
                    //TODO Manage Error 500, execute logout
                    // Error getRepositories
                    // io.ktor.client.call.NoTransformationFoundException: Expected response body of the type 'class com.takaotech.dashboard.model.session.TokenPairDao (Kotlin reflection is not available)' but was 'class
                    // io.ktor.utils.io.ByteBufferChannel (Kotlin reflection is not available)'
                    // In response from `http://<IP>/session/refresh`
                    // Response status `500 `
                    // Response header `ContentType: null`
                    // Request header `Accept: application/json`
                    try {
                        withContext(coroutineScope.coroutineContext) {
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
                        }
                    } catch (ex: Exception) {
                        null
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


