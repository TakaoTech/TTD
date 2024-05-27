package com.takaotech.dashboard.utils

import java.security.MessageDigest
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun String.sha256(): String {
    return hashString(this, "SHA-256")
}

@OptIn(ExperimentalEncodingApi::class)
private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray()).let {
            Base64.encode(it)
        }
}