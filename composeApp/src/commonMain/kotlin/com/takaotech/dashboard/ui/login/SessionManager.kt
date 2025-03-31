package com.takaotech.dashboard.ui.login

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import co.touchlab.kermit.Logger
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.isSuccess
import com.github.kittinunf.result.map
import com.takaotech.dashboard.model.jwt.TakaoSession
import com.takaotech.dashboard.model.role.TakaoRole
import com.takaotech.dashboard.model.session.RefreshTokenDao
import com.takaotech.dashboard.model.session.TokenPairDao
import com.takaotech.dashboard.repository.AuthApi
import com.takaotech.dashboard.repository.converter.KtorfitHttpException
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.minutes

abstract class SessionManager(
    private val json: Json,
    protected val logger: Logger,
    protected val googleLogin: GoogleLogin,
    protected val authApi: AuthApi,
) {
    // https://github.com/android/kotlin-multiplatform-samples/tree/main/DiceRoller

    // https://github.com/philipplackner/AndroidCrypto
    private val mSessionExpiredFlow = MutableSharedFlow<Unit>()
    val sessionExpiredFlow = mSessionExpiredFlow.asSharedFlow()
    protected val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val SESSION_KEY = byteArrayPreferencesKey("SESSION_LOGIN")
    private lateinit var sessionDatastore: DataStore<Preferences>

    private lateinit var sessionFlow: StateFlow<TokenPairDao?>
    lateinit var takaoSession: StateFlow<TakaoSession?>

    private lateinit var authKtor: HttpClient

    fun init() {
        sessionDatastore = initSessionDatastore()
        sessionFlow =
            sessionDatastore.data
                .map {
                    val sessionEncrypted = it[SESSION_KEY]
                    if (sessionEncrypted != null) {
                        try {
                            decryptTokens(sessionEncrypted)
                        } catch (ex: Exception) {
                            logger.e(ex) { "Error while decrypt session, logout executed" }
                            logoutInternal()
                            null
                        }
                    } else {
                        null
                    }
                }.stateIn(coroutineScope, SharingStarted.Eagerly, null)

        takaoSession =
            sessionFlow
                .map {
                    if (it != null) {
                        try {
                            TakaoSession(json, it.accessToken)
                        } catch (ex: Exception) {
                            logger.e(ex) { "Error while loading session, logout executed" }
                            logoutInternal()
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

    private suspend inline fun internalGoogleLogin(
        crossinline callService: suspend (googleTokenPair: Pair<Nonce, GoogleToken>) -> TokenPairDao?,
    ): Result<Unit, Exception> {
        val googleLoginResult = googleLogin.startLogin()
        return if (googleLoginResult.isSuccess()) {
            Result
                .of<TokenPairDao, Exception> {
                    callService(googleLoginResult.value)
                }.map { tokenPair ->
                    sessionDatastore.edit {
                        it[SESSION_KEY] = encryptTokens(tokenPair)
                    }

                    Unit
                }
        } else {
            Result.failure(Exception("Error while login"))
        }
    }

    private fun logoutWithEject() {
        coroutineScope.launch {
            mSessionExpiredFlow.emit(Unit)
            logoutInternal()
        }
    }

    /**
     * Logout with explicit user action
     */
    fun logout() {
        coroutineScope.launch {
            logoutInternal()
        }
    }

    private suspend fun logoutInternal() {
        uninstallBearer()
        sessionDatastore.edit {
            it.remove(SESSION_KEY)
        }
    }

    fun bindKtor(baseKtor: HttpClient) {
        authKtor = baseKtor
    }

    private fun uninstallBearer() {
        authKtor.authProvider<BearerAuthProvider>()?.clearToken()
    }

    suspend fun getRefreshToken(oldTokens: BearerTokens?): BearerTokens? {
        val mOldToken = oldTokens ?: return null
        return try {
            withContext(coroutineScope.coroutineContext + SupervisorJob()) {
                authApi
                    .refresh(
                        RefreshTokenDao(mOldToken.refreshToken!!),
                        ext = {
                            timeout {
                                requestTimeoutMillis = 1.minutes.inWholeMilliseconds
                                connectTimeoutMillis = 1.minutes.inWholeMilliseconds
                            }
                        },
                    ).let {
                        BearerTokens(it.accessToken, it.refreshToken)
                    }
            }
        } catch (ex: KtorfitHttpException) {
            if (ex.response.status == HttpStatusCode.Unauthorized) {
                logoutWithEject()
            } else {
                logger.e(ex) { "Error while refresh token, unmanaged error" }
            }
            null
        }
    }

    suspend fun loadToken(): BearerTokens? {
        val pair = sessionFlow.value
        return if (pair != null) {
            BearerTokens(pair.accessToken, pair.refreshToken)
        } else {
            null
        }
    }

    fun isAdminFlow() = takaoSession.map { session ->
        session != null && session.roles.contains(TakaoRole.ADMINISTRATOR)
    }
}
