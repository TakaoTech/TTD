package com.takaotech.dashboard.repository.converter

import co.touchlab.kermit.Logger
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData
import io.ktor.client.statement.*
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal actual fun getUnsuccessResponseConverterFactory(): Converter.Factory {
    return UnsuccessResponseConverterFactoryImpl()
}

internal class UnsuccessResponseConverterFactoryImpl : Converter.Factory, KoinComponent {
    private val logger: Logger by inject()

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
        logger.i(tag = "UnsuccessResponseConverterFactoryImpl") { "Response Type $typeData" }
//        if (typeData.typeInfo.type == DarwinHttpRequestException::class) {
//
//        }
//        https://developer.apple.com/documentation/foundation/nsurlresponse
//        if (typeData.typeInfo.type != NSURLResponse::class) {
//            return UnsuccessResponseSuspendConverter(typeData, ktorfit)
//        }
        return null
    }
}