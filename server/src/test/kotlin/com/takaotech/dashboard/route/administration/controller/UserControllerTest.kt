package com.takaotech.dashboard.route.administration.controller

import com.auth0.jwt.interfaces.Payload
import com.takaotech.dashboard.model.exception.SignUpException
import com.takaotech.dashboard.model.role.TakaoRole
import com.takaotech.dashboard.route.administration.data.role.RoleEntity
import com.takaotech.dashboard.route.administration.data.user.UserEntity
import com.takaotech.dashboard.route.administration.repository.UserRepository
import com.takaotech.dashboard.utils.sha256
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.jetbrains.exposed.dao.id.EntityID

class UserControllerTest : BehaviorSpec({

    fun mockUserEntity(
        id: String,
        email: String,
        displayName: String,
        profileImage: String
    ): UserEntity {
        val entityId = mockk<EntityID<String>>()
        every { entityId.value } returns id

        val userEntity = mockk<UserEntity>()
        every { userEntity.id } returns entityId
        every { userEntity.email } returns email
        every { userEntity.displayName } returns displayName
        every { userEntity.profileImage } returns profileImage

        return userEntity
    }

    fun mockRoleEntity(role: TakaoRole): RoleEntity {
        val entityId = mockk<EntityID<TakaoRole>>()
        every { entityId.value } returns role

        val roleEntity = mockk<RoleEntity>()
        every { roleEntity.id } returns entityId

        return roleEntity
    }

    val userRepository = mockk<UserRepository>()
    val userController = UserController(userRepository)

    Given("getUserByGoogle") {
        When("user exists") {
            val email = "test@example.com"
            val expectedUser = mockUserEntity(
                id = "1",
                email = email,
                displayName = "Test User",
                profileImage = "pic.jpg"
            )

            coEvery { userRepository.getUser(email.sha256()) } returns expectedUser

            Then("should return user") {
                userController.getUserByGoogle(email) shouldBe expectedUser
            }
        }

        When("user does not exist") {
            val email = "nonexistent@example.com"

            coEvery { userRepository.getUser(email.sha256()) } returns null

            Then("should return null") {
                userController.getUserByGoogle(email) shouldBe null
            }
        }
    }

    Given("getUserById") {
        When("user exists") {
            val id = "1"
            val expectedUser = mockUserEntity(
                id = id,
                email = "test@example.com",
                displayName = "Test User",
                profileImage = "pic.jpg"
            )

            coEvery { userRepository.getUser(id) } returns expectedUser

            Then("should return user") {
                userController.getUserById(id) shouldBe expectedUser
            }
        }

        When("user does not exist") {
            val id = "nonexistent"

            coEvery { userRepository.getUser(id) } returns null

            Then("should return null") {
                userController.getUserById(id) shouldBe null
            }
        }
    }

    Given("getUserRolesById") {
        When("user has roles") {
            val id = "1"
            val roles = setOf(TakaoRole.ADMINISTRATOR, TakaoRole.BASE_USER)
            val roleEntities = roles.map { mockRoleEntity(it) }

            coEvery { userRepository.getUserRolesById(id) } returns roleEntities

            Then("should return roles") {
                userController.getUserRolesById(id) shouldBe roles
            }
        }

        When("user has no roles") {
            val id = "1"

            coEvery { userRepository.getUserRolesById(id) } returns null

            Then("should return null") {
                userController.getUserRolesById(id) shouldBe null
            }
        }
    }

    Given("signUpByGoogle with Payload") {
        When("email is verified and user doesn't exist") {
            val payload = mockk<Payload>()
            val email = "new@example.com"
            val name = "New User"
            val picture = "new_pic.jpg"

            coEvery { payload.getClaim("email_verified").asBoolean() } returns true
            coEvery { payload.getClaim("email").asString() } returns email
            coEvery { payload.getClaim("name").asString() } returns name
            coEvery { payload.getClaim("picture").asString() } returns picture
            coEvery { userRepository.getUser(email.sha256()) } returns null
            coEvery { userRepository.createUser(email, name, picture) } returns Unit

            Then("should create user") {
                userController.signUpByGoogle(payload)
            }
        }

        When("email is not verified") {
            val payload = mockk<Payload>()

            coEvery { payload.getClaim("email_verified").asBoolean() } returns false

            Then("should throw EmailNotVerified exception") {
                val exception = shouldThrow<SignUpException.EmailNotVerified> {
                    userController.signUpByGoogle(payload)
                }
                exception.message shouldBe "Email not verified"
            }
        }

        When("email claim is null") {
            val payload = mockk<Payload>()

            coEvery { payload.getClaim("email_verified").asBoolean() } returns true
            coEvery { payload.getClaim("email").asString() } returns null

            Then("should throw EmailNotFound exception") {
                val exception = shouldThrow<SignUpException.EmailNotFound> {
                    userController.signUpByGoogle(payload)
                }
                exception.message shouldBe "Email not found"
            }
        }

        When("user already exists") {
            val payload = mockk<Payload>()
            val email = "existing@example.com"
            val existingUser = mockUserEntity(
                id = "1",
                email = email,
                displayName = "Existing User",
                profileImage = "pic.jpg"
            )

            coEvery { payload.getClaim("email_verified").asBoolean() } returns true
            coEvery { payload.getClaim("email").asString() } returns email
            coEvery { userRepository.getUser(email.sha256()) } returns existingUser

            Then("should throw UserAlreadyExists exception") {
                val exception = shouldThrow<SignUpException.UserAlreadyExists> {
                    userController.signUpByGoogle(payload)
                }
                exception.message shouldBe "User already exists"
            }
        }
    }

    Given("signUpByGoogle with JsonObject") {
        When("email is verified and user doesn't exist") {
            val email = "new@example.com"
            val name = "New User"
            val picture = "new_pic.jpg"
            val json = JsonObject(
                mapOf(
                    "verified_email" to JsonPrimitive(true),
                    "email" to JsonPrimitive(email),
                    "name" to JsonPrimitive(name),
                    "picture" to JsonPrimitive(picture)
                )
            )

            coEvery { userRepository.getUser(email.sha256()) } returns null
            coEvery { userRepository.createUser(email, name, picture) } returns Unit

            Then("should create user") {
                userController.signUpByGoogle(json)
            }
        }

        When("email is not verified") {
            val json = JsonObject(
                mapOf(
                    "verified_email" to JsonPrimitive(false)
                )
            )

            Then("should throw EmailNotVerified exception") {
                val exception = shouldThrow<SignUpException.EmailNotVerified> {
                    userController.signUpByGoogle(json)
                }
                exception.message shouldBe "Email not verified"
            }
        }

        When("email key is missing") {
            val json = JsonObject(
                mapOf(
                    "verified_email" to JsonPrimitive(true)
                )
            )

            Then("should throw InvalidUserData exception") {
                val exception = shouldThrow<SignUpException.InvalidUserData> {
                    userController.signUpByGoogle(json)
                }
                exception.message shouldBe "Invalid user data: malformed data"
            }
        }

        When("user already exists") {
            val email = "existing@example.com"
            val json = JsonObject(
                mapOf(
                    "verified_email" to JsonPrimitive(true),
                    "email" to JsonPrimitive(email)
                )
            )

            val existingUser = mockUserEntity(
                id = "1",
                email = email,
                displayName = "Existing User",
                profileImage = "pic.jpg"
            )

            coEvery { userRepository.getUser(email.sha256()) } returns existingUser

            Then("should throw UserAlreadyExists exception") {
                val exception = shouldThrow<SignUpException.UserAlreadyExists> {
                    userController.signUpByGoogle(json)
                }
                exception.message shouldBe "User already exists"
            }
        }
    }
})
