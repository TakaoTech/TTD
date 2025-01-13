package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.model.github.exception.GHExternalConversionException
import com.takaotech.dashboard.route.github.repository.utils.convertToGHRepositoryWithDefaults
import com.takaotech.dashboard.utils.GHFieldModifier
import com.takaotech.dashboard.utils.TestCustomException
import com.takaotech.dashboard.utils.getGHRepositoryExternalGenerator
import com.takaotech.dashboard.utils.getGHUserExternalGenerator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.arbitrary.next
import kotlinx.datetime.toKotlinInstant
import java.util.*
import org.kohsuke.github.GHUser as GHUserExternal

class GithubRepositoryUtilsTest : FunSpec() {
    private val domainRegex = Regex("^(https?://)([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(:\\d+)?(/.*)?$")
    private val mDateMock = Date()

    private val ghUsers: List<GHUserExternal> = getGHUserExternalGenerator().let {
        List(10) { _ ->
            it.next()
        }
    }

    init {
        context("convertToGHRepositoryWithDefaults") {
            test("Happy flow") {
                val repoTest = getGHRepositoryExternalGenerator(
                    ghUsers = ghUsers,
                    mDateMock = mDateMock
                ).next()

                val repoResult = repoTest.convertToGHRepositoryWithDefaults()

                repoResult.id shouldBe repoTest.id
                repoResult.name shouldBe repoTest.name
                repoResult.fullName shouldBe repoTest.fullName
                repoResult.description shouldBe repoTest.description
                repoResult.license shouldBe repoTest.license?.name
                repoResult.licenseUrl.also { licenseUrl ->
                    licenseUrl shouldBe repoTest.license?.htmlUrl?.toString()
                    if (licenseUrl != null) {
                        licenseUrl shouldMatch domainRegex
                    }
                }
                repoResult.updatedAt shouldBe repoTest.updatedAt.toInstant().toKotlinInstant()
                repoResult.languages.forEach { language ->
                    val languageTest = repoTest.listLanguages()[language.name]
                    language.lines shouldBe languageTest
                }

                repoResult.user.also { user ->
                    user.id shouldBe repoTest.owner.id
                    user.name shouldBe repoTest.owner.login
                    user.url shouldBe repoTest.owner.url.toString()
                    user.avatarUrl shouldBe repoTest.owner.avatarUrl
                }
            }

            test("licence null") {
                val repoTest = getGHRepositoryExternalGenerator(
                    ghUsers = ghUsers,
                    mDateMock = mDateMock,
                    licenseModifier = GHFieldModifier.AS_NULL
                ).next()

                val repoResult = repoTest.convertToGHRepositoryWithDefaults()

                repoResult.license shouldBe null
                repoResult.licenseUrl shouldBe null
            }

            test("licence IOException throw GHExternalConversionException") {
                val repoTest = getGHRepositoryExternalGenerator(
                    ghUsers = ghUsers,
                    mDateMock = mDateMock,
                    licenseModifier = GHFieldModifier.IOEXCEPTION
                ).next()

                shouldThrow<GHExternalConversionException> { repoTest.convertToGHRepositoryWithDefaults() }
            }

            test("licence other Exception throw Exception") {
                val repoTest = getGHRepositoryExternalGenerator(
                    ghUsers = ghUsers,
                    mDateMock = mDateMock,
                    licenseModifier = GHFieldModifier.OTHER_EXCEPTION
                ).next()

                shouldThrow<TestCustomException> { repoTest.convertToGHRepositoryWithDefaults() }
            }

            test("owner null NPE throw GHExternalConversionException") {
                val repoTest = getGHRepositoryExternalGenerator(
                    ghUsers = ghUsers,
                    mDateMock = mDateMock,
                    ownerModifier = GHFieldModifier.AS_NULL
                ).next()

                shouldThrow<GHExternalConversionException> { repoTest.convertToGHRepositoryWithDefaults() }
            }

            test("owner IOException throw GHExternalConversionException") {
                val repoTest = getGHRepositoryExternalGenerator(
                    ghUsers = ghUsers,
                    mDateMock = mDateMock,
                    ownerModifier = GHFieldModifier.IOEXCEPTION
                ).next()

                shouldThrow<GHExternalConversionException> { repoTest.convertToGHRepositoryWithDefaults() }
            }

            test("owner throw other Exception") {
                val repoTest = getGHRepositoryExternalGenerator(
                    ghUsers = ghUsers,
                    mDateMock = mDateMock,
                    ownerModifier = GHFieldModifier.OTHER_EXCEPTION
                ).next()

                shouldThrow<TestCustomException> { repoTest.convertToGHRepositoryWithDefaults() }
            }

            test("listLanguages() null NPE throw GHExternalConversionException") {
                val repoTest = getGHRepositoryExternalGenerator(
                    ghUsers = ghUsers,
                    mDateMock = mDateMock,
                    languagesModifier = GHFieldModifier.AS_NULL
                ).next()

                shouldThrow<GHExternalConversionException> { repoTest.convertToGHRepositoryWithDefaults() }
            }

            test("listLanguages() IOException throw GHExternalConversionException") {
                val repoTest = getGHRepositoryExternalGenerator(
                    ghUsers = ghUsers,
                    mDateMock = mDateMock,
                    languagesModifier = GHFieldModifier.IOEXCEPTION
                ).next()

                shouldThrow<GHExternalConversionException> { repoTest.convertToGHRepositoryWithDefaults() }
            }

            test("listLanguages() throw other exception") {
                val repoTest = getGHRepositoryExternalGenerator(
                    ghUsers = ghUsers,
                    mDateMock = mDateMock,
                    languagesModifier = GHFieldModifier.OTHER_EXCEPTION
                ).next()

                shouldThrow<TestCustomException> { repoTest.convertToGHRepositoryWithDefaults() }
            }
        }
    }
}