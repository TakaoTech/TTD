package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.utils.RedisDatabase
import eu.vendeli.rethis.commands.del
import eu.vendeli.rethis.commands.exists
import eu.vendeli.rethis.commands.get
import eu.vendeli.rethis.commands.jsonGet
import eu.vendeli.rethis.commands.jsonSet
import eu.vendeli.rethis.commands.set
import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.days

interface GithubColorController {

    suspend fun getColorLanguageByName(language: String): String
}

@Single(binds = [GithubColorController::class])
class GithubColorControllerImpl(
    private val redisDatabase: RedisDatabase,
    private val client: HttpClient = HttpClient(Java),
) : GithubColorController {
    private var mutex = Mutex()
    lateinit var colorMapping: JsonObject

    override suspend fun getColorLanguageByName(language: String): String {
        mutex.withLock {
            if (!::colorMapping.isInitialized) {
                colorMapping = if (checkNeedUpdate()) {
                    getColorLanguagesMappingRemote().also {
                        setColorLanguagesMappingLocal(it)
                    }
                } else {
                    getColorLanguagesMappingLocal()
                }
            } else {
                if (checkNeedUpdate()) {
                    colorMapping = getColorLanguagesMappingRemote().also {
                        setColorLanguagesMappingLocal(it)
                    }
                }
            }
        }

        return colorMapping[language]
            ?.jsonObject
            ?.get("color")
            ?.jsonPrimitive
            ?.contentOrNull ?: FALLBACK_COLOR
    }

    private suspend fun getColorLanguagesMappingRemote(): JsonObject =
        client.get(COLOR_MAP_ENDPOINT)
            .bodyAsText()
            .run {
                Json.decodeFromString(this)
            }

    private suspend fun checkNeedUpdate(): Boolean {
        return if (existColorLanguagesMappingLocal()) {
            val lastUpdate = getLastUpdateMapping()

            if (lastUpdate != null) {
                lastUpdate.plus(LAST_UPDATE_DELAY) < Clock.System.now()
            } else {
                true
            }
        } else {
            true
        }
    }

    private suspend fun getLastUpdateMapping(): Instant? {
        return redisDatabase.client.get(LAST_UPDATE_GH_COLOR_MAPPING)?.let {
            Instant.parse(it)
        }
    }

    private suspend fun setLastUpdateMapping(lastUpdate: Instant?) {
        if (lastUpdate != null) {
            redisDatabase.client.set(LAST_UPDATE_GH_COLOR_MAPPING, lastUpdate.toString())
        } else {
            redisDatabase.client.del(LAST_UPDATE_GH_COLOR_MAPPING)
        }
    }

    private suspend fun existColorLanguagesMappingLocal(): Boolean {
        return redisDatabase.client.exists(LAST_UPDATE_GH_COLOR_MAPPING, GH_COLOR_MAPPING).toInt() == 2
    }

    private suspend fun getColorLanguagesMappingLocal(): JsonObject {
        return redisDatabase.client.jsonGet<JsonObject>(GH_COLOR_MAPPING)!!
    }

    private suspend fun setColorLanguagesMappingLocal(colorMapping: JsonObject) {
        redisDatabase.client.jsonSet(GH_COLOR_MAPPING, colorMapping)
        setLastUpdateMapping(Clock.System.now())
    }


    companion object {
        private const val COLOR_MAP_ENDPOINT = "https://raw.githubusercontent.com/ozh/github-colors/master/colors.json"

        private const val LAST_UPDATE_GH_COLOR_MAPPING = "LAST_UPDATE_GH_COLOR_MAPPING"
        private val LAST_UPDATE_DELAY = 30.days
        private const val GH_COLOR_MAPPING = "GH_COLOR_MAPPING"

        const val FALLBACK_COLOR = "#ededed"
    }
}
