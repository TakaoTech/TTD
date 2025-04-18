package com.takaotech.dashboard.ui.login

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import co.touchlab.kermit.Logger
import com.takaotech.dashboard.AppBuildKonfig
import com.takaotech.dashboard.model.session.TokenPairDao
import com.takaotech.dashboard.repository.AuthApi
import com.takaotech.dashboard.ui.platform.SymmetricCryptoManagerImpl
import com.takaotech.dashboard.ui.utils.createSessionDataStore
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

// @Single
@OptIn(ExperimentalEncodingApi::class)
class SessionManagerImpl(
    json: Json,
    logger: Logger,
    googleLogin: GoogleLogin,
    authApi: AuthApi,
    private val context: Context,
) : SessionManager(json, logger, googleLogin, authApi) {
    private val cryptoManager = SymmetricCryptoManagerImpl(AppBuildKonfig.SESSION_KEY_ALIAS)

    override fun initSessionDatastore(): DataStore<Preferences> = createSessionDataStore(context)

    override fun decryptTokens(sessionEncrypted: ByteArray): TokenPairDao {
        val base = Base64.decode(sessionEncrypted)
        val decoded = cryptoManager.decryptFromByteArrayToByteArray(base)

        return TokenPairDao.parse(decoded.decodeToString())
    }

    override fun encryptTokens(tokenPair: TokenPairDao): ByteArray =
        cryptoManager.encryptFromByteArrayToByteArray(
            tokenPair.toString().toByteArray(),
        )
}
