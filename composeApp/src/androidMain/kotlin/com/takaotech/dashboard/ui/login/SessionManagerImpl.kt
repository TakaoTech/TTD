package com.takaotech.dashboard.ui.login

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.takaotech.dashboard.model.session.TokenPair
import com.takaotech.dashboard.repository.AuthApi
import com.takaotech.dashboard.ui.platform.CryptoManager
import com.takaotech.dashboard.ui.utils.createSessionDataStore
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

//@Single
@OptIn(ExperimentalEncodingApi::class)
class SessionManagerImpl(
    googleLogin: GoogleLogin,
    authApi: AuthApi,
    private val context: Context
) : SessionManager(googleLogin, authApi) {

    private val cryptoManager = CryptoManager()

    override fun initSessionDatastore(): DataStore<Preferences> = createSessionDataStore(context)

    override fun decryptTokens(sessionEncrypted: ByteArray): TokenPair {
        val base = Base64.decode(sessionEncrypted)
        val decoded = cryptoManager.decrypt(base.inputStream())

        return TokenPair.parse(String(Base64.decode(decoded)))
    }

    override fun encryptTokens(tokenPair: TokenPair): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        cryptoManager.encrypt(
            Base64.encode(tokenPair.toString().toByteArray()).toByteArray(),
            byteArrayOutputStream
        )
        return Base64.encodeToByteArray(byteArrayOutputStream.toByteArray())
    }
}