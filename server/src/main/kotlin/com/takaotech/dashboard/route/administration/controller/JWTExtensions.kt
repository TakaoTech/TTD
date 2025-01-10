package com.takaotech.dashboard.route.administration.controller

import com.auth0.jwt.interfaces.Payload

fun Payload.getEmail(): String? = getClaim("email").asString()
