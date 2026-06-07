package com.one.task.presentation.ui.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [RichTextVisualTransformation].
 *
 * Each test calls [filter] with raw markdown text and asserts:
 *   1. The visual (displayed) text has all markers removed.
 *   2. The correct [SpanStyle]s are present.
 *   3. The [OffsetMapping] correctly translates between raw and visual positions.
 */
class RichTextVisualTransformationTest {

    private val transform = RichTextVisualTransformation()

    private fun filter(raw: String) =
        transform.filter(AnnotatedString(raw))

    // ─────────────────────────────────────────────────────────────────────────
    // Visual text — markers hidden
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `bold markers are hidden in visual text`() {
        val result = filter("**hello**")
        assertEquals("hello", result.text.text)
    }

    @Test
    fun `italic markers are hidden in visual text`() {
        val result = filter("*hello*")
        assertEquals("hello", result.text.text)
    }

    @Test
    fun `underline markers are hidden in visual text`() {
        val result = filter("__hello__")
        assertEquals("hello", result.text.text)
    }

    @Test
    fun `strikethrough markers are hidden in visual text`() {
        val result = filter("~~hello~~")
        assertEquals("hello", result.text.text)
    }

    @Test
    fun `plain text is unchanged`() {
        val result = filter("hello world")
        assertEquals("hello world", result.text.text)
    }

    @Test
    fun `empty string produces empty visual text`() {
        val result = filter("")
        assertEquals("", result.text.text)
    }

    @Test
    fun `mixed formats - all markers are hidden`() {
        // raw: **bold** and *italic* and ~~strike~~
        val result = filter("**bold** and *italic* and ~~strike~~")
        assertEquals("bold and italic and strike", result.text.text)
    }

    @Test
    fun `triple-star nested - both bold and italic markers hidden`() {
        // **Bold - *Italic*** → visual "Bold - Italic"
        val result = filter("**Bold - *Italic***")
        assertEquals("Bold - Italic", result.text.text)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SpanStyle application — bold
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `bold span style covers entire visual content`() {
        val result = filter("**hello**")
        val spans = result.text.spanStyles
        val boldSpan = spans.firstOrNull { it.item.fontWeight == FontWeight.Bold }
        assertNotNull(boldSpan, "Expected a Bold SpanStyle to be present")
        // visual text is "hello" (5 chars), style should cover 0..5
        assertEquals(0, boldSpan.start)
        assertEquals(5, boldSpan.end)
    }

    @Test
    fun `bold span only covers the bold word in mixed text`() {
        // "hello **world** end" → visual "hello world end"
        // bold should cover "world" at visual positions 6..11
        val result = filter("hello **world** end")
        val boldSpan = result.text.spanStyles.firstOrNull { it.item.fontWeight == FontWeight.Bold }
        assertNotNull(boldSpan)
        assertEquals(6, boldSpan.start)
        assertEquals(11, boldSpan.end)
    }

    @Test
    fun `multiple bold spans are independently applied`() {
        val result = filter("**a** and **b**")
        val boldSpans = result.text.spanStyles.filter { it.item.fontWeight == FontWeight.Bold }
        assertEquals(2, boldSpans.size)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SpanStyle application — italic
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `italic span style covers entire visual content`() {
        val result = filter("*hello*")
        val italicSpan = result.text.spanStyles.firstOrNull { it.item.fontStyle == FontStyle.Italic }
        assertNotNull(italicSpan, "Expected an Italic SpanStyle to be present")
        assertEquals(0, italicSpan.start)
        assertEquals(5, italicSpan.end)
    }

    @Test
    fun `italic inside bold triple-star - italic span applied to inner word only`() {
        // **Bold - *Italic*** → visual "Bold - Italic"
        // italic should cover "Italic" = visual positions 7..13
        val result = filter("**Bold - *Italic***")
        val italicSpan = result.text.spanStyles.firstOrNull { it.item.fontStyle == FontStyle.Italic }
        assertNotNull(italicSpan, "Expected Italic span inside **Bold - *Italic***")
        assertEquals("Italic", result.text.text.substring(italicSpan.start, italicSpan.end))
    }

    @Test
    fun `bold inside triple-star - bold span covers full content`() {
        val result = filter("**Bold - *Italic***")
        val boldSpan = result.text.spanStyles.firstOrNull { it.item.fontWeight == FontWeight.Bold }
        assertNotNull(boldSpan, "Expected Bold span inside **Bold - *Italic***")
        assertEquals("Bold - Italic", result.text.text.substring(boldSpan.start, boldSpan.end))
    }

    @Test
    fun `bold asterisks do not incorrectly trigger italic`() {
        // **bold** should NOT create an italic span
        val result = filter("**bold**")
        val italicSpan = result.text.spanStyles.firstOrNull { it.item.fontStyle == FontStyle.Italic }
        assertNull(italicSpan, "Bold-only text should not produce an italic span")
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SpanStyle application — underline
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `underline span style covers visual content`() {
        val result = filter("__hello__")
        val ulSpan = result.text.spanStyles.firstOrNull {
            it.item.textDecoration == TextDecoration.Underline
        }
        assertNotNull(ulSpan, "Expected Underline SpanStyle")
        assertEquals(0, ulSpan.start)
        assertEquals(5, ulSpan.end)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SpanStyle application — strikethrough
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `strikethrough span style covers visual content`() {
        val result = filter("~~hello~~")
        val stSpan = result.text.spanStyles.firstOrNull {
            it.item.textDecoration == TextDecoration.LineThrough
        }
        assertNotNull(stSpan, "Expected LineThrough SpanStyle")
        assertEquals(0, stSpan.start)
        assertEquals(5, stSpan.end)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // OffsetMapping — originalToTransformed
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `bold - original pos 0 (on opening marker) maps to visual pos 0`() {
        // raw: **hello** (0=*, 1=*, 2=h, ..., 6=o, 7=*, 8=*)
        // visual: hello (0=h, ..., 4=o)
        val result = filter("**hello**")
        val mapping = result.offsetMapping
        // Both raw positions 0 and 1 (the markers) map to visual position 0
        assertEquals(0, mapping.originalToTransformed(0))
        assertEquals(0, mapping.originalToTransformed(1))
    }

    @Test
    fun `bold - original content positions map correctly to visual positions`() {
        val result = filter("**hello**")
        val mapping = result.offsetMapping
        // raw pos 2 → 'h' → visual pos 0
        assertEquals(0, mapping.originalToTransformed(2))
        // raw pos 6 → 'o' → visual pos 4
        assertEquals(4, mapping.originalToTransformed(6))
    }

    @Test
    fun `italic - original pos on closing marker maps to visual end`() {
        // raw: *hello* (0=*, 1=h, ..., 5=o, 6=*)
        val result = filter("*hello*")
        val mapping = result.offsetMapping
        // raw pos 6 (closing *) → visual pos 5 (end of "hello")
        assertEquals(5, mapping.originalToTransformed(6))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // OffsetMapping — transformedToOriginal
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `bold - visual pos 0 maps back to original pos 2 (skip opening markers)`() {
        val result = filter("**hello**")
        val mapping = result.offsetMapping
        assertEquals(2, mapping.transformedToOriginal(0))
    }

    @Test
    fun `italic - visual pos 0 maps back to original pos 1`() {
        val result = filter("*hello*")
        val mapping = result.offsetMapping
        assertEquals(1, mapping.transformedToOriginal(0))
    }

    @Test
    fun `plain text - offset mapping is identity`() {
        val result = filter("hello")
        val mapping = result.offsetMapping
        for (i in 0..5) {
            assertEquals(i, mapping.originalToTransformed(i))
            assertEquals(i, mapping.transformedToOriginal(i))
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Edge cases
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `incomplete bold marker not formatted`() {
        // Single ** with no closing → no bold span, raw text preserved
        val result = filter("**hello")
        assertEquals("**hello", result.text.text)
        assertTrue(result.text.spanStyles.none { it.item.fontWeight == FontWeight.Bold })
    }

    @Test
    fun `incomplete italic marker not formatted`() {
        val result = filter("*hello")
        assertEquals("*hello", result.text.text)
        assertTrue(result.text.spanStyles.none { it.item.fontStyle == FontStyle.Italic })
    }

    @Test
    fun `adjacent bold and italic spans are both applied`() {
        // **bold***italic*  → visual: "bolditalic"
        // Bold on "bold" (0..4), italic on "italic" (4..10)
        val result = filter("**bold***italic*")
        val boldSpan = result.text.spanStyles.firstOrNull { it.item.fontWeight == FontWeight.Bold }
        val italicSpan = result.text.spanStyles.firstOrNull { it.item.fontStyle == FontStyle.Italic }
        assertNotNull(boldSpan)
        assertNotNull(italicSpan)
        // They should not overlap (bold ends where italic begins)
        assertTrue(boldSpan.end <= italicSpan.start || italicSpan.end <= boldSpan.start,
            "Bold and italic spans should not overlap in adjacent case")
    }

    @Test
    fun `normal text with dashes and symbols is not formatted`() {
        val raw = "Normal - text -- dashes"
        val result = filter(raw)
        assertEquals(raw, result.text.text)
        assertTrue(result.text.spanStyles.isEmpty())
    }

    @Test
    fun `full mixed format line from user example`() {
        // User's exact test string
        val raw = "Normal - **Bold - *Italic*** - Underline -~~crossed~~"
        val result = filter(raw)

        // Visual text should have all markers stripped
        assertEquals("Normal - Bold - Italic - Underline -crossed", result.text.text)

        val spans = result.text.spanStyles
        val hasBold   = spans.any { it.item.fontWeight == FontWeight.Bold }
        val hasItalic = spans.any { it.item.fontStyle == FontStyle.Italic }
        val hasStrike = spans.any { it.item.textDecoration == TextDecoration.LineThrough }

        assertTrue(hasBold,   "Expected bold span")
        assertTrue(hasItalic, "Expected italic span")
        assertTrue(hasStrike, "Expected strikethrough span")
    }
}
