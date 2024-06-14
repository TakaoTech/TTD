package com.takaotech.dashboard.model.jwt

import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.arrayContaining
import org.hamcrest.collection.IsArrayWithSize
import org.hamcrest.collection.IsEmptyCollection
import org.hamcrest.core.IsCollectionContaining
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

@OptIn(ExperimentalSerializationApi::class)
class ClaimImplTest {
    //    private Gson gson;

    val json = Json { ignoreUnknownKeys = true }

    @JvmField
    @Rule
    var exception: ExpectedException = ExpectedException.none()

    @Before
    fun setUp() {

    }

    @Test
    fun shouldGetBooleanValue() {
        val value: JsonElement = JsonPrimitive(true)
        val claim = Claim(value)

        assertThat(claim.asBoolean(), CoreMatchers.`is`(CoreMatchers.notNullValue()))
        assertThat(claim.asBoolean(), CoreMatchers.`is`(true))
    }

    @Test
    fun shouldGetNullBooleanIfNotPrimitiveValue() {
        val value: JsonElement = JsonPrimitive(null)
        val claim = Claim(value)

        assertThat(claim.asBoolean(), CoreMatchers.`is`(CoreMatchers.nullValue()))
    }

    @Test
    fun shouldGetIntValue() {
        val value: JsonElement = JsonPrimitive(123)
        val claim = Claim(value)

        assertThat(claim.asInt(), CoreMatchers.`is`(CoreMatchers.notNullValue()))
        assertThat(claim.asInt(), CoreMatchers.`is`(123))
    }

    @Test
    fun shouldGetLongValue() {
        val value: JsonElement = JsonPrimitive(123L)
        val claim = Claim(value)

        assertThat(claim.asLong(), CoreMatchers.`is`(CoreMatchers.notNullValue()))
        assertThat(claim.asLong(), CoreMatchers.`is`(123L))
    }

    @Test
    fun shouldGetNullIntIfNotPrimitiveValue() {
        val value: JsonElement = JsonPrimitive(null)
        val claim = Claim(value)

        assertThat(claim.asInt(), CoreMatchers.`is`(CoreMatchers.nullValue()))
    }

    @Test
    fun shouldGetNullLongIfNotPrimitiveValue() {
        val value: JsonElement = JsonPrimitive(null)
        val claim = Claim(value)

        assertThat(claim.asLong(), CoreMatchers.`is`(CoreMatchers.nullValue()))
    }

    @Test
    fun shouldGetDoubleValue() {
        val value: JsonElement = JsonPrimitive(1.5)
        val claim = Claim(value)

        assertThat(claim.asDouble(), CoreMatchers.`is`(CoreMatchers.notNullValue()))
        assertThat(claim.asDouble(), CoreMatchers.`is`(1.5))
    }

    @Test
    fun shouldGetNullDoubleIfNotPrimitiveValue() {
        val value: JsonElement = JsonPrimitive(null)
        val claim = Claim(value)

        assertThat(claim.asDouble(), CoreMatchers.`is`(CoreMatchers.nullValue()))
    }

    @Test
    fun shouldGetLargeDateValue() {
        val seconds = Int.MAX_VALUE + 10000L
        val value: JsonElement = JsonPrimitive(seconds)
        val claim = Claim(value)

        val date = claim.asInstantFromSeconds()
        MatcherAssert.assertThat(date, CoreMatchers.`is`(CoreMatchers.notNullValue()))
        MatcherAssert.assertThat(date?.toEpochMilliseconds(), CoreMatchers.`is`(seconds * 1000))
        MatcherAssert.assertThat(date?.toEpochMilliseconds(), CoreMatchers.`is`(2147493647L * 1000))
    }

    @Test
    fun shouldGetDateValue() {
        val value: JsonElement = JsonPrimitive("1476824844")
        val claim = Claim(value)

        assertThat(claim.asInstantFromSeconds(), CoreMatchers.`is`(CoreMatchers.notNullValue()))
        assertThat(
            claim.asInstantFromSeconds(),
            CoreMatchers.`is`(Instant.fromEpochMilliseconds(1476824844L * 1000))
        )

        assertThat(
            claim.asInstantFromMilliseconds(),
            CoreMatchers.`is`(Instant.fromEpochMilliseconds(1476824844L))
        )
    }

    @Test
    fun shouldGetNullDateIfNotPrimitiveValue() {
        val value: JsonElement = JsonPrimitive(null)
        val claim = Claim(value)

        assertThat(claim.asInstantFromMilliseconds(), CoreMatchers.`is`(CoreMatchers.nullValue()))
        assertThat(claim.asInstantFromSeconds(), CoreMatchers.`is`(CoreMatchers.nullValue()))
    }

    @Test
    fun shouldGetStringValue() {
        val value: JsonElement = JsonPrimitive("string")
        val claim = Claim(value)

        assertThat(claim.asString(), CoreMatchers.`is`(CoreMatchers.notNullValue()))
        assertThat(claim.asString(), CoreMatchers.`is`("string"))
    }

    @Test
    fun shouldGetNullStringIfNotPrimitiveValue() {
        val value: JsonElement = JsonPrimitive(null)
        val claim = Claim(value)

        assertThat(claim.asString(), CoreMatchers.`is`(CoreMatchers.nullValue()))
    }

    @Test
    fun shouldGetArrayValueOfCustomClass() {

        val value: JsonElement = json.encodeToJsonElement(arrayOf(UserPojo("George", 1), UserPojo("Mark", 2)))
        val claim = Claim(value)

        assertThat(
            json.decodeFromJsonElement<Array<UserPojo>>(claim.value),
            CoreMatchers.`is`(CoreMatchers.notNullValue())
        )
        assertThat(
            json.decodeFromJsonElement<Array<UserPojo>>(claim.value),
            CoreMatchers.`is`(arrayContaining(UserPojo("George", 1), UserPojo("Mark", 2)))
        )

    }

    @Test
    fun shouldGetArrayValue() {
        val value: JsonElement = json.encodeToJsonElement(arrayOf("string1", "string2"))
        val claim = Claim(value)

        assertThat(
            json.decodeFromJsonElement<Array<String>>(claim.value),
            CoreMatchers.`is`(CoreMatchers.notNullValue())
        )
        assertThat(
            json.decodeFromJsonElement<Array<String>>(claim.value),
            CoreMatchers.`is`(arrayContaining("string1", "string2"))
        )
    }

    @Test
    fun shouldGetEmptyArrayIfNullValue() {
        val value: JsonElement = JsonArray(listOf())
        val claim = Claim(value)

        assertThat(
            json.decodeFromJsonElement<Array<String>>(claim.value),
            CoreMatchers.`is`(CoreMatchers.notNullValue())
        )
        assertThat(
            json.decodeFromJsonElement<Array<String>>(claim.value),
            CoreMatchers.`is`(IsArrayWithSize.emptyArray())
        )
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

        assertThat(
            json.decodeFromJsonElement<List<UserPojo>>(claim.value),
            CoreMatchers.`is`(CoreMatchers.notNullValue())
        )
        assertThat(
            json.decodeFromJsonElement<List<UserPojo>>(claim.value),
            CoreMatchers.`is`(IsCollectionContaining.hasItems(UserPojo("George", 1), UserPojo("Mark", 2)))
        )
    }

    @Test
    fun shouldGetListValue() {
        val value: JsonElement = json.encodeToJsonElement(mutableListOf("string1", "string2"))
        val claim = Claim(value)

        assertThat(
            json.decodeFromJsonElement<List<String>>(claim.value),
            CoreMatchers.`is`(CoreMatchers.notNullValue())
        )
        assertThat(
            json.decodeFromJsonElement<List<String>>(claim.value),
            CoreMatchers.`is`(IsCollectionContaining.hasItems("string1", "string2"))
        )
    }

    @Test
    fun shouldGetEmptyListIfNullValue() {
        val value: JsonElement = JsonArray(listOf())
        val claim = Claim(value)

        assertThat(
            json.decodeFromJsonElement<List<String>>(claim.value),
            CoreMatchers.`is`(CoreMatchers.notNullValue())
        )
        assertThat(
            json.decodeFromJsonElement<List<String>>(claim.value), CoreMatchers.`is`(
                IsEmptyCollection.emptyCollectionOf(
                    String::class.java
                )
            )
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