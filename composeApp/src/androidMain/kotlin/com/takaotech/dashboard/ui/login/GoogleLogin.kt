package com.takaotech.dashboard.ui.login

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.onFailure
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.takaotech.dashboard.AppBuildKonfig
import java.security.MessageDigest
import java.util.*

class GoogleLoginImpl(
    private val context: Context,
) : GoogleLogin {

    override suspend fun startLogin(): Result<Pair<Nonce, GoogleToken>, Exception> {
        return Result.of<Pair<Nonce, GoogleToken>, Exception> {
            val credentialManager = CredentialManager.create(context)

            // Generate a nonce and hash it with sha-256
            // Providing a nonce is optional but recommended
            val rawNonce = UUID.randomUUID()
                .toString() // Generate a random String. UUID should be sufficient, but can also be any other random string.
            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(AppBuildKonfig.googleWebAuth)
                .setAutoSelectEnabled(false)
                .setNonce(hashedNonce)
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )

            handleSignIn(result, hashedNonce)
        }.onFailure {
            //TODO Log with correct logger
            Log.e("Err", "bruh", it)
        }
    }

    private fun handleSignIn(result: GetCredentialResponse, hashedNonce: String): Pair<String, String> {
        return when (val credential = result.credential) {
            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    hashedNonce to GoogleIdTokenCredential
                        .createFrom(credential.data)
                        .idToken
                } else {
                    //TODO Throw custom credential

                    // Catch any unrecognized custom credential type here.
                    throw Exception("Unsupported login type")
                }
            }

            else -> {
                //TODO Throw custom credential

                // Catch any unrecognized credential type here.
                throw Exception("Unsupported login type")
            }
        }
    }
}