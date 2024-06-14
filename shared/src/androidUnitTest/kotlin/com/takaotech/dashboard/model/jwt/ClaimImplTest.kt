package com.takaotech.dashboard.model.jwt

import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import kotlin.test.*

@OptIn(ExperimentalSerializationApi::class)
class ClaimImplTest {

    val json = Json { ignoreUnknownKeys = true }

    @Test
    fun shouldGetBooleanValue() {
        val value: JsonElement = JsonPrimitive(true)
        val claim = Claim(value)

        assertNotNull(claim.asBoolean())
        assertTrue { claim.asBoolean() == true }
    }

    @Test
    fun shouldGetNullBooleanIfNotPrimitiveValue() {
        val value: JsonElement = JsonPrimitive(null)
        val claim = Claim(value)

        assertNull(claim.asBoolean())
    }

    @Test
    fun shouldGetIntValue() {
        val value: JsonElement = JsonPrimitive(123)
        val claim = Claim(value)

        assertNotNull(claim.asInt())
        assertEquals(123, claim.asInt())
    }

    @Test
    fun shouldGetLongValue() {
        val value: JsonElement = JsonPrimitive(123L)
        val claim = Claim(value)

        assertNotNull(claim.asLong())
        assertEquals(123L, claim.asLong())
    }

    @Test
    fun shouldGetNullIntIfNotPrimitiveValue() {
        val value: JsonElement = JsonPrimitive(null)
        val claim = Claim(value)

        assertNull(claim.asInt())
    }

    @Test
    fun shouldGetNullLongIfNotPrimitiveValue() {
        val value: JsonElement = JsonPrimitive(null)
        val claim = Claim(value)

        assertNull(claim.asLong())
    }

    @Test
    fun shouldGetDoubleValue() {
        val value: JsonElement = JsonPrimitive(1.5)
        val claim = Claim(value)

        assertNotNull(claim.asDouble())
        assertEquals(1.5, claim.asDouble())
    }

    @Test
    fun shouldGetNullDoubleIfNotPrimitiveValue() {
        val value: JsonElement = JsonPrimitive(null)
        val claim = Claim(value)

        assertNull(claim.asDouble())
    }

    @Test
    fun shouldGetLargeDateValue() {
        val seconds = Int.MAX_VALUE + 10000L
        val value: JsonElement = JsonPrimitive(seconds)
        val claim = Claim(value)

        val date = claim.asInstantFromSeconds()
        assertNotNull(date)
        assertEquals(seconds * 1000, date.toEpochMilliseconds())
        assertEquals(2147493647L * 1000, date.toEpochMilliseconds())
    }

    @Test
    fun shouldGetDateValue() {
        val value: JsonElement = JsonPrimitive("1476824844")
        val claim = Claim(value)

        assertNotNull(claim.asInstantFromSeconds())
        assertEquals(
            Instant.fromEpochMilliseconds(1476824844L * 1000),
            claim.asInstantFromSeconds()
        )

        assertEquals(
            Instant.fromEpochMilliseconds(1476824844L),
            claim.asInstantFromMilliseconds()
        )
    }

    @Test
    fun shouldGetNullDateIfNotPrimitiveValue() {
        val value: JsonElement = JsonPrimitive(null)
        val claim = Claim(value)

        assertNull(claim.asInstantFromMilliseconds())
        assertNull(claim.asInstantFromSeconds())
    }

    @Test
    fun shouldGetStringValue() {
        val value: JsonElement = JsonPrimitive("string")
        val claim = Claim(value)

        assertNotNull(claim.asString())
        assertEquals("string", claim.asString())
    }

    @Test
    fun shouldGetNullStringIfNotPrimitiveValue() {
        val value: JsonElement = JsonPrimitive(null)
        val claim = Claim(value)

        assertNull(claim.asString())
    }

    @Test
    fun shouldGetArrayValueOfCustomClass() {

        val value: JsonElement = json.encodeToJsonElement(arrayOf(UserPojo("George", 1), UserPojo("Mark", 2)))
        val claim = Claim(value)

        assertNotNull(
            json.decodeFromJsonElement<Array<UserPojo>>(claim.value)
        )
        assertTrue {
            arrayOf(UserPojo("George", 1), UserPojo("Mark", 2))
                .contentEquals(json.decodeFromJsonElement<Array<UserPojo>>(claim.value))
        }
    }

    @Test
    fun shouldGetArrayValue() {
        val value: JsonElement = json.encodeToJsonElement(arrayOf("string1", "string2"))
        val claim = Claim(value)

        assertNotNull(json.decodeFromJsonElement<Array<String>>(claim.value))
        assertTrue {
            arrayOf("string1", "string2")
                .contentEquals(json.decodeFromJsonElement<Array<String>>(claim.value))
        }
    }

    @Test
    fun shouldGetEmptyArrayIfNullValue() {
        val value: JsonElement = JsonArray(listOf())
        val claim = Claim(value)

        assertNotNull(json.decodeFromJsonElement<Array<String>>(claim.value))

        assertTrue {
            arrayOf<String>().contentEquals(json.decodeFromJsonElement<Array<String>>(claim.value))
        }
    }

    //Skipped array deserialization delegated to library user
//    @Test
//    fun shouldGetEmptyArrayIfNonArrayValue() {
//        val value: JsonElement = json.encodeToJsonElement(1)
//        val claim = Claim(value)
//
////        assertThat(claim.asArray(String::class.java), CoreMatchers.`is`(CoreMatchers.notNullValue()))
////        assertThat(claim.asArray(String::class.java), CoreMatchers.`is`(IsArrayWithSize.emptyArray<String>()))
//
//        assertThat(json.decodeFromJsonElement<Array<String>>(claim.value), CoreMatchers.`is`(CoreMatchers.notNullValue()))
//        assertThat(json.decodeFromJsonElement<Array<String>>(claim.value), CoreMatchers.`is`(IsArrayWithSize.emptyArray<String>()))
//    }

    //Skipped array deserialization delegated to library user
//    @Test
//    fun shouldThrowIfArrayClassMismatch() {
//        val value: JsonElement = json.encodeToJsonElement(arrayOf("keys", "values"))
//        val claim = Claim(value)
//
//        exception.expect(DecodeException::class.java)
//        claim.asArray(UserPojo::class.java)
//    }

    @Test
    fun shouldGetListValueOfCustomClass() {
        val value: JsonElement = json.encodeToJsonElement(listOf(UserPojo("George", 1), UserPojo("Mark", 2)))
        val claim = Claim(value)

        assertNotNull(json.decodeFromJsonElement<List<UserPojo>>(claim.value))
        assertTrue {
            json.decodeFromJsonElement<List<UserPojo>>(claim.value).containsAll(
                listOf(
                    UserPojo("George", 1), UserPojo("Mark", 2)
                )
            )
        }
    }

    @Test
    fun shouldGetListValue() {
        val value: JsonElement = json.encodeToJsonElement(mutableListOf("string1", "string2"))
        val claim = Claim(value)

        assertNotNull(
            json.decodeFromJsonElement<List<String>>(claim.value)
        )
        assertEquals(
            listOf("string1", "string2"),
            json.decodeFromJsonElement<List<String>>(claim.value)
        )
    }

    @Test
    fun shouldGetEmptyListIfNullValue() {
        val value: JsonElement = JsonArray(listOf())
        val claim = Claim(value)

        assertNotNull(
            json.decodeFromJsonElement<List<String>>(claim.value)
        )
        assertEquals(
            listOf(),
            json.decodeFromJsonElement<List<String>>(claim.value)
        )
    }

    //Skipped
//    @Test
//    fun shouldGetEmptyListIfNonArrayValue() {
//        val value: JsonElement = gson.toJsonTree(1)
//        val claim: ClaimImpl = ClaimImpl(value)
//
//        assertThat(claim.asList(String::class.java), CoreMatchers.`is`(CoreMatchers.notNullValue()))
//        assertThat(
//            claim.asList(String::class.java), CoreMatchers.`is`(
//                IsEmptyCollection.emptyCollectionOf(
//                    String::class.java
//                )
//            )
//        )
//    }

//    @Test
//    fun shouldThrowIfListClassMismatch() {
//        val value: JsonElement = gson.toJsonTree(arrayOf<String>("keys", "values"))
//        val claim: ClaimImpl = ClaimImpl(value)
//
//        exception.expect(DecodeException::class.java)
//        claim.asList(UserPojo::class.java)
//    }
//
//    @Test
//    fun shouldGetAsObject() {
//        val data: UserPojo = UserPojo("George", 1)
//        val userValue: JsonElement = gson.toJsonTree(data)
//        val userClaim: ClaimImpl = ClaimImpl(userValue)
//
//        val intValue: JsonElement = gson.toJsonTree(1)
//        val intClaim: ClaimImpl = ClaimImpl(intValue)
//
//        val booleanValue: JsonElement = gson.toJsonTree(true)
//        val booleanClaim: ClaimImpl = ClaimImpl(booleanValue)
//
//        assertThat(userClaim.asObject(UserPojo::class.java), CoreMatchers.`is`(CoreMatchers.notNullValue()))
//        assertThat(userClaim.asObject(UserPojo::class.java), `is`(UserPojo("George", 1)))
//
//        assertThat(intClaim.asObject(Int::class.java), CoreMatchers.`is`(CoreMatchers.notNullValue()))
//        assertThat(intClaim.asObject(Int::class.java), CoreMatchers.`is`(1))
//
//        assertThat(booleanClaim.asObject(Boolean::class.java), CoreMatchers.`is`(CoreMatchers.notNullValue()))
//        assertThat(booleanClaim.asObject(Boolean::class.java), CoreMatchers.`is`(true))
//    }
//
//    @Test
//    fun shouldGetNullObjectIfNullValue() {
//        val value: JsonElement = gson.toJsonTree(null)
//        val claim: ClaimImpl = ClaimImpl(value)
//
//        assertThat(claim.asObject(UserPojo::class.java), CoreMatchers.`is`(CoreMatchers.nullValue()))
//    }
//
//    @Test
//    fun shouldThrowIfObjectClassMismatch() {
//        val value: JsonElement = gson.toJsonTree(1)
//        val claim: ClaimImpl = ClaimImpl(value)
//
//        exception.expect(DecodeException::class.java)
//        claim.asObject(UserPojo::class.java)
//    }
}