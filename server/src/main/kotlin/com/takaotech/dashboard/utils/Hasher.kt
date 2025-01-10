package com.takaotech.dashboard.utils

import java.security.MessageDigest
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun String.sha256(): String = hashString(this, "SHA-256")

@OptIn(ExperimentalEncodingApi::class)
private fun hashString(
    input: String,
    algorithm: String,
): String =
    MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
        .let {
            Base64.encode(it)
        }
