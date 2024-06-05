package com.takaotech.dashboard.ui.login

import android.content.Context
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.isSuccess
import com.github.kittinunf.result.onSuccess
import com.takaotech.dashboard.model.session.TokenPair
import com.takaotech.dashboard.repository.AuthApi
import com.takaotech.dashboard.ui.platform.CryptoManager
import com.takaotech.dashboard.ui.utils.createSessionDataStore
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

//@Single
@OptIn(ExperimentalEncodingApi::class)
class SessionManagerImpl(
    private val googleLogin: GoogleLogin,
    private val authApi: AuthApi,
    context: Context
) : SessionManager {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val SESSION_KEY = byteArrayPreferencesKey("SESSION_LOGIN")

    private val cryptoManager = CryptoManager()
    private val sessionDatastore = createSessionDataStore(context)

    override val sessionFlow = sessionDatastore.data.map {
        val sessionEncrypted = it[SESSION_KEY]
        if (sessionEncrypted != null) {
            val base = Base64.decode(sessionEncrypted)
            val decoded = cryptoManager.decrypt(base.inputStream())

            TokenPair.parse(String(Base64.decode(decoded)))
        } else {
            null
        }
    }.stateIn(coroutineScope, SharingStarted.Eagerly, null)


    override fun startGoogleLogin() {
        coroutineScope.launch(Dispatchers.IO) {
            val googleLoginResult = googleLogin.startLogin()
            if (googleLoginResult.isSuccess()) {
                Result.of<TokenPair, Exception> {
                    authApi.login(
                        hashedNonce = googleLoginResult.value.first,
                    ) {
                        bearerAuth(googleLoginResult.value.second)
                    }
                }.onSuccess {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    cryptoManager.encrypt(
                        Base64.encode(it.toString().toByteArray()).toByteArray(),
                        byteArrayOutputStream
                    )

                    sessionDatastore.edit {
                        it[SESSION_KEY] = Base64.encodeToByteArray(byteArrayOutputStream.toByteArray())
                    }
                }


            } else {
                //TODO Error on login
            }

        }
    }

    override fun startGoogleSignup() {
        coroutineScope.launch(Dispatchers.IO) {
            val googleLoginResult = googleLogin.startLogin()
            if (googleLoginResult.isSuccess()) {
                Result.of<TokenPair, Exception> {
                    authApi.signup(
                        hashedNonce = googleLoginResult.value.first,
                    ) {
                        bearerAuth(googleLoginResult.value.second)
                    }
                }.onSuccess {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    cryptoManager.encrypt(
                        Base64.encode(it.toString().toByteArray()).toByteArray(),
                        byteArrayOutputStream
                    )

                    sessionDatastore.edit {
                        it[SESSION_KEY] = Base64.encodeToByteArray(byteArrayOutputStream.toByteArray())
                    }
                }


            } else {
                //TODO Error on login
            }

        }
    }

    override fun logout() {
        coroutineScope.launch {
            sessionDatastore.edit {
                it.clear()
            }
        }
    }
}