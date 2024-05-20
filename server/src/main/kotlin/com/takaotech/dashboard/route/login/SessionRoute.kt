package com.takaotech.dashboard.route.login

import io.ktor.resources.*

@Resource("/session")
class SessionRoute {
    @Resource("login")
    class Login(val parent: SessionRoute = SessionRoute())
}