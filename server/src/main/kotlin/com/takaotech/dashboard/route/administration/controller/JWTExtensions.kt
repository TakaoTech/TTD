package com.takaotech.dashboard.route.administration.controller

import com.auth0.jwt.interfaces.Payload

fun Payload.getEmail() = getClaim("email").asString()