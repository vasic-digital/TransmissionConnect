package com.shareconnect.transmissionconnect.data.api

import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.shareconnect.transmissionconnect.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Transmission RPC API client
 * Implements Transmission RPC protocol
 */
class TransmissionApiClient(
    private val serverUrl: String,
    private val username: String? = null,
    private val password: String? = null
) {
    private val tag = "TransmissionApiClient"
    private val gson = Gson()

    private val rpcUrl = "${serverUrl.removeSuffix("/")}/transmission/rpc"
    private var sessionId: String? = null

    private val authHeader: String?
        get() = if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
            val credentials = "$username:$password"
            val encodedCredentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            "Basic $encodedCredentials"
        } else null

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    /**
     * Execute Transmission RPC method
     */
    private suspend fun <T> executeRpc(
        method: String,
        arguments: Map<String, Any> = emptyMap(),
        resultClass: Class<T>
    ): Result<T> = withContext(Dispatchers.IO) {
        try {
            val jsonRequest = JSONObject().apply {
                put("method", method)
                if (arguments.isNotEmpty()) {
                    put("arguments", JSONObject(arguments))
                }
            }

            val result = executeRpcWithSessionHandling(jsonRequest.toString())

            when {
                result.isSuccess -> {
                    try {
                        val responseBody = result.getOrNull()!!
                        val response = gson.fromJson(responseBody, TransmissionResponse::class.java)

                        if (response.result == "success") {
                            val argumentsJson = gson.toJson(response.arguments)
                            val resultObject = gson.fromJson(argumentsJson, resultClass)
                            Result.success(resultObject)
                        } else {
                            Result.failure(Exception("RPC failed: ${response.result}"))
                        }
                    } catch (e: Exception) {
                        Log.e(tag, "Error parsing response", e)
                        Result.failure(e)
                    }
                }
                else -> Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error executing RPC method: $method", e)
            Result.failure(e)
        }
    }

    /**
     * Execute RPC with automatic session ID handling (409 retry)
     */
    private suspend fun executeRpcWithSessionHandling(
        jsonBody: String,
        retryCount: Int = 0
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val requestBody = jsonBody.toRequestBody("application/json".toMediaType())
            val requestBuilder = Request.Builder()
                .url(rpcUrl)
                .post(requestBody)

            // Add authentication header if credentials provided
            authHeader?.let { requestBuilder.addHeader("Authorization", it) }

            // Add session ID if we have one
            sessionId?.let { requestBuilder.addHeader("X-Transmission-Session-Id", it) }

            val request = requestBuilder.build()
            val response = okHttpClient.newCall(request).execute()

            when (response.code) {
                409 -> {
                    // Session ID required or expired
                    val newSessionId = response.header("X-Transmission-Session-Id")
                    if (newSessionId != null && retryCount < 3) {
                        sessionId = newSessionId
                        Log.d(tag, "Got new session ID, retrying request")
                        response.close()
                        return@withContext executeRpcWithSessionHandling(jsonBody, retryCount + 1)
                    } else {
                        response.close()
                        return@withContext Result.failure(Exception("Failed to get session ID"))
                    }
                }
                200 -> {
                    val responseBody = response.body?.string()
                    response.close()
                    if (responseBody != null) {
                        Result.success(responseBody)
                    } else {
                        Result.failure(Exception("Empty response body"))
                    }
                }
                else -> {
                    val errorBody = response.body?.string() ?: "Unknown error"
                    response.close()
                    Result.failure(Exception("HTTP ${response.code}: $errorBody"))
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error in RPC request", e)
            Result.failure(e)
        }
    }

    /**
     * Get session information
     */
    suspend fun getSession(): Result<TransmissionSession> {
        return executeRpc("session-get", emptyMap(), TransmissionSession::class.java)
    }

    /**
     * Set session parameters
     */
    suspend fun setSession(parameters: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val jsonRequest = JSONObject().apply {
                put("method", "session-set")
                put("arguments", JSONObject(parameters))
            }

            val result = executeRpcWithSessionHandling(jsonRequest.toString())
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error setting session", e)
            Result.failure(e)
        }
    }

    /**
     * Get session statistics
     */
    suspend fun getSessionStats(): Result<TransmissionStats> {
        return executeRpc("session-stats", emptyMap(), TransmissionStats::class.java)
    }

    /**
     * Get list of all torrents
     */
    suspend fun getTorrents(
        ids: List<Int>? = null,
        fields: List<String>? = null
    ): Result<List<TransmissionTorrent>> = withContext(Dispatchers.IO) {
        try {
            val arguments = mutableMapOf<String, Any>()

            if (fields != null) {
                arguments["fields"] = fields
            } else {
                // Default fields to retrieve
                arguments["fields"] = listOf(
                    "id", "name", "hashString", "status", "percentDone",
                    "rateDownload", "rateUpload", "downloadedEver", "uploadedEver",
                    "totalSize", "eta", "peersConnected", "uploadRatio",
                    "addedDate", "downloadDir", "error", "errorString",
                    "queuePosition", "isFinished"
                )
            }

            if (ids != null) {
                arguments["ids"] = ids
            }

            val jsonRequest = JSONObject().apply {
                put("method", "torrent-get")
                put("arguments", JSONObject(arguments as Map<*, *>))
            }

            val result = executeRpcWithSessionHandling(jsonRequest.toString())

            when {
                result.isSuccess -> {
                    try {
                        val responseBody = result.getOrNull()!!
                        val response = gson.fromJson(responseBody, object : com.google.gson.reflect.TypeToken<TransmissionResponse<TransmissionTorrentList>>() {}.type) as TransmissionResponse<TransmissionTorrentList>

                        if (response.result == "success") {
                            Result.success(response.arguments?.torrents ?: emptyList())
                        } else {
                            Result.failure(Exception("Get torrents failed: ${response.result}"))
                        }
                    } catch (e: Exception) {
                        Log.e(tag, "Error parsing torrents response", e)
                        Result.failure(e)
                    }
                }
                else -> Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting torrents", e)
            Result.failure(e)
        }
    }

    /**
     * Add torrent by URL or magnet link
     */
    suspend fun addTorrent(
        filename: String,
        downloadDir: String? = null,
        paused: Boolean? = null,
        bandwidthPriority: Int? = null,
        filesWanted: List<Int>? = null,
        filesUnwanted: List<Int>? = null,
        priorityHigh: List<Int>? = null,
        priorityLow: List<Int>? = null,
        priorityNormal: List<Int>? = null
    ): Result<TransmissionTorrent> = withContext(Dispatchers.IO) {
        try {
            val arguments = mutableMapOf<String, Any>("filename" to filename)

            downloadDir?.let { arguments["download-dir"] = it }
            paused?.let { arguments["paused"] = it }
            bandwidthPriority?.let { arguments["bandwidthPriority"] = it }
            filesWanted?.let { arguments["files-wanted"] = it }
            filesUnwanted?.let { arguments["files-unwanted"] = it }
            priorityHigh?.let { arguments["priority-high"] = it }
            priorityLow?.let { arguments["priority-low"] = it }
            priorityNormal?.let { arguments["priority-normal"] = it }

            val jsonRequest = JSONObject().apply {
                put("method", "torrent-add")
                put("arguments", JSONObject(arguments as Map<*, *>))
            }

            val result = executeRpcWithSessionHandling(jsonRequest.toString())

            when {
                result.isSuccess -> {
                    try {
                        val responseBody = result.getOrNull()!!
                        val response = gson.fromJson(responseBody, object : com.google.gson.reflect.TypeToken<TransmissionResponse<TransmissionTorrentAddResult>>() {}.type) as TransmissionResponse<TransmissionTorrentAddResult>

                        if (response.result == "success") {
                            val torrent = response.arguments?.torrentAdded ?: response.arguments?.torrentDuplicate
                            if (torrent != null) {
                                Result.success(torrent)
                            } else {
                                Result.failure(Exception("No torrent in response"))
                            }
                        } else {
                            Result.failure(Exception("Add torrent failed: ${response.result}"))
                        }
                    } catch (e: Exception) {
                        Log.e(tag, "Error parsing add torrent response", e)
                        Result.failure(e)
                    }
                }
                else -> Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error adding torrent", e)
            Result.failure(e)
        }
    }

    /**
     * Start one or more torrents
     */
    suspend fun startTorrents(ids: List<Int>): Result<Unit> = withContext(Dispatchers.IO) {
        torrentAction("torrent-start", ids)
    }

    /**
     * Start torrents immediately (bypass queue)
     */
    suspend fun startTorrentsNow(ids: List<Int>): Result<Unit> = withContext(Dispatchers.IO) {
        torrentAction("torrent-start-now", ids)
    }

    /**
     * Stop one or more torrents
     */
    suspend fun stopTorrents(ids: List<Int>): Result<Unit> = withContext(Dispatchers.IO) {
        torrentAction("torrent-stop", ids)
    }

    /**
     * Verify one or more torrents
     */
    suspend fun verifyTorrents(ids: List<Int>): Result<Unit> = withContext(Dispatchers.IO) {
        torrentAction("torrent-verify", ids)
    }

    /**
     * Reannounce to trackers for one or more torrents
     */
    suspend fun reannounceTorrents(ids: List<Int>): Result<Unit> = withContext(Dispatchers.IO) {
        torrentAction("torrent-reannounce", ids)
    }

    /**
     * Remove one or more torrents
     */
    suspend fun removeTorrents(ids: List<Int>, deleteLocalData: Boolean = false): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val arguments = mapOf(
                "ids" to ids,
                "delete-local-data" to deleteLocalData
            )

            val jsonRequest = JSONObject().apply {
                put("method", "torrent-remove")
                put("arguments", JSONObject(arguments as Map<*, *>))
            }

            val result = executeRpcWithSessionHandling(jsonRequest.toString())
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error removing torrents", e)
            Result.failure(e)
        }
    }

    /**
     * Set torrent location
     */
    suspend fun setTorrentLocation(
        ids: List<Int>,
        location: String,
        move: Boolean = false
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val arguments = mapOf(
                "ids" to ids,
                "location" to location,
                "move" to move
            )

            val jsonRequest = JSONObject().apply {
                put("method", "torrent-set-location")
                put("arguments", JSONObject(arguments as Map<*, *>))
            }

            val result = executeRpcWithSessionHandling(jsonRequest.toString())
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error setting torrent location", e)
            Result.failure(e)
        }
    }

    /**
     * Rename torrent path
     */
    suspend fun renameTorrentPath(
        ids: List<Int>,
        path: String,
        name: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val arguments = mapOf(
                "ids" to ids,
                "path" to path,
                "name" to name
            )

            val jsonRequest = JSONObject().apply {
                put("method", "torrent-rename-path")
                put("arguments", JSONObject(arguments as Map<*, *>))
            }

            val result = executeRpcWithSessionHandling(jsonRequest.toString())
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error renaming torrent path", e)
            Result.failure(e)
        }
    }

    /**
     * Set torrent properties
     */
    suspend fun setTorrent(
        ids: List<Int>,
        properties: Map<String, Any>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val arguments = properties.toMutableMap()
            arguments["ids"] = ids

            val jsonRequest = JSONObject().apply {
                put("method", "torrent-set")
                put("arguments", JSONObject(arguments as Map<*, *>))
            }

            val result = executeRpcWithSessionHandling(jsonRequest.toString())
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error setting torrent properties", e)
            Result.failure(e)
        }
    }

    /**
     * Move torrents in queue
     */
    suspend fun queueMoveTop(ids: List<Int>): Result<Unit> = torrentAction("queue-move-top", ids)
    suspend fun queueMoveUp(ids: List<Int>): Result<Unit> = torrentAction("queue-move-up", ids)
    suspend fun queueMoveDown(ids: List<Int>): Result<Unit> = torrentAction("queue-move-down", ids)
    suspend fun queueMoveBottom(ids: List<Int>): Result<Unit> = torrentAction("queue-move-bottom", ids)

    /**
     * Test port connectivity
     */
    suspend fun portTest(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val jsonRequest = JSONObject().apply {
                put("method", "port-test")
            }

            val result = executeRpcWithSessionHandling(jsonRequest.toString())

            when {
                result.isSuccess -> {
                    try {
                        val responseBody = result.getOrNull()!!
                        val json = JSONObject(responseBody)
                        val portOpen = json.getJSONObject("arguments").getBoolean("port-is-open")
                        Result.success(portOpen)
                    } catch (e: Exception) {
                        Log.e(tag, "Error parsing port test response", e)
                        Result.failure(e)
                    }
                }
                else -> Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error testing port", e)
            Result.failure(e)
        }
    }

    /**
     * Update blocklist
     */
    suspend fun blocklistUpdate(): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val jsonRequest = JSONObject().apply {
                put("method", "blocklist-update")
            }

            val result = executeRpcWithSessionHandling(jsonRequest.toString())

            when {
                result.isSuccess -> {
                    try {
                        val responseBody = result.getOrNull()!!
                        val json = JSONObject(responseBody)
                        val blocklistSize = json.getJSONObject("arguments").getLong("blocklist-size")
                        Result.success(blocklistSize)
                    } catch (e: Exception) {
                        Log.e(tag, "Error parsing blocklist update response", e)
                        Result.failure(e)
                    }
                }
                else -> Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating blocklist", e)
            Result.failure(e)
        }
    }

    /**
     * Generic torrent action helper
     */
    private suspend fun torrentAction(method: String, ids: List<Int>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val arguments = mapOf("ids" to ids)

            val jsonRequest = JSONObject().apply {
                put("method", method)
                put("arguments", JSONObject(arguments as Map<*, *>))
            }

            val result = executeRpcWithSessionHandling(jsonRequest.toString())
            result.map { Unit }
        } catch (e: Exception) {
            Log.e(tag, "Error executing torrent action: $method", e)
            Result.failure(e)
        }
    }

    companion object {
        private const val TAG = "TransmissionApiClient"
    }
}
