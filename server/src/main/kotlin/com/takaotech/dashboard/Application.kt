package com.takaotech.dashboard

import com.takaotech.dashboard.plugins.auth.configureAuth
import com.takaotech.dashboard.plugins.configureHTTP
import com.takaotech.dashboard.plugins.configureKoin
import com.takaotech.dashboard.plugins.configureMonitoring
import com.takaotech.dashboard.plugins.configureRouting
import com.takaotech.dashboard.plugins.configureSerialization
import com.takaotech.dashboard.plugins.initExposed
import com.takaotech.dashboard.plugins.installCors
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.tomcat.jakarta.EngineMain.main(args)
//    Tomcat
//    embeddedServer(
//        factory = Tomcat,
//
//    ).start(wait = true)
}

fun Application.mainModule() {
    // https://ktor.io/docs/connection-pooling-caching.html#connection-settings-config

    installCors()
    configureKoin()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    initExposed()
    configureAuth()
    configureRouting()
}
