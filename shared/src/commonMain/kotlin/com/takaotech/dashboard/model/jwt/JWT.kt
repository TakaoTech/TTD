package com.takaotech.dashboard.model.jwt

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration

/**
 * Wrapper class for values contained inside a Json Web Token (JWT).
 */
class JWT(token: String) {
    private val json = Json { ignoreUnknownKeys = true }

    private val token: String

    /**
     * Get the Header values from this JWT as a Map of Strings.
     *
     * @return the Header values of the JWT.
     */
    var header: JsonObject? = null
        private set
    private lateinit var payload: JWTPayload

    /**
     * Get the Signature from this JWT as a Base64 encoded String.
     *
     * @return the Signature of the JWT.
     */
    var signature: String? = null
        private set

    val issuer: String?
        /**
         * Get the value of the "iss" claim, or null if it's not available.
         *
         * @return the Issuer value or null.
         */
        get() = payload.iss

    val subject: String?
        /**
         * Get the value of the "sub" claim, or null if it's not available.
         *
         * @return the Subject value or null.
         */
        get() = payload.sub

    val audience: List<String>
        /**
         * Get the value of the "aud" claim, or an empty list if it's not available.
         *
         * @return the Audience value or an empty list.
         */
        get() = payload.aud

    val expiresAt: Instant?
        /**
         * Get the value of the "exp" claim, or null if it's not available.
         *
         * @return the Expiration Time value or null.
         */
        get() = payload!!.exp

    val notBefore: Instant?
        /**
         * Get the value of the "nbf" claim, or null if it's not available.
         *
         * @return the Not Before value or null.
         */
        get() = payload.nbf

    val issuedAt: Instant?
        /**
         * Get the value of the "iat" claim, or null if it's not available.
         *
         * @return the Issued At value or null.
         */
        get() = payload.iat

    val id: String?
        /**
         * Get the value of the "jti" claim, or null if it's not available.
         *
         * @return the JWT ID value or null.
         */
        get() = payload.jti

    /**
     * Get a Claim given it's name. If the Claim wasn't specified in the JWT payload, a BaseClaim will be returned.
     *
     * @param name the name of the Claim to retrieve.
     * @return a valid Claim or null if not exist
     */
    fun getClaim(name: String): Claim? {
        return payload.claimForName(name)
    }

    val claims: Map<String, Claim?>
        /**
         * Get all the Claims.
         *
         * @return a valid Map of Claims.
         */
        get() = payload.tree

    /**
     * Validates that this JWT was issued in the past and hasn't expired yet.
     *
     * @param leeway the time leeway in seconds in which the token should still be considered valid.
     * @return if this JWT has already expired or not.
     */
    fun isExpired(leeway: Duration): Boolean {
        if (leeway.isNegative()) {
            throw IllegalArgumentException("The leeway must be a positive value.")
        }

        val todayTime = Clock.System.now()

        val futureToday = todayTime + leeway
        val pastToday = todayTime - leeway

        val exp = payload.exp
        val iat = payload.iat

        val expValid = exp == null || pastToday <= exp
        val iatValid = iat == null || futureToday >= (iat)
        return !expValid || !iatValid
    }

    /**
     * Returns the String representation of this JWT.
     *
     * @return the String Token.
     */
    override fun toString(): String {
        return token
    }

//    fun describeContents(): Int {
//        return 0
//    }

//    fun writeToParcel(dest: Parcel, flags: Int) {
//        dest.writeString(token)
//    }

    /**
     * Decode a given string JWT token.
     *
     * @param token the string JWT token.
     * @throws DecodeException if the token cannot be decoded
     */
    init {
        decode(token)
        this.token = token
    }

    // =====================================
    // ===========Private Methods===========
    // =====================================
    private fun decode(token: String) {
        val parts = splitToken(token)
        header = json.parseToJsonElement(base64Decode(parts[0])).jsonObject
        try {
            payload = json.decodeFromString<JWTPayload>(base64Decode(parts[1]))
        } catch (ex: SerializationException) {
            throw DecodeException("The token's payload had an invalid JSON format.", ex)
        }
        signature = parts[2]
    }

    private fun splitToken(token: String): Array<String> {
        var parts = token.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (parts.size == 2 && token.endsWith(".")) {
            //Tokens with alg='none' have empty String as Signature.
            parts = arrayOf(parts[0], parts[1], "")
        }
        if (parts.size != 3) {
            throw DecodeException("The token was expected to have 3 parts, but got ${parts.size}.")
        }
        return parts
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun base64Decode(string: String): String {
        val decoded: String
        try {
            val bytes: ByteArray = Base64.decode(string)
            decoded = bytes.decodeToString()
        } catch (e: IllegalArgumentException) {
            throw DecodeException("Received bytes didn't correspond to a valid Base64 encoded string.", e)
        }
        return decoded
    }
}