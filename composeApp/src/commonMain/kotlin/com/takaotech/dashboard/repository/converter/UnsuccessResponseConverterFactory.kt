package com.takaotech.dashboard.repository.converter

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData
import io.ktor.client.statement.*
import io.ktor.http.*
import okhttp3.Response

internal class UnsuccessResponseConverterFactory : Converter.Factory {
    class UnsuccessResponseSuspendConverter(
        val typeData: TypeData,
        val ktorfit: Ktorfit
    ) : Converter.SuspendResponseConverter<HttpResponse, Any?> {
        override suspend fun convert(result: KtorfitResult): Any =
            when (result) {
                is KtorfitResult.Failure -> {
                    throw result.throwable
                }

                is KtorfitResult.Success -> {
                    if (result.response.status.isSuccess()) {
                        result.response.call.body(typeData.typeInfo)
                    } else {
                        throw KtorfitHttpException(result.response, result.response.bodyAsText())
                    }
                }
            }
    }

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, Any?>? {
        if (typeData.typeInfo.type != Response::class) {
            return UnsuccessResponseSuspendConverter(typeData, ktorfit)
        }
        return null
    }

}

class KtorfitHttpException(
    @Transient public val response: HttpResponse,
    public val bodyText: String,
) : RuntimeException() {
    override val message: String
        get() = "HTTP " + response.status.value + " " + bodyText
}
