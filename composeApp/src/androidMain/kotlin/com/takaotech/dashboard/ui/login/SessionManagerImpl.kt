package com.takaotech.dashboard.ui.login

import android.content.Context
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import com.takaotech.dashboard.repository.AuthApi
import com.takaotech.dashboard.ui.platform.CryptoManager
import com.takaotech.dashboard.ui.utils.createSessionDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Single
@OptIn(ExperimentalEncodingApi::class)
class SessionManagerImpl(
    private val googleLogin: GoogleLogin,
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

           String(Base64.decode(decoded))
        } else {
            null
        }
    }.stateIn(coroutineScope, SharingStarted.Eagerly, null)


    override fun startGoogleLogin() {
        coroutineScope.launch {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val googleToken = googleLogin.startLogin()
            if (googleToken != null) {
                cryptoManager.encrypt(
                    Base64.encode(googleToken.toByteArray()).toByteArray(),
                    byteArrayOutputStream
                )

                sessionDatastore.edit {
                    it[SESSION_KEY] = Base64.encodeToByteArray(byteArrayOutputStream.toByteArray())
                }

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