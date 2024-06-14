package com.takaotech.dashboard.model.jwt

import kotlinx.datetime.Instant
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasEntry
import org.hamcrest.Matchers.hasSize
import org.hamcrest.core.IsCollectionContaining
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.nio.charset.Charset
import java.util.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration.Companion.seconds


class JWTTest {
    @JvmField
    @Rule
    var exception: ExpectedException = ExpectedException.none()

    // Exceptions
    @Test
    fun shouldThrowIfLessThan3Parts() {
        exception.expect(DecodeException::class.java)
        exception.expectMessage("The token was expected to have 3 parts, but got 2.")
        JWT("two.parts")
    }

    @Test
    fun shouldThrowIfMoreThan3Parts() {
        exception.expect(DecodeException::class.java)
        exception.expectMessage("The token was expected to have 3 parts, but got 4.")
        JWT("this.has.four.parts")
    }

    @Test
    fun shouldThrowIfItsNotBase64Encoded() {
        exception.expect(DecodeException::class.java)
        exception.expectMessage("Received bytes didn't correspond to a valid Base64 encoded string.")
        JWT("thisIsNot.Base64_Enc.oded")
    }

    @Test
    fun shouldThrowIfPayloadHasInvalidJSONFormat() {
        exception.expect(DecodeException::class.java)
        exception.expectMessage("The token's payload had an invalid JSON format.")
        JWT("eyJhbGciOiJIUzI1NiJ9.e30ijfe923.XmNK3GpH3Ys_7lyQ")
    }

    // toString
    @Test
    fun shouldGetStringToken() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.e30.XmNK3GpH3Ys_7wsYBfq4C3M6goz71I7dTgUkuIa5lyQ")
        assertThat(jwt, `is`(notNullValue()))
        assertThat(jwt.toString(), `is`(notNullValue()))
        assertThat(jwt.toString(), `is`("eyJhbGciOiJIUzI1NiJ9.e30.XmNK3GpH3Ys_7wsYBfq4C3M6goz71I7dTgUkuIa5lyQ"))
    }

    // Parts
    @Test
    fun shouldGetHeader() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.e30.XmNK3GpH3Ys_7wsYBfq4C3M6goz71I7dTgUkuIa5lyQ")
        assertThat(jwt, `is`(notNullValue()))
        assertThat(jwt.header, `is`(instanceOf(Map::class.java)))

        assertThat(jwt.header, `is`(hasEntry("alg", "HS256")))
    }

    @Test
    fun shouldGetSignature() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.e30.XmNK3GpH3Ys_7wsYBfq4C3M6goz71I7dTgUkuIa5lyQ")
        assertThat(jwt, `is`(notNullValue()))
        assertThat(jwt.signature, `is`("XmNK3GpH3Ys_7wsYBfq4C3M6goz71I7dTgUkuIa5lyQ"))
    }

    @Test
    fun shouldGetEmptySignature() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.e30.")
        assertThat(jwt, `is`(notNullValue()))
        assertThat(jwt.signature, `is`(""))
    }

    // Public Claims
    @Test
    fun shouldGetIssuer() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJKb2huIERvZSJ9.SgXosfRR_IwCgHq5lF3tlM-JHtpucWCRSaVuoHTbWbQ")
        assertThat(jwt, `is`(notNullValue()))
        assertThat(jwt.issuer, `is`("John Doe"))
    }

    @Test
    fun shouldGetNullIssuerIfMissing() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.e30.something")
        assertThat(jwt, `is`(notNullValue()))

        assertThat(jwt.issuer, `is`(nullValue()))
    }

    @Test
    fun shouldGetSubject() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUb2szbnMifQ.RudAxkslimoOY3BLl2Ghny3BrUKu9I1ZrXzCZGDJtNs")
        assertThat(jwt, `is`(notNullValue()))
        assertThat(jwt.subject, `is`("Tok3ns"))
    }

    @Test
    fun shouldGetNullSubjectIfMissing() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.e30.something")
        assertThat(jwt, `is`(notNullValue()))

        assertThat(jwt.subject, `is`(nullValue()))
    }

    @Test
    fun shouldGetArrayAudience() {
        val jwt =
            JWT("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOlsiSG9wZSIsIlRyYXZpcyIsIlNvbG9tb24iXX0.Tm4W8WnfPjlmHSmKFakdij0on2rWPETpoM7Sh0u6-S4")
        assertThat(jwt, `is`(notNullValue()))
        assertThat(jwt.audience, `is`(hasSize(3)))
        assertThat(jwt.audience, `is`(hasItems("Hope", "Travis", "Solomon")))
    }

    @Test
    fun shouldGetStringAudience() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJKYWNrIFJleWVzIn0.a4I9BBhPt1OB1GW67g2P1bEHgi6zgOjGUL4LvhE9Dgc")
        assertThat(jwt, `is`(notNullValue()))
//        assertThat(jwt.audience, `is`(hasSize(1)))
        assertThat(jwt.audience, `is`(hasItems("Jack Reyes")))
    }

    @Test
    fun shouldGetEmptyListAudienceIfMissing() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.e30.something")
        assertThat(jwt, `is`(notNullValue()))

//        assertThat(jwt.audience, IsEmptyCollection.< String > empty < kotlin . String ? > ())
    }

    @Test
    fun shouldDeserializeDatesUsingLong() {
        val jwt =
            JWT("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjIxNDc0OTM2NDcsIm5iZiI6MjE0NzQ5MzY0NywiZXhwIjoyMTQ3NDkzNjQ3LCJjdG0iOjIxNDc0OTM2NDd9.txmUJ0UCy2pqTFrEgj49eNDQCWUSW_XRMjMaRqcrgLg")
        assertThat(jwt, `is`(notNullValue()))

        val secs = Int.MAX_VALUE + 10000L
        val expectedDate = Instant.fromEpochMilliseconds(secs * 1000)
        assertThat(jwt.issuedAt, `is`(expectedDate))
        assertThat(jwt.notBefore, `is`(expectedDate))
        assertThat(jwt.expiresAt, `is`(expectedDate))
        assertThat(jwt.getClaim("ctm")?.asInstantFromSeconds(), `is`(expectedDate))
    }

    @Test
    fun shouldGetExpirationTime() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.eyJleHAiOiIxNDc2NzI3MDg2In0.XwZztHlQwnAgmnQvrcWXJloLOUaLZGiY0HOXJCKRaks")
        assertThat(jwt, `is`(notNullValue()))
        assertThat(jwt.expiresAt, `is`(instanceOf(Instant::class.java)))
        val ms = 1476727086L * 1000
//        val expectedDate = Date(ms)
        val expectedDate = Instant.fromEpochMilliseconds(ms)
        assertThat(jwt.expiresAt, `is`(notNullValue()))
        assertThat(jwt.expiresAt, `is`(equalTo(expectedDate)))
    }

    @Test
    fun shouldGetNullExpirationTimeIfMissing() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.e30.something")
        assertThat(jwt, `is`(notNullValue()))

        assertThat(jwt.expiresAt, `is`(nullValue()))
    }

    @Test
    fun shouldGetNotBefore() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOiIxNDc2NzI3MDg2In0.pi3Fi3oFiXk5A5AetDdL0hjVx_rt6F5r_YiG6HoCYDw")
        assertThat(jwt, `is`(notNullValue()))
        assertThat(jwt.notBefore, `is`(instanceOf(Instant::class.java)))
        val ms = 1476727086L * 1000
//        val expectedDate = Date(ms)
        val expectedDate = Instant.fromEpochMilliseconds(ms)
        assertThat(jwt.notBefore, `is`(notNullValue()))
        assertThat(jwt.notBefore, `is`(equalTo(expectedDate)))
    }

    @Test
    fun shouldGetNullNotBeforeIfMissing() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.e30.something")
        assertThat(jwt, `is`(notNullValue()))

        assertThat(jwt.notBefore, `is`(nullValue()))
    }

    @Test
    fun shouldGetIssuedAt() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOiIxNDc2NzI3MDg2In0.u6BxwrO7S0sqDY8-1cUOLzU2uejAJBzQQF8g_o5BAgo")
        assertThat(jwt, `is`(notNullValue()))
        assertThat(jwt.issuedAt, `is`(instanceOf(Instant::class.java)))
        val ms = 1476727086L * 1000
//        val expectedDate = Date(ms)
        val expectedDate = Instant.fromEpochMilliseconds(ms)
        assertThat(jwt.issuedAt, `is`(notNullValue()))
        assertThat(jwt.issuedAt, `is`(equalTo(expectedDate)))
    }

    @Test
    fun shouldGetNullIssuedAtIfMissing() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.e30.something")
        assertThat(jwt, `is`(notNullValue()))

        assertThat(jwt.issuedAt, `is`(nullValue()))
    }

    @Test
    fun shouldGetId() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMjM0NTY3ODkwIn0.m3zgEfVUFOd-CvL3xG5BuOWLzb0zMQZCqiVNQQOPOvA")
        assertThat(jwt, `is`(notNullValue()))
        assertThat(jwt.id, `is`("1234567890"))
    }

    @Test
    fun shouldGetNullIdIfMissing() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.e30.something")
        assertThat(jwt, `is`(notNullValue()))

        assertThat(jwt.id, `is`(nullValue()))
    }

    @Test
    fun shouldNotBeDeemedExpiredWithoutDateClaims() {
        val jwt = customTimeJWT(null, null)
        assertThat(jwt.isExpired(0.seconds), `is`(false))
    }

    @Test
    fun shouldNotBeDeemedExpired() {
        val jwt = customTimeJWT(null, Date().time + 2000)
        assertThat(jwt.isExpired(0.seconds), `is`(false))
    }

    @Test
    fun shouldBeDeemedExpired() {
        val jwt = customTimeJWT(null, Date().time - 2000)
        assertThat(jwt.isExpired(0.seconds), `is`(true))
    }

    @Test
    fun shouldNotBeDeemedExpiredByLeeway() {
        val jwt = customTimeJWT(null, Date().time - 1000)
        assertThat(jwt.isExpired(2.seconds), `is`(false))
    }

    @Test
    fun shouldBeDeemedExpiredByLeeway() {
        val jwt = customTimeJWT(null, Date().time - 2000)
        assertThat(jwt.isExpired(1.seconds), `is`(true))
    }

    @Test
    fun shouldNotBeDeemedFutureIssued() {
        val jwt = customTimeJWT(Date().time - 2000, null)
        assertThat(jwt.isExpired(0.seconds), `is`(false))
    }

    @Test
    fun shouldBeDeemedFutureIssued() {
        val jwt = customTimeJWT(Date().time + 2000, null)
        assertThat(jwt.isExpired(0.seconds), `is`(true))
    }

    @Test
    fun shouldNotBeDeemedFutureIssuedByLeeway() {
        val jwt = customTimeJWT(Date().time + 1000, null)
        assertThat(jwt.isExpired(2.seconds), `is`(false))
    }

    @Test
    fun shouldBeDeemedFutureIssuedByLeeway() {
        val jwt = customTimeJWT(Date().time + 2000, null)
        assertThat(jwt.isExpired(1.seconds), `is`(true))
    }

    @Test
    fun shouldBeDeemedNotTimeValid() {
        val jwt = customTimeJWT(Date().time + 1000, Date().time - 1000)
        assertThat(jwt.isExpired(0.seconds), `is`(true))
    }

    @Test
    fun shouldBeDeemedTimeValid() {
        val jwt = customTimeJWT(Date().time - 1000, Date().time + 1000)
        assertThat(jwt.isExpired(0.seconds), `is`(false))
    }

    @Test
    fun shouldThrowIfLeewayIsNegative() {
        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("The leeway must be a positive value.")
        val jwt = customTimeJWT(null, null)
        jwt.isExpired((-1).seconds)
    }

    @Test
    fun shouldNotRemoveKnownPublicClaimsFromTree() {
        val jwt =
            JWT("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInN1YiI6ImVtYWlscyIsImF1ZCI6InVzZXJzIiwiaWF0IjoxMDEwMTAxMCwiZXhwIjoxMTExMTExMSwibmJmIjoxMDEwMTAxMSwianRpIjoiaWRpZCIsInJvbGVzIjoiYWRtaW4ifQ.jCchxb-mdMTq5EpeVMSQyTp6zSwByKnfl9U-Zc9kg_w")

        assertThat(jwt, `is`(notNullValue()))
        assertThat(jwt.issuer, `is`("auth0"))
        assertThat(jwt.subject, `is`("emails"))
        assertThat(jwt.audience, `is`(IsCollectionContaining.hasItem("users")))
//        assertThat(jwt.issuedAt?.epochSeconds, `is`(10101010L * 1000))
        assertThat(jwt.issuedAt?.epochSeconds, `is`(10101010L))
//        assertThat(jwt.expiresAt?.epochSeconds, `is`(11111111L * 1000))
        assertThat(jwt.expiresAt?.epochSeconds, `is`(11111111L))
//        assertThat(jwt.notBefore?.epochSeconds, `is`(10101011L * 1000))
        assertThat(jwt.notBefore?.epochSeconds, `is`(10101011L))
        assertThat(jwt.id, `is`("idid"))

        assertThat(jwt.getClaim("roles")?.asString(), `is`("admin"))
        assertThat(jwt.getClaim("iss")?.asString(), `is`("auth0"))
        assertThat(jwt.getClaim("sub")?.asString(), `is`("emails"))
        assertThat(jwt.getClaim("aud")?.asString(), `is`("users"))
        assertThat(jwt.getClaim("iat")?.asDouble(), `is`(10101010.0))
        assertThat(jwt.getClaim("exp")?.asDouble(), `is`(11111111.0))
        assertThat(jwt.getClaim("nbf")?.asDouble(), `is`(10101011.0))
        assertThat(jwt.getClaim("jti")?.asString(), `is`("idid"))
    }


    //Private Claims
    @Test
    fun shouldGetNullIsMissing() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.e30.K17vlwhE8FCMShdl1_65jEYqsQqBOVMPUU9IgG-QlTM")
        assertThat(jwt, `is`(notNullValue()))
//        assertThat(jwt.getClaim("notExisting"), `is`(notNullValue()))
        assertThat(jwt.getClaim("notExisting"), `is`(nullValue()))
//        assertThat(jwt.getClaim("notExisting"), `is`(not(instanceOf(ClaimImpl::class.java))))
//        assertThat(jwt.getClaim("notExisting"), `is`(instanceOf(BaseClaim::class.java)))
    }

    @Test
    fun shouldGetClaim() {
        val jwt =
            JWT("eyJhbGciOiJIUzI1NiJ9.eyJvYmplY3QiOnsibmFtZSI6ImpvaG4ifX0.lrU1gZlOdlmTTeZwq0VI-pZx2iV46UWYd5-lCjy6-c4")
        assertThat(jwt, `is`(notNullValue()))
        assertThat(jwt.getClaim("object"), `is`(notNullValue()))
        assertThat(jwt.getClaim("object"), `is`(instanceOf(Claim::class.java)))
    }

    @Test
    fun shouldGetAllClaims() {
        val jwt =
            JWT("eyJhbGciOiJIUzI1NiJ9.eyJvYmplY3QiOnsibmFtZSI6ImpvaG4ifSwic3ViIjoiYXV0aDAifQ.U20MgOAV81c54mRelwYDJiLllb5OVwUAtMGn-eUOpTA")
        assertThat(jwt, `is`(notNullValue()))
        val claims: Map<String, Claim?>? = jwt.claims
        assertThat(claims, `is`(notNullValue()))
        val objectClaim = claims?.get("object")
        assertThat(objectClaim, `is`(notNullValue()))
        assertThat(objectClaim, `is`(instanceOf(Claim::class.java)))
        val extraClaim = claims?.get("sub")
        assertThat(extraClaim, `is`(notNullValue()))
        assertThat(extraClaim!!.asString(), `is`("auth0"))
    }

    @Test
    fun shouldGetEmptyAllClaims() {
        val jwt = JWT("eyJhbGciOiJIUzI1NiJ9.e30.ZRrHA1JJJW8opsbCGfG_HACGpVUMN_a9IV7pAx_Zmeo")
        assertThat(jwt, `is`(notNullValue()))
        val claims: Map<String, Claim?>? = jwt.claims
        assertThat(claims, `is`(notNullValue()))
        assertThat(claims?.isEmpty(), `is`(true))
    }

    //Parcelable
//    @Test
//    fun shouldBeParceled() {
//        val jwtOrigin = JWT("eyJhbGciOiJIUzI1NiJ9.e30.K17vlwhE8FCMShdl1_65jEYqsQqBOVMPUU9IgG-QlTM")
//        assertThat(jwtOrigin, `is`(notNullValue()))
//
//        val bundleOrigin: Bundle = Bundle()
//        bundleOrigin.putParcelable("jwt", jwtOrigin)
//        val parcel: Parcel = Parcel.obtain()
//        bundleOrigin.writeToParcel(parcel, 0)
//
//        //Extract bundle from parcel
//        parcel.setDataPosition(0)
//        val bundleDest: Bundle = parcel.readBundle(JWT::class.java.classLoader)
//        val jwtDest: JWT = bundleDest.getParcelable("jwt")
//
//        assertThat(jwtDest, `is`(notNullValue()))
//        assertThat(bundleOrigin, `is`(not(bundleDest)))
//        assertThat(jwtOrigin, `is`(not(jwtDest)))
//        assertThat(jwtOrigin.toString(), `is`(jwtDest.toString()))
//    }


    //Helper Methods
    /**
     * Creates a new JWT with custom time claims.
     *
     * @param iatMs iat value in MILLISECONDS
     * @param expMs exp value in MILLISECONDS
     * @return a JWT
     */
    private fun customTimeJWT(iatMs: Long?, expMs: Long?): JWT {
        val header = encodeString("{}")
        val bodyBuilder = StringBuilder("{")
        if (iatMs != null) {
            val iatSeconds = iatMs / 1000
            bodyBuilder.append("\"iat\":\"").append(iatSeconds).append("\"")
        }
        if (expMs != null) {
            if (iatMs != null) {
                bodyBuilder.append(",")
            }
            val expSeconds = expMs / 1000
            bodyBuilder.append("\"exp\":\"").append(expSeconds).append("\"")
        }
        bodyBuilder.append("}")
        val body = encodeString(bodyBuilder.toString())
        val signature = "sign"
        return JWT(String.format("%s.%s.%s", header, body, signature))
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun encodeString(source: String): String {
        val bytes: ByteArray =
            Base64.encodeToByteArray(source.toByteArray())
        return String(bytes, Charset.defaultCharset())
    }
}