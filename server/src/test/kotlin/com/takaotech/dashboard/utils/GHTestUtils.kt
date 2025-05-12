package com.takaotech.dashboard.utils

import com.takaotech.dashboard.models.GHLanguageDao
import com.takaotech.dashboard.models.GHRepositoryDao
import com.takaotech.dashboard.models.GHUserDao
import com.takaotech.dashboard.models.MainCategory
import com.takaotech.dashboard.models.TagDao
import com.takaotech.dashboard.models.TagNewDao
import com.takaotech.dashboard.route.github.repository.GithubColorControllerImpl
import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.lorem.LoremFaker
import io.kotest.common.DelicateKotest
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.distinct
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.orNull
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okio.IOException
import org.kohsuke.github.GHRepository
import java.io.File
import java.net.URL
import java.nio.file.Paths
import java.util.*
import kotlin.math.abs
import org.kohsuke.github.GHUser as GHUserExternal

fun getGHRepositoryGenerator(
    ghUserDaos: List<GHUserDao>,
    languages: List<GHLanguageDao>,
    updatedAt: Instant,
    tags: List<TagDao>,
    mainCategory: MainCategory,
): Arb<GHRepositoryDao> {
    val faker = Faker()
    val fakerLorem = LoremFaker()
    return arbitrary {
        GHRepositoryDao(
            id = abs(faker.random.nextLong()),
            name = faker.name.name(),
            fullName = faker.name.name(),
            description = fakerLorem.lorem.words(),
            url = "https://${faker.internet.domain()}",
            license = faker.name.nameWithMiddle(),
            licenseUrl = "https://${faker.internet.domain()}",
            user = ghUserDaos.random(),
            languages = languages,
            updatedAt = updatedAt,
            tags = tags,
            mainCategory = mainCategory,
            readmeUrl = "https://${faker.internet.domain()}"
        )
    }
}

@Suppress("ThrowsCount")
fun getGHRepositoryExternalGenerator(
    ghUsers: List<GHUserExternal>,
    mDateMock: Date,
    ownerModifier: GHFieldModifier? = null,
    languagesModifier: GHFieldModifier? = null,
    licenseModifier: GHFieldModifier? = null,
): Arb<GHRepository> {
    val faker = Faker()
    val fakerLorem = LoremFaker()
    return arbitrary { rs ->
        mockk<GHRepository>().also {
            every { it.id } returns abs(faker.random.nextLong())
            every { it.owner }.also {
                when (ownerModifier) {
                    GHFieldModifier.AS_NULL -> it returns null

                    GHFieldModifier.IOEXCEPTION -> it answers {
                        throw IOException()
                    }

                    GHFieldModifier.OTHER_EXCEPTION -> it answers {
                        throw TestCustomException()
                    }

                    null -> it returns ghUsers.random()
                }
            }
            every { it.name } returns faker.name.name()
            every { it.fullName } returns faker.name.name()
            every { it.description } returns fakerLorem.lorem.words()
            every { it.htmlUrl } returns URL("https://${faker.internet.domain()}")
            every { it.license }.let {
                when (licenseModifier) {
                    GHFieldModifier.AS_NULL -> it returns null
                    GHFieldModifier.IOEXCEPTION -> it answers {
                        throw IOException()
                    }

                    GHFieldModifier.OTHER_EXCEPTION -> it answers {
                        throw TestCustomException()
                    }

                    null -> it returns mockk {
                        every { name } returns faker.name.nameWithMiddle()
                        every { htmlUrl } returns Arb.of(URL("https://${faker.internet.domain()}")).orNull().next()
                    }
                }
            }
            every { it.updatedAt } returns mDateMock
            every { it.listLanguages() }.let {
                val languagesGen = generateLanguages(faker.random.nextInt(0..10))

                when (languagesModifier) {
                    GHFieldModifier.AS_NULL -> it returns null
                    GHFieldModifier.IOEXCEPTION -> it answers {
                        throw IOException()
                    }

                    GHFieldModifier.OTHER_EXCEPTION -> it answers {
                        throw TestCustomException()
                    }

                    null -> it returns languagesGen.associate { ghLanguageDao ->
                        ghLanguageDao.name to ghLanguageDao.lines
                    }
                }
            }

            every { it.readme.downloadUrl } returns "https://${faker.internet.domain()}"
        }
    }
}

@OptIn(DelicateKotest::class)
fun generateLanguages(
    count: Int,
    forcedLanguage: String? = null,
    languageModifier: GHLanguageLinesModifier? = null,
): List<GHLanguageDao> {
    val faker = Faker()
    val languages = getGHLanguagesGenerator().distinct().let {
        MutableList(count) { _ ->
            it.next()
        }
    }.apply {
        if (forcedLanguage != null) {
            remove(forcedLanguage)
            add(forcedLanguage)
        }
    }

    val linesList = (0..<count).map { _ ->
        abs(
            faker.random
                .nextLong(
                    min = 11,
                    max = Long.MAX_VALUE
                )
        )
    }.toMutableList().also {
        it.add(
            when (languageModifier) {
                GHLanguageLinesModifier.MAX -> it.max() + 10
                GHLanguageLinesModifier.MIN -> it.min() - 10
                GHLanguageLinesModifier.INSIDE -> faker.random
                    .nextLong(
                        min = it.min() + 1,
                        max = it.max() - 1
                    )

                null -> return@also
            }
        )
    }

    val totalLines = linesList.sum()

    return languages.zip(linesList) { name, lines ->
        val weight = (lines.toFloat() / totalLines) * 100
        GHLanguageDao(name, lines, weight)
    }
}

fun getGithubColorsFile(): String {
    return (Paths.get("").toAbsolutePath().toString() + GITHUB_TEST_RESOURCE_PATH).let {
        File(it, "githubColors.json").readText()
    }
}

fun getGHLanguagesColor(language: String): String {
    return getGithubColorsFile().let {
        Json.parseToJsonElement(it).jsonObject[language]
            ?.jsonObject
            ?.get("color")
            ?.jsonPrimitive
            ?.contentOrNull ?: GithubColorControllerImpl.FALLBACK_COLOR
    }
}

fun getGHLanguagesGenerator(
    languageNameModifier: GHLanguageNameModifier = GHLanguageNameModifier.WITHOUT_KOTLIN
): Arb<String> {
    val languages = (Paths.get("").toAbsolutePath().toString() + GITHUB_TEST_RESOURCE_PATH).let {
        Json.parseToJsonElement(File(it, "languages.json").readText()).jsonArray.toList()
    }.map {
        it.jsonPrimitive.content
    }.toMutableList()
        .also {
            when (languageNameModifier) {
                GHLanguageNameModifier.WITHOUT_KOTLIN -> {
                    it.remove("Kotlin")
                }

                else -> Unit
            }
        }


    return arbitrary {
        languages[it.random.nextInt(languages.lastIndex)]
    }
}

fun getGHUserGenerator(): Arb<GHUserDao> {
    val faker = Faker()
    return arbitrary { rs ->
        GHUserDao(
            id = abs(faker.random.nextLong()),
            name = faker.name.nameWithMiddle(),
            url = "https://${faker.internet.domain()}",
            avatarUrl = "https://${faker.internet.domain()}"
        )
    }
}

fun getGHUserExternalGenerator(): Arb<GHUserExternal> {
    val faker = Faker()
    return arbitrary { rs ->
        mockk<GHUserExternal>().also {
            every { it.id } returns abs(rs.random.nextLong())
            every { it.login } returns faker.name.name()
            every { it.url } returns URL("https://${faker.internet.domain()}")
            every { it.avatarUrl } returns "https://${faker.internet.domain()}"
        }
    }
}

fun getTagsEntityGenerator(): Arb<TagNewDao> {
    val faker = Faker()
    val loremFaker = LoremFaker()
    return arbitrary {
        TagNewDao(
            name = faker.name.name().let {
                println("Tag name $it length: ${it.length}")
                if (it.length > 20) {
                    it.substring(0..19)
                } else {
                    it
                }
            },
            description = loremFaker.lorem.words(),
            color = generateRandomColor(it.random)
        )
    }
}

fun generateRandomColor(random: kotlin.random.Random): String {
    val red = random.nextInt(0, 256)
    val green = random.nextInt(0, 256)
    val blue = random.nextInt(0, 256)
    return "#%02X%02X%02X".format(red, green, blue)
}
