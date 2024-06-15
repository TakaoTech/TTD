package com.takaotech.dashboard.ui.login

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import co.touchlab.kermit.Logger
import com.takaotech.dashboard.model.session.TokenPairDao
import com.takaotech.dashboard.repository.AuthApi
import com.takaotech.dashboard.ui.platform.CryptoManager
import com.takaotech.dashboard.ui.utils.createSessionDataStore
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

//@Single
@OptIn(ExperimentalEncodingApi::class)
class SessionManagerImpl(
    json: Json,
    logger: Logger,
    googleLogin: GoogleLogin,
    authApi: AuthApi,
    private val context: Context,
) : SessionManager(json, logger, googleLogin, authApi) {

    private val cryptoManager = CryptoManager()

    override fun initSessionDatastore(): DataStore<Preferences> = createSessionDataStore(context)

    override fun decryptTokens(sessionEncrypted: ByteArray): TokenPairDao {
        val base = Base64.decode(sessionEncrypted)
        val decoded = cryptoManager.decrypt(base.inputStream())

        return TokenPairDao.parse(String(Base64.decode(decoded)))
    }

    override fun encryptTokens(tokenPair: TokenPairDao): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        cryptoManager.encrypt(
            Base64.encode(tokenPair.toString().toByteArray()).toByteArray(),
            byteArrayOutputStream
        )
        return Base64.encodeToByteArray(byteArrayOutputStream.toByteArray())
    }
}