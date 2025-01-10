package com.takaotech.dashboard.model.jwt

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(with = JWTPayloadSerializer::class)
class JWTPayload(
    @SerialName("iss")
    val iss: String? = null,
    @SerialName("sub")
    val sub: String? = null,
    @Serializable(InstantSecondsSerializer::class)
    @SerialName("exp")
    val exp: Instant? = null,
    @Serializable(InstantSecondsSerializer::class)
    @SerialName("nbf")
    val nbf: Instant? = null,
    @Serializable(InstantSecondsSerializer::class)
    @SerialName("iat")
    val iat: Instant? = null,
    @SerialName("jti")
    val jti: String? = null,
    @SerialName("aud")
    val aud: List<String>,
    val tree: Map<String, Claim>,
) {
    fun claimForName(name: String): Claim? {
        val claim = tree[name] ?: return null
        return claim
    }
}

object InstantSecondsSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.LONG)

    override fun serialize(
        encoder: Encoder,
        value: Instant,
    ) {
        encoder.encodeLong(value.epochSeconds)
    }

    override fun deserialize(decoder: Decoder): Instant = Instant.fromEpochSeconds(decoder.decodeLong())
}

object JWTPayloadSerializer : KSerializer<JWTPayload> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("JWT") {
            element<String>("iss", isOptional = true)
            element<String>("sub", isOptional = true)
            element<Long>("exp", isOptional = true)
            element<Long>("nbf", isOptional = true)
            element<Long>("iat", isOptional = true)
            element<String>("jti", isOptional = true)
            element<List<String>>("aud", isOptional = true)
        }

    override fun serialize(
        encoder: Encoder,
        value: JWTPayload,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder ?: throw SerializationException("This class can be saved only by Json")

        val jsonMap =
            mutableMapOf(
                "iss" to JsonPrimitive(value.iss),
                "sub" to JsonPrimitive(value.sub),
                "exp" to JsonPrimitive(value.exp?.epochSeconds),
                "nbf" to JsonPrimitive(value.nbf?.epochSeconds),
                "iat" to JsonPrimitive(value.iat?.epochSeconds),
                "jti" to JsonPrimitive(value.jti),
                "aud" to Json.encodeToJsonElement(value.aud),
            ).also { jsonMap ->
                value.tree
                    .mapValues {
                        Json.encodeToJsonElement(it.value.value)
                    }.let {
                        jsonMap.putAll(it)
                    }
            }

        jsonEncoder.encodeJsonElement(JsonObject(jsonMap))
    }

    override fun deserialize(decoder: Decoder): JWTPayload {
        val jsonDecoder =
            decoder as? JsonDecoder ?: throw SerializationException("This class can be loaded only by Json")
        val jsonElement = jsonDecoder.decodeJsonElement()

        // Convert JsonElement to JsonObject to access individual fields
        val jsonObject = jsonElement as JsonObject
        val iss = jsonObject["iss"]?.jsonPrimitive?.contentOrNull
        val sub = jsonObject["sub"]?.jsonPrimitive?.contentOrNull
        val exp = jsonObject["exp"]?.jsonPrimitive?.longOrNull?.let { Instant.fromEpochSeconds(it) }
        val nbf = jsonObject["nbf"]?.jsonPrimitive?.longOrNull?.let { Instant.fromEpochSeconds(it) }
        val iat = jsonObject["iat"]?.jsonPrimitive?.longOrNull?.let { Instant.fromEpochSeconds(it) }
        val jti = jsonObject["jti"]?.jsonPrimitive?.contentOrNull
        val aud = getStringOrArray(jsonObject, "aud")

        return JWTPayload(
            iss = iss,
            sub = sub,
            exp = exp,
            nbf = nbf,
            iat = iat,
            jti = jti,
            aud = aud,
            tree =
                jsonObject.mapValues {
                    Claim(it.value)
                },
        )
    }

    private fun getStringOrArray(
        obj: JsonObject,
        claimName: String,
    ): List<String> {
        var list: MutableList<String?> = mutableListOf()
        if (obj.containsKey(claimName)) {
            val arrElement = obj[claimName]
            if (arrElement is JsonArray) {
                val jsonArr: JsonArray = arrElement.jsonArray
                jsonArr.forEach {
                    list.add(it.jsonPrimitive.content)
                }
            } else {
                list = mutableListOf(arrElement?.jsonPrimitive?.contentOrNull)
            }
        }
        return list.filterNotNull().toList()
    }
}
