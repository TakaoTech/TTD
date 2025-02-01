package com.takaotech.dashboard.di

import co.touchlab.kermit.Logger
import com.takaotech.dashboard.AppBuildKonfig
import com.takaotech.dashboard.ui.login.GoogleLogin
import com.takaotech.dashboard.ui.login.GoogleLoginImpl
import com.takaotech.dashboard.ui.login.SessionManager
import com.takaotech.dashboard.ui.login.SessionManagerImpl
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.http.*
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule

@Suppress("SpreadOperator")
actual fun KoinApplication.platformModules() {
    modules(
        defaultModule,
        module {
            single { GoogleLoginImpl(get(), get()) } bind (GoogleLogin::class)
            single {
                SessionManagerImpl(
                    json = get(),
                    logger = get(),
                    googleLogin = get(),
                    authApi = get(),
                    context = get(),
                ).apply {
                    init()
                }
            } bind SessionManager::class withOptions {
                createdAtStart()
            }
        },
        *appModules(),
    )
}

internal actual fun getBaseKtor(kermitLogger: Logger) =
    HttpClient(OkHttp) {
        engine {
//        https {
//            trustManager = SslSettings.getTrustManager()
//        }

            preconfigured = SslSettings.getOkHttpClient()
        }

        configureCommonHttp(kermitLogger)
    }

// https://medium.com/@MrHardikTrivedi/public-key-pinning-using-ktor-for-android-and-ios-kmm-61066cb34321

object SslSettings {
    private val pin =
        arrayOf(
            AppBuildKonfig.CERT_PIN1,
        )

    internal fun getOkHttpClient(): OkHttpClient {
        val certificatePinner =
            CertificatePinner
                .Builder()
                .add(pattern = Url(AppBuildKonfig.baseUrl).host, pins = pin)
                .build()
        return OkHttpClient
            .Builder()
            .certificatePinner(certificatePinner)
            .build()
    }

//    fun getKeyStore(): KeyStore {
//        val keyStoreFile = FileInputStream("keystore.jks")
//        val keyStorePassword = "foobar".toCharArray()
//        val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
//        keyStore.load(keyStoreFile, keyStorePassword)
//        return keyStore
//    }
//
//    fun getTrustManagerFactory(): TrustManagerFactory? {
//        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
//        trustManagerFactory.init(getKeyStore())
//        return trustManagerFactory
//    }

//    fun getSslContext(): SSLContext? {
//        val sslContext = SSLContext.getInstance("TLS")
//        sslContext.init(null, getTrustManagerFactory()?.trustManagers, null)
//        return sslContext
//    }

//    fun getTrustManager(): X509TrustManager {
//        return getTrustManagerFactory()?.trustManagers?.first { it is X509TrustManager } as X509TrustManager
//    }
}
