package com.convx.music.ai

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okio.Buffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AiRequestsTest {
    private fun body(request: okhttp3.Request): JsonObject =
        Json.parseToJsonElement(Buffer().also { request.body!!.writeTo(it) }.readUtf8()).jsonObject

    @Test
    fun `groq gets an OpenAI-style request with a bearer key`() {
        val request = AiRequests.chat(AiDefaults.BASE_URL, " gsk_1 ", AiDefaults.MODEL, "sys", "hi", maxTokens = 4096, temperature = 0.3, stream = true)
        assertEquals("Bearer gsk_1", request.header("Authorization"))
        assertNull(request.header("x-api-key"))
        val json = body(request)
        assertEquals(AiDefaults.MODEL, json["model"]!!.jsonPrimitive.content)
        val messages = json["messages"] as JsonArray
        assertEquals("system", messages[0].jsonObject["role"]!!.jsonPrimitive.content)
        assertEquals("user", messages[1].jsonObject["role"]!!.jsonPrimitive.content)
        assertEquals("0.3", json["temperature"]!!.jsonPrimitive.content)
        assertEquals("true", json["stream"]!!.jsonPrimitive.content)
    }

    @Test
    fun `claude gets the Messages API shape`() {
        val request = AiRequests.chat("https://api.anthropic.com/v1/messages", "sk-ant", "claude-haiku-5-5", "sys", "hi", maxTokens = 300, temperature = 0.3)
        assertTrue(AiRequests.isAnthropic("https://api.anthropic.com/v1/messages"))
        assertEquals("sk-ant", request.header("x-api-key"))
        assertEquals("2023-06-01", request.header("anthropic-version"))
        assertNull(request.header("Authorization"))
        val json = body(request)
        assertEquals("sys", json["system"]!!.jsonPrimitive.content)
        assertEquals(1, (json["messages"] as JsonArray).size)
        // Thinking needs room: a small budget is raised, and sampling parameters are never sent.
        assertEquals("4096", json["max_tokens"]!!.jsonPrimitive.content)
        assertNull(json["temperature"])
        assertNull(json["fallbacks"])
    }

    @Test
    fun `claude opus asks for the server-side fallback`() {
        val request = AiRequests.chat("https://api.anthropic.com/v1/messages", "k", "claude-opus-5-5", "s", "u")
        assertEquals("server-side-fallback-2026-07-01", request.header("anthropic-beta"))
        assertEquals("default", body(request)["fallbacks"]!!.jsonPrimitive.content)
    }

    @Test
    fun `answers are read from both shapes`() {
        assertEquals("hi", AiRequests.text("""{"choices":[{"message":{"content":"hi"}}]}"""))
        assertEquals(
            "a b",
            AiRequests.text("""{"content":[{"type":"thinking","thinking":""},{"type":"text","text":"a "},{"type":"text","text":"b"}],"stop_reason":"end_turn"}"""),
        )
        assertNull(AiRequests.text("""{"content":[],"stop_reason":"refusal"}"""))
        assertNull(AiRequests.text("""{"choices":[{"message":{"content":""}}]}"""))
        assertNull(AiRequests.text("not json"))
    }

    @Test
    fun `stream chunks are read from both shapes`() {
        assertEquals("Ho", AiRequests.streamText("""{"choices":[{"delta":{"content":"Ho"}}]}"""))
        assertNull(AiRequests.streamText("""{"choices":[{"delta":{"content":null}}]}"""))
        assertEquals("la", AiRequests.streamText("""{"type":"content_block_delta","index":1,"delta":{"type":"text_delta","text":"la"}}"""))
        assertNull(AiRequests.streamText("""{"type":"content_block_delta","index":0,"delta":{"type":"thinking_delta","thinking":"x"}}"""))
        assertTrue(AiRequests.isStreamEnd("[DONE]"))
        assertTrue(AiRequests.isStreamEnd("""{"type":"message_stop"}"""))
        assertFalse(AiRequests.isStreamEnd("""{"type":"message_delta"}"""))
    }

    @Test
    fun `json is pulled out of chatty replies`() {
        assertEquals("""[{"title":"a"}]""", AiRequests.jsonPart("Here you go:\n```json\n[{\"title\":\"a\"}]\n```", '['))
        assertEquals("""[{"title":"a"}]""", AiRequests.jsonPart("""{"songs":[{"title":"a"}]}""", '['))
        assertEquals("""{"remove_ids":[1],"additions":[]}""", AiRequests.jsonPart("""Sure! {"remove_ids":[1],"additions":[]} Done.""", '{'))
        assertEquals("no json", AiRequests.jsonPart("no json", '['))
    }

    @Test
    fun `provider errors keep their own message`() {
        assertEquals("model not found", AiRequests.errorMessage("""{"error":{"message":"model not found"}}""", 404, "Not Found"))
        assertEquals("invalid x-api-key", AiRequests.errorMessage("""{"type":"error","error":{"type":"authentication_error","message":"invalid x-api-key"}}""", 401, ""))
        assertEquals("HTTP 502: Bad Gateway", AiRequests.errorMessage("<html>", 502, "Bad Gateway"))
    }
}
