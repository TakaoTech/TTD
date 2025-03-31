package com.takaotech.dashboard.repository.converter

import de.jensklingenberg.ktorfit.converter.Converter
import io.ktor.client.statement.*
import kotlinx.serialization.Transient

internal expect fun getUnsuccessResponseConverterFactory(): Converter.Factory

class KtorfitHttpException(
    @Transient
    val response: HttpResponse,
    val bodyText: String,
) : RuntimeException() {
    override val message: String
        get() = "HTTP " + response.status.value + " " + bodyText
}
