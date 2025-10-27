/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.transmissionconnect.data.api

import com.shareconnect.transmissionconnect.data.model.TransmissionTorrent
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for TransmissionApiClient
 *
 * Tests cover:
 * - Session ID handling and 409 retry mechanism
 * - Torrent operations (add, start, stop, remove)
 * - Torrent information retrieval
 * - Queue management
 * - Session management
 * - Error handling
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class TransmissionApiClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: TransmissionApiClient
    private val testUsername = "transmission"
    private val testPassword = "password"
    private val testSessionId = "test-session-id-12345"

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val baseUrl = mockWebServer.url("/transmission/rpc").toString()
        apiClient = TransmissionApiClient(baseUrl, testUsername, testPassword)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test API client initialization`() {
        assertNotNull(apiClient)
    }

    @Test
    fun `test session ID handling with 409 response`() = runBlocking {
        // First request returns 409 with session ID
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(409)
            .setHeader("X-Transmission-Session-Id", testSessionId)
            .setBody(""))

        // Second request with session ID succeeds
        val responseJson = """
            {
                "result": "success",
                "arguments": {
                    "torrents": []
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.getTorrents()

        assertTrue(result.isSuccess)

        // Verify first request was made
        val firstRequest = mockWebServer.takeRequest()
        assertNull(firstRequest.getHeader("X-Transmission-Session-Id"))

        // Verify second request includes session ID
        val secondRequest = mockWebServer.takeRequest()
        assertEquals(testSessionId, secondRequest.getHeader("X-Transmission-Session-Id"))
    }

    @Test
    fun `test get all torrents`() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "arguments": {
                    "torrents": [
                        {
                            "id": 1,
                            "hashString": "abc123def456",
                            "name": "Test Torrent",
                            "status": 4,
                            "percentDone": 0.75,
                            "rateDownload": 102400,
                            "rateUpload": 51200,
                            "totalSize": 1073741824,
                            "downloadedEver": 805306368,
                            "uploadedEver": 1073741824,
                            "uploadRatio": 1.5,
                            "eta": 3600,
                            "peersConnected": 25,
                            "downloadDir": "/downloads"
                        }
                    ]
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.getTorrents()

        assertTrue(result.isSuccess)
        val torrents = result.getOrNull()!!
        assertEquals(1, torrents.size)
        assertEquals(1, torrents[0].id)
        assertEquals("abc123def456", torrents[0].hashString)
        assertEquals("Test Torrent", torrents[0].name)
    }

    @Test
    fun `test get specific torrents by ID`() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "arguments": {
                    "torrents": [
                        {
                            "id": 1,
                            "hashString": "abc123",
                            "name": "Torrent 1",
                            "status": 4,
                            "percentDone": 1.0
                        },
                        {
                            "id": 2,
                            "hashString": "def456",
                            "name": "Torrent 2",
                            "status": 0,
                            "percentDone": 0.5
                        }
                    ]
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.getTorrents(ids = listOf(1, 2))

        assertTrue(result.isSuccess)
        val torrents = result.getOrNull()!!
        assertEquals(2, torrents.size)
    }

    @Test
    fun `test add torrent by URL`() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "arguments": {
                    "torrent-added": {
                        "id": 5,
                        "hashString": "new-torrent-hash",
                        "name": "New Torrent"
                    }
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.addTorrent(
            filename = "magnet:?xt=urn:btih:test",
            downloadDir = "/downloads/torrents"
        )

        assertTrue(result.isSuccess)
        val torrent = result.getOrNull()!!
        assertEquals(5, torrent.id)
        assertEquals("new-torrent-hash", torrent.hashString)
    }

    @Test
    fun `test start torrents`() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "arguments": {}
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.startTorrents(listOf(1, 2, 3))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test start torrents now (force)`() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "arguments": {}
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.startTorrentsNow(listOf(1, 2))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test stop torrents`() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "arguments": {}
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.stopTorrents(listOf(1, 2, 3))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test remove torrents without data`() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "arguments": {}
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.removeTorrents(listOf(1), deleteLocalData = false)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test remove torrents with data`() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "arguments": {}
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.removeTorrents(listOf(1, 2), deleteLocalData = true)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test verify torrents`() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "arguments": {}
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.verifyTorrents(listOf(1, 2))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test reannounce torrents`() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "arguments": {}
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.reannounceTorrents(listOf(1))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test queue move top`() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "arguments": {}
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.queueMoveTop(listOf(5))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test queue move bottom`() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "arguments": {}
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.queueMoveBottom(listOf(1))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test queue move up`() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "arguments": {}
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.queueMoveUp(listOf(3))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test queue move down`() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "arguments": {}
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.queueMoveDown(listOf(2))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test get session`() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "arguments": {
                    "version": "3.00",
                    "download-dir": "/downloads",
                    "speed-limit-down-enabled": true,
                    "speed-limit-down": 1024,
                    "speed-limit-up-enabled": true,
                    "speed-limit-up": 512
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.getSession()

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test RPC error response`() = runBlocking {
        val responseJson = """
            {
                "result": "torrent not registered with this tracker",
                "arguments": {}
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        val result = apiClient.getTorrents()

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception!!.message!!.contains("torrent not registered"))
    }

    @Test
    fun `test network error handling`() = runBlocking {
        // Shutdown server to simulate network error
        mockWebServer.shutdown()

        val result = apiClient.getTorrents()

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `test HTTP error code handling`() = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(500)
            .setBody("Internal Server Error"))

        val result = apiClient.getTorrents()

        assertTrue(result.isFailure)
    }

    @Test
    fun `test session ID retry limit`() = runBlocking {
        // Return 409 three times (exceeds retry limit)
        for (i in 1..4) {
            mockWebServer.enqueue(MockResponse()
                .setResponseCode(409)
                .setHeader("X-Transmission-Session-Id", "session-$i")
                .setBody(""))
        }

        val result = apiClient.getTorrents()

        assertTrue(result.isFailure)
        // Should have made 4 attempts (initial + 3 retries)
        assertEquals(4, mockWebServer.requestCount)
    }

    @Test
    fun `test basic authentication header`() = runBlocking {
        val responseJson = """
            {
                "result": "success",
                "arguments": {
                    "torrents": []
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(responseJson))

        apiClient.getTorrents()

        val request = mockWebServer.takeRequest()
        val authHeader = request.getHeader("Authorization")
        assertNotNull(authHeader)
        assertTrue(authHeader!!.startsWith("Basic "))
    }
}
