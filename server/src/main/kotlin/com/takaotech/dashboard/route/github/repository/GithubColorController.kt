package com.takaotech.dashboard.route.github.repository

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.*
import org.koin.core.annotation.Single
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import kotlin.time.Duration.Companion.days

interface GithubColorController {
    suspend fun getColorLanguagesMapping(): JsonObject

    suspend fun getColorLanguageByName(language: String): String
}

@Single
class GithubColorControllerImpl(
    private var ghLanguagesColor: JsonObject? = null,
) : GithubColorController {
    private var mutex = Mutex()

    override suspend fun getColorLanguagesMapping(): JsonObject {
        mutex.withLock {
            val mGhLanguagesColor = ghLanguagesColor
            if (mGhLanguagesColor == null) {
                var localColor = getLocalColors()
                if (localColor != null) {
                    if (checkNeedUpdateColors(Instant.parse(localColor[LAST_UPDATE_FIELD]!!.jsonPrimitive.content))) {
                        localColor = getColorLanguagesMappingRemote()
                        saveColorsLocal(localColor)
                    }
                    ghLanguagesColor = localColor
                    return localColor
                } else {
                    localColor = getColorLanguagesMappingRemote()
                    saveColorsLocal(localColor)
                    ghLanguagesColor = localColor
                    // Mapping isn't null
                    return localColor
                }
            } else {
                if (checkNeedUpdateColors(Instant.parse(mGhLanguagesColor[LAST_UPDATE_FIELD]!!.jsonPrimitive.content))) {
                    val newColorMapping = getColorLanguagesMappingRemote()
                    saveColorsLocal(newColorMapping)
                    ghLanguagesColor = newColorMapping
                    // Mapping isn't null
                    return newColorMapping
                } else {
                    return mGhLanguagesColor
                }
            }
        }
    }

    override suspend fun getColorLanguageByName(language: String): String {
        val colorMapping = getColorLanguagesMapping()

        return colorMapping[language]
            ?.jsonObject
            ?.get("color")
            ?.jsonPrimitive
            ?.contentOrNull ?: FALLBACK_COLOR
    }

    private suspend fun getColorLanguagesMappingRemote(): JsonObject =
        HttpClient()
            .get("https://raw.githubusercontent.com/ozh/github-colors/master/colors.json")
            .bodyAsText()
            .run<String, JsonObject> {
                Json.decodeFromString(this)
            }.toMutableMap()
            .apply {
                put(LAST_UPDATE_FIELD, JsonPrimitive(Clock.System.now().toString()))
            }.run {
                JsonObject(this)
            }

    private fun checkNeedCreateFolder() {
        // TODO Move to configuration
        File("./ghAsset").let {
            if (!it.exists()) {
                it.mkdir()
            }
        }
    }

    private fun getLocalColors(): JsonObject? {
        checkNeedCreateFolder()
        // TODO Move to configuration
        val colorFile = File("./ghAsset/colors.json")
        if (colorFile.exists()) {
            return FileReader(colorFile)
                .use {
                    it.readText()
                }.run {
                    Json.decodeFromString(this)
                }
        }

        return null
    }

    private fun saveColorsLocal(colorsObject: JsonObject) {
        checkNeedCreateFolder()
        // TODO Move to configuration
        FileWriter(File("./ghAsset/colors.json")).use {
            it.write(colorsObject.toString())
        }
    }

    private fun checkNeedUpdateColors(lastUpdate: Instant): Boolean = lastUpdate.plus(30.days) < Clock.System.now()

    companion object {
        private const val LAST_UPDATE_FIELD = "lastUpdate"
        const val FALLBACK_COLOR = "#ededed"
    }
}
