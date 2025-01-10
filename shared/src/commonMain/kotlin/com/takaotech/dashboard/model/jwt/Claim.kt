package com.takaotech.dashboard.model.jwt

import kotlinx.datetime.Instant
import kotlinx.serialization.json.*
import kotlin.jvm.JvmInline

/**
 * Claim
 *
 * @property value The Claim class wrap JsonElement
 * @constructor Create Claim
 */
@JvmInline
value class Claim(
    val value: JsonElement,
) {
    /**
     * Get this Claim as a Boolean.
     * If the value isn't of type Boolean or it can't be converted to a Boolean, null will be returned.
     *
     * @return the value as a Boolean or null.
     */
    fun asBoolean(): Boolean? =
        if (value is JsonPrimitive) {
            value.booleanOrNull
        } else {
            null
        }

    /**
     * Get this Claim as an Integer.
     * If the value isn't of type Integer or it can't be converted to an Integer, null will be returned.
     *
     * @return the value as an Integer or null.
     */
    fun asInt(): Int? {
        if (value !is JsonPrimitive) {
            return null
        }
        return value.jsonPrimitive.intOrNull
    }

    /**
     * Get this Claim as an Long.
     * If the value isn't of type Long or it can't be converted to an Long, null will be returned.
     *
     * @return the value as an Long or null.
     */
    fun asLong(): Long? =
        if (value is JsonPrimitive) {
            value.longOrNull
        } else {
            null
        }

    /**
     * Get this Claim as a Double.
     * Izf the value isn't of type Double or it can't be converted to a Double, null will be returned.
     *
     * @return the value as a Double or null.
     */
    fun asDouble(): Double? =
        if (value is JsonPrimitive) {
            value.doubleOrNull
        } else {
            null
        }

    /**
     * Get this Claim as a String.
     * If the value isn't of type String or it can't be converted to a String, null will be returned.
     *
     * @return the value as a String or null.
     */
    fun asString(): String? =
        if (value is JsonPrimitive) {
            value.contentOrNull
        } else {
            null
        }

    fun asInstantFromMilliseconds(): Instant? =
        if (value is JsonPrimitive) {
            value.longOrNull?.let { Instant.fromEpochMilliseconds(it) }
        } else {
            null
        }

    fun asInstantFromSeconds(): Instant? =
        if (value is JsonPrimitive) {
            value.longOrNull?.let { Instant.fromEpochSeconds(it) }
        } else {
            null
        }

//    @Nullable
//    fun asDate(): Date? {
//        if (!value.isJsonPrimitive()) {
//            return null
//        }
//        val ms: Long = value.getAsString().toLong() * 1000
//        return Date(ms)
//    }

//    @Throws(DecodeException::class)
//    fun <T> asArray(tClazz: java.lang.Class<T>?): Array<T> {
//        try {
//            if (!value.isJsonArray() || value.isJsonNull()) {
//                return Array.newInstance(tClazz, 0) as Array<T>
//            }
//            val gson: Gson = Gson()
//            val jsonArr: JsonArray = value.getAsJsonArray()
//            val arr = Array.newInstance(tClazz, jsonArr.size()) as Array<T>
//            for (i in 0 until jsonArr.size()) {
//                arr[i] = gson.fromJson(jsonArr.get(i), tClazz)
//            }
//            return arr
//        } catch (e: JsonSyntaxException) {
//            throw DecodeException("Failed to decode claim as array", e)
//        }
//    }
//
//    @Throws(DecodeException::class)
//    fun <T> asList(tClazz: java.lang.Class<T>?): List<T> {
//        try {
//            if (!value.isJsonArray() || value.isJsonNull()) {
//                return ArrayList()
//            }
//            val gson: Gson = Gson()
//            val jsonArr: JsonArray = value.getAsJsonArray()
//            val list: List<T> = ArrayList()
//            for (i in 0 until jsonArr.size()) {
//                list.add(gson.fromJson(jsonArr.get(i), tClazz))
//            }
//            return list
//        } catch (e: JsonSyntaxException) {
//            throw DecodeException("Failed to decode claim as list", e)
//        }
//    }
//
//    @Throws(DecodeException::class)
//    fun <T> asObject(tClazz: java.lang.Class<T>): T? {
//        try {
//            if (value.isJsonNull()) {
//                return null
//            }
//            return Gson().fromJson(value, tClazz)
//        } catch (e: JsonSyntaxException) {
//            throw DecodeException("Failed to decode claim as " + tClazz.getSimpleName(), e)
//        }
//    }
}
