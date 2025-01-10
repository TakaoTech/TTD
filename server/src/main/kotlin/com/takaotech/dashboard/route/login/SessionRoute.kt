package com.takaotech.dashboard.route.login

import io.ktor.resources.*

@Resource("/session")
class SessionRoute {
    @Resource("login")
    class Login(
        val parent: SessionRoute = SessionRoute(),
    )

    @Resource("signup")
    class Signup(
        val parent: SessionRoute = SessionRoute(),
    )

    @Resource("refresh")
    class Refresh(
        val parent: SessionRoute = SessionRoute(),
    )
}
