/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.takaotech.dashboard.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.GzipSource
import java.io.EOFException
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8

/**
 * An OkHttp interceptor which logs request and response information. Can be applied as an
 * [application interceptor][OkHttpClient.interceptors] or as a [OkHttpClient.networkInterceptors].
 *
 * The format of the logs created by this class should not be considered stable and may
 * change slightly between releases. If you need a stable logging format, use your own interceptor.
 */
class OkHttpRequestResponseFileLogInterceptor constructor(
    private val logFilePath: String = "http_logs.json"
) : Interceptor {
    private val coroutineScope = CoroutineScope(Dispatchers.Default.limitedParallelism(1))

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    private val file = File(logFilePath)

    init {
        // Initialize the file with a valid JSON array if it doesn't exist or is empty
        if (!file.exists() || file.length() == 0L) {
            FileWriter(file).use { writer ->
                writer.write("[]")
            }
        } else if (file.length() > 0L) {
            // Validate that the file contains a valid JSON array without loading it entirely
            try {
                RandomAccessFile(file, "r").use { randomAccessFile ->
                    // Check if file starts with '['
                    randomAccessFile.seek(0)
                    val firstChar = randomAccessFile.read()

                    // Check if file ends with ']'
                    randomAccessFile.seek(file.length() - 1)
                    val lastChar = randomAccessFile.read()

                    if (firstChar != '['.code || lastChar != ']'.code) {
                        // If not a valid JSON array, reset the file
                        FileWriter(file).use { writer ->
                            writer.write("[]")
                        }
                    }
                }
            } catch (e: Exception) {
                // If there's an error reading the file, reset it
                FileWriter(file).use { writer ->
                    writer.write("[]")
                }
            }
        }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBody = request.body

        coroutineScope.launch {
            appendLogToFile(
                Log(
                    timestamp = Clock.System.now().toString(),
                    type = Log.Type.REQUEST,
                    method = request.method,
                    url = request.url.toString(),
                    body = requestBody?.let {
                        if (bodyHasUnknownEncoding(request.headers)) {
                            return@let null
                        }
                        if (requestBody.isDuplex()) {
                            return@let null
                        }
                        if (requestBody.isOneShot()) {
                            return@let null
                        }

                        val buffer = Buffer()
                        requestBody.writeTo(buffer)

                        val contentType = requestBody.contentType()
                        val charset: Charset = contentType?.charset(UTF_8) ?: UTF_8

                        if (buffer.isProbablyUtf8()) {
                            buffer.readString(charset)
                        } else {
                            null
                        }
                    }
                )
            )
        }

        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            throw e
        }

        val responseBody = response.body!!
        val bodyString = if (!response.promisesBody()) {
            null
        } else if (bodyHasUnknownEncoding(response.headers)) {
            null
        } else {
            val headers = response.headers
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            var buffer = source.buffer

            if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                GzipSource(buffer.clone()).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                }
            }

            val contentType = responseBody.contentType()
            val charset: Charset = contentType?.charset(UTF_8) ?: UTF_8

            if (!buffer.isProbablyUtf8()) {
                null
            } else {
                buffer.clone().readString(charset)
            }
        }

        coroutineScope.launch {
            appendLogToFile(
                Log(
                    timestamp = Clock.System.now().toString(),
                    type = Log.Type.RESPONSE,
                    method = response.request.method,
                    url = response.request.url.toString(),
                    body = bodyString
                )
            )
        }



        return response
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }

    private suspend fun appendLogToFile(log: Log) {
        try {
            // Check if file exists and has content
            if (!file.exists() || file.length() == 0L) {
                // If file doesn't exist or is empty, create it with a new array containing just this log
                FileWriter(file).use { writer ->
                    writer.write(json.encodeToString(listOf(log)))
                }
                return
            }

            // Use RandomAccessFile to read just the last few bytes to determine file state
            RandomAccessFile(file, "rw").use { randomAccessFile ->
                val fileLength = randomAccessFile.length()

                // If file is very small, it might just contain "[]"
                if (fileLength <= 2) {
                    // Rewrite the file with just this log
                    randomAccessFile.setLength(0) // Truncate the file
                    FileWriter(file).use { writer ->
                        writer.write(json.encodeToString(listOf(log)))
                    }
                    return
                }

                // Read the last few bytes to check if we need a comma separator
                val bufferSize = minOf(20, fileLength.toInt()) // Read at most 20 bytes
                val buffer = ByteArray(bufferSize)
                randomAccessFile.seek(fileLength - bufferSize)
                randomAccessFile.readFully(buffer)
                val lastBytes = String(buffer)

                // Check if the file ends with "]" (needs comma) or "}," (doesn't need comma)
                val needsComma = !lastBytes.contains("},") && lastBytes.contains("]")

                // Position file pointer right before the closing bracket
                var position = fileLength - 1
                randomAccessFile.seek(position)
                while (position > 0 && randomAccessFile.read() != '['.code) {
                    position--
                    if (position > 0) {
                        randomAccessFile.seek(position)
                    }
                }

                // If we found the opening bracket and nothing else, it's an empty array
                if (position == 0L || position == fileLength - 2L) {
                    // Rewrite the file with just this log
                    randomAccessFile.setLength(0) // Truncate the file
                    FileWriter(file).use { writer ->
                        writer.write(json.encodeToString(listOf(log)))
                    }
                    return
                }

                // Find the position of the closing bracket
                randomAccessFile.seek(fileLength - 1)
                if (randomAccessFile.read() == ']'.code) {
                    // Truncate the file to remove the closing bracket
                    randomAccessFile.setLength(fileLength - 1)

                    // Append the new log with appropriate separator
                    randomAccessFile.seek(randomAccessFile.length())
                    val separator = if (needsComma) ",\n" else "\n"
                    val logJson = separator + json.encodeToString(log) + "\n]"
                    randomAccessFile.write(logJson.toByteArray())
                } else {
                    // If the file doesn't end with ']', it might be corrupted
                    // Reset it with just this log
                    randomAccessFile.setLength(0) // Truncate the file
                    FileWriter(file).use { writer ->
                        writer.write(json.encodeToString(listOf(log)))
                    }
                }
            }
        } catch (e: Exception) {
            // If there's an error, reset the file with just this log
            FileWriter(file).use { writer ->
                writer.write(json.encodeToString(listOf(log)))
            }
        }
    }
}

@Serializable
data class Log(
    val timestamp: String,
    val type: Type,
    val url: String,
    val method: String,
    val body: String?
) {
    enum class Type {
        REQUEST,
        RESPONSE
    }
}

internal fun Buffer.isProbablyUtf8(): Boolean {
    try {
        val prefix = Buffer()
        val byteCount = size.coerceAtMost(64)
        copyTo(prefix, 0, byteCount)
        for (i in 0 until 16) {
            if (prefix.exhausted()) {
                break
            }
            val codePoint = prefix.readUtf8CodePoint()
            if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                return false
            }
        }
        return true
    } catch (_: EOFException) {
        return false // Truncated UTF-8 sequence.
    }
}
