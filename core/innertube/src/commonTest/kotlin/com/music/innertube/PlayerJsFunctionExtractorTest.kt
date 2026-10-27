package com.music.innertube

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PlayerJsFunctionExtractorTest {
    @Test
    fun extractsSignatureTimestamp() {
        assertEquals(19_726, PlayerJsFunctionExtractor.signatureTimestamp("var player = {signatureTimestamp:19726};"))
        assertNull(PlayerJsFunctionExtractor.signatureTimestamp("var player = {};"))
    }

    @Test
    fun extractsModernSignatureFunctionAndConstantArgument() {
        val result = PlayerJsFunctionExtractor.signatureFunction(
            "if(a&&(b=DE(8,decodeURIComponent(a)))){return b}",
        )

        assertEquals(SignatureFunctionInfo("DE", 8), result)
    }

    @Test
    fun extractsLegacySignatureFunction() {
        val result = PlayerJsFunctionExtractor.signatureFunction(
            "c&&a.set(\"signature\",encodeURIComponent(XY(a)))",
        )

        assertEquals(SignatureFunctionInfo("XY", null), result)
    }

    @Test
    fun extractsArrayBasedThrottlingFunction() {
        val result = PlayerJsFunctionExtractor.throttlingFunction(
            "url.searchParams.get(\"n\"))&&(b=Fns[3](a))",
        )

        assertEquals(ThrottlingFunctionInfo("Fns", 3), result)
    }

    @Test
    fun extractsAssignedThrottlingFunction() {
        val result = PlayerJsFunctionExtractor.throttlingFunction(
            "url.searchParams.get(\"n\"))&&(b=Fns[2](b))",
        )

        assertEquals(ThrottlingFunctionInfo("Fns", 2), result)
    }
}
