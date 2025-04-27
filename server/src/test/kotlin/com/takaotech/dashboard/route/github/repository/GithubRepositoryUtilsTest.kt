package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.model.github.exception.GHExternalConversionException
import com.takaotech.dashboard.route.github.repository.utils.convertToGHRepositoryWithDefaults
import com.takaotech.dashboard.utils.GHFieldModifier
import com.takaotech.dashboard.utils.TestCustomException
import com.takaotech.dashboard.utils.getGHRepositoryExternalGenerator
import com.takaotech.dashboard.utils.getGHUserExternalGenerator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.arbitrary.next
import kotlinx.datetime.toKotlinInstant
import java.util.*
import org.kohsuke.github.GHUser as GHUserExternal

class GithubRepositoryUtilsTest : BehaviorSpec({
    val domainRegex = Regex("^(https?://)([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(:\\d+)?(/.*)?$")
    val mDateMock = Date()

    val ghUsers: List<GHUserExternal> = getGHUserExternalGenerator().let {
        List(10) { _ ->
            it.next()
        }
    }

    Given("convertToGHRepositoryWithDefaults function") {
        When("converting a repository with all fields populated") {
            val repoTest = getGHRepositoryExternalGenerator(
                ghUsers = ghUsers,
                mDateMock = mDateMock
            ).next()

            val repoResult = repoTest.convertToGHRepositoryWithDefaults()

            Then("should correctly map all repository properties") {
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
        }

        When("converting a repository with null license") {
            val repoTest = getGHRepositoryExternalGenerator(
                ghUsers = ghUsers,
                mDateMock = mDateMock,
                licenseModifier = GHFieldModifier.AS_NULL
            ).next()

            val repoResult = repoTest.convertToGHRepositoryWithDefaults()

            Then("should handle null license fields properly") {
                repoResult.license shouldBe null
                repoResult.licenseUrl shouldBe null
            }
        }

        When("license access throws IOException") {
            val repoTest = getGHRepositoryExternalGenerator(
                ghUsers = ghUsers,
                mDateMock = mDateMock,
                licenseModifier = GHFieldModifier.IOEXCEPTION
            ).next()

            Then("should throw GHExternalConversionException") {
                shouldThrow<GHExternalConversionException> {
                    repoTest.convertToGHRepositoryWithDefaults()
                }
            }
        }

        When("license access throws other exception") {
            val repoTest = getGHRepositoryExternalGenerator(
                ghUsers = ghUsers,
                mDateMock = mDateMock,
                licenseModifier = GHFieldModifier.OTHER_EXCEPTION
            ).next()

            Then("should propagate the original exception") {
                shouldThrow<TestCustomException> {
                    repoTest.convertToGHRepositoryWithDefaults()
                }
            }
        }

        When("owner is null") {
            val repoTest = getGHRepositoryExternalGenerator(
                ghUsers = ghUsers,
                mDateMock = mDateMock,
                ownerModifier = GHFieldModifier.AS_NULL
            ).next()

            Then("should throw GHExternalConversionException for NPE") {
                shouldThrow<GHExternalConversionException> {
                    repoTest.convertToGHRepositoryWithDefaults()
                }
            }
        }

        When("owner access throws IOException") {
            val repoTest = getGHRepositoryExternalGenerator(
                ghUsers = ghUsers,
                mDateMock = mDateMock,
                ownerModifier = GHFieldModifier.IOEXCEPTION
            ).next()

            Then("should throw GHExternalConversionException") {
                shouldThrow<GHExternalConversionException> {
                    repoTest.convertToGHRepositoryWithDefaults()
                }
            }
        }

        When("owner access throws other exception") {
            val repoTest = getGHRepositoryExternalGenerator(
                ghUsers = ghUsers,
                mDateMock = mDateMock,
                ownerModifier = GHFieldModifier.OTHER_EXCEPTION
            ).next()

            Then("should propagate the original exception") {
                shouldThrow<TestCustomException> {
                    repoTest.convertToGHRepositoryWithDefaults()
                }
            }
        }

        When("listLanguages() returns null") {
            val repoTest = getGHRepositoryExternalGenerator(
                ghUsers = ghUsers,
                mDateMock = mDateMock,
                languagesModifier = GHFieldModifier.AS_NULL
            ).next()

            Then("should throw GHExternalConversionException for NPE") {
                shouldThrow<GHExternalConversionException> {
                    repoTest.convertToGHRepositoryWithDefaults()
                }
            }
        }

        When("listLanguages() throws IOException") {
            val repoTest = getGHRepositoryExternalGenerator(
                ghUsers = ghUsers,
                mDateMock = mDateMock,
                languagesModifier = GHFieldModifier.IOEXCEPTION
            ).next()

            Then("should throw GHExternalConversionException") {
                shouldThrow<GHExternalConversionException> {
                    repoTest.convertToGHRepositoryWithDefaults()
                }
            }
        }

        When("listLanguages() throws other exception") {
            val repoTest = getGHRepositoryExternalGenerator(
                ghUsers = ghUsers,
                mDateMock = mDateMock,
                languagesModifier = GHFieldModifier.OTHER_EXCEPTION
            ).next()

            Then("should propagate the original exception") {
                shouldThrow<TestCustomException> {
                    repoTest.convertToGHRepositoryWithDefaults()
                }
            }
        }
    }
})