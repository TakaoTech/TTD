package com.takaotech.dashboard.ui.login

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import co.touchlab.kermit.Logger
import com.takaotech.dashboard.AppBuildKonfig
import com.takaotech.dashboard.model.session.TokenPairDao
import com.takaotech.dashboard.repository.AuthApi
import com.takaotech.dashboard.ui.platform.SymmetricCryptoManager2
import com.takaotech.dashboard.ui.utils.getSessionDatastore
import com.takaotech.dashboard.ui.utils.sessionDataStoreFileName
import kotlinx.serialization.json.Json
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class SessionManagerImpl(
    json: Json,
    logger: Logger,
    googleLogin: GoogleLogin,
    authApi: AuthApi,
) : SessionManager(json, logger, googleLogin, authApi) {
    private val cryptoManager = SymmetricCryptoManager2(AppBuildKonfig.SESSION_KEY_ALIAS)

    override fun initSessionDatastore(): DataStore<Preferences> =
        getSessionDatastore {
            val documentsPath = NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory,
                NSUserDomainMask,
                true
            ).first() as String
            // TODO Check path is correct
            "$documentsPath/$sessionDataStoreFileName"
        }

    override fun decryptTokens(sessionEncrypted: ByteArray): TokenPairDao {
        val base = Base64.decode(sessionEncrypted)
        val decoded = cryptoManager.decryptFromByteArray(base)

        return TokenPairDao.parse(decoded.decodeToString())
    }

    override fun encryptTokens(tokenPair: TokenPairDao): ByteArray =
        cryptoManager.encryptFromByteArrayToByteArray(
            tokenPair.toString().encodeToByteArray(),
        )
}
