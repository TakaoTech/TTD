package com.takaotech.dashboard.ui.login

import android.content.Context
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.takaotech.dashboard.ui.platform.CryptoManager
import com.takaotech.dashboard.ui.utils.createSessionDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
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
    private val SESSION_KEY = stringPreferencesKey("SESSION_LOGIN")



    private val cryptoManager = CryptoManager()
    private val sessionDatastore = createSessionDataStore(context)
    override val sessionFlow = sessionDatastore.data.map {
        val sessionEncrypted = it[SESSION_KEY]
        if (sessionEncrypted != null) {
            val base = Base64.decode(sessionEncrypted)
            cryptoManager.decrypt(base.inputStream())
                .decodeToString()
        } else {
            null
        }

    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun startGoogleLogin() {
        coroutineScope.launch {
            val byteArrayOutputStream = ByteArrayOutputStream()

            val googleToken = googleLogin.startLogin()
            if (googleToken != null) {
                cryptoManager.encrypt(
                    googleToken.toByteArray(),
                    byteArrayOutputStream
                )

                sessionDatastore.edit {
                    it[SESSION_KEY] = Base64.encode(byteArrayOutputStream.toByteArray())
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