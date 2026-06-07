package com.one.task.presentation.ui.utils

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for [FormatEngine].
 *
 * Covers:
 *  - isFormatActive: bold, italic, underline, strikethrough
 *  - applyFormat: toggle-on (wrap), toggle-off (unwrap exact), toggle-off (unwrap containing)
 *  - Boundary and edge cases: empty text, no selection, selection at boundaries
 *  - Nested ***bold+italic*** interaction
 */
class FormatEngineTest {

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun tfv(text: String, start: Int = 0, end: Int = 0) =
        TextFieldValue(text, selection = TextRange(start, end))

    private fun range(start: Int, end: Int) = TextRange(start, end)

    // ─────────────────────────────────────────────────────────────────────────
    // isFormatActive — Bold (**)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `isFormatActive bold - cursor inside bold span returns true`() {
        val text = "**hello**"
        // cursor at position 4 (inside "hello")
        assertTrue(FormatEngine.isFormatActive(text, range(4, 4), "**"))
    }

    @Test
    fun `isFormatActive bold - selection exactly wrapping bold content returns true`() {
        val text = "**hello**"
        assertTrue(FormatEngine.isFormatActive(text, range(2, 7), "**"))
    }

    @Test
    fun `isFormatActive bold - cursor outside bold span returns false`() {
        val text = "**hello** world"
        assertFalse(FormatEngine.isFormatActive(text, range(11, 11), "**"))
    }

    @Test
    fun `isFormatActive bold - plain text returns false`() {
        assertFalse(FormatEngine.isFormatActive("hello", range(2, 2), "**"))
    }

    @Test
    fun `isFormatActive bold - empty text returns false`() {
        assertFalse(FormatEngine.isFormatActive("", range(0, 0), "**"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // isFormatActive — Italic (*)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `isFormatActive italic - cursor inside italic span returns true`() {
        val text = "*hello*"
        assertTrue(FormatEngine.isFormatActive(text, range(3, 3), "*"))
    }

    @Test
    fun `isFormatActive italic - bold asterisks do not trigger italic detection`() {
        val text = "**hello**"
        // single-* check should NOT match **
        assertFalse(FormatEngine.isFormatActive(text, range(4, 4), "*"))
    }

    @Test
    fun `isFormatActive italic - italic inside bold triple-star sequence`() {
        // **Bold - *Italic*** : italic span is *Italic* (positions 9..16 in zero-based)
        val text = "**Bold - *Italic***"
        // cursor inside "Italic" (position 12 is 'a' in Italic)
        assertTrue(FormatEngine.isFormatActive(text, range(12, 12), "*"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // isFormatActive — Underline (__) and Strikethrough (~~)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `isFormatActive underline - cursor inside underline span`() {
        assertTrue(FormatEngine.isFormatActive("__hello__", range(4, 4), "__"))
    }

    @Test
    fun `isFormatActive underline - double-underscore not confused with italic underscore`() {
        assertFalse(FormatEngine.isFormatActive("__hello__", range(4, 4), "*"))
    }

    @Test
    fun `isFormatActive strikethrough - cursor inside strike span`() {
        assertTrue(FormatEngine.isFormatActive("~~hello~~", range(4, 4), "~~"))
    }

    @Test
    fun `isFormatActive strikethrough - plain text returns false`() {
        assertFalse(FormatEngine.isFormatActive("hello", range(2, 2), "~~"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // applyFormat — Toggle ON (wrap selection)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `applyFormat bold - wraps selected text with double asterisks`() {
        val value = tfv("hello world")
        val result = FormatEngine.applyFormat(value, "**", range(0, 5))
        assertEquals("**hello** world", result.text)
        // selection should surround the content (inside markers)
        assertEquals(TextRange(2, 7), result.selection)
    }

    @Test
    fun `applyFormat italic - wraps selected text with single asterisk`() {
        val value = tfv("hello world")
        val result = FormatEngine.applyFormat(value, "*", range(6, 11))
        assertEquals("hello *world*", result.text)
        assertEquals(TextRange(7, 12), result.selection)
    }

    @Test
    fun `applyFormat underline - wraps selected text with double underscores`() {
        val value = tfv("hello")
        val result = FormatEngine.applyFormat(value, "__", range(0, 5))
        assertEquals("__hello__", result.text)
        assertEquals(TextRange(2, 7), result.selection)
    }

    @Test
    fun `applyFormat strikethrough - wraps selected text with double tildes`() {
        val value = tfv("hello")
        val result = FormatEngine.applyFormat(value, "~~", range(0, 5))
        assertEquals("~~hello~~", result.text)
        assertEquals(TextRange(2, 7), result.selection)
    }

    @Test
    fun `applyFormat bold - no selection inserts empty markers and places cursor between`() {
        val value = tfv("hello", start = 3, end = 3)
        val result = FormatEngine.applyFormat(value, "**", range(3, 3))
        assertEquals("hel****lo", result.text)
        // cursor should be between the two ** pairs
        assertEquals(TextRange(5, 5), result.selection)
    }

    @Test
    fun `applyFormat italic - no selection inserts empty markers`() {
        val value = tfv("", start = 0, end = 0)
        val result = FormatEngine.applyFormat(value, "*", range(0, 0))
        assertEquals("**", result.text)
        assertEquals(TextRange(1, 1), result.selection)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // applyFormat — Toggle OFF (exact wrap unwrap)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `applyFormat bold toggle-off - removes markers when selection wraps content exactly`() {
        // text = "**hello**", frozen selection points to the content "hello" (2..7)
        val value = tfv("**hello**")
        val result = FormatEngine.applyFormat(value, "**", range(2, 7))
        assertEquals("hello", result.text)
        assertEquals(TextRange(0, 5), result.selection)
    }

    @Test
    fun `applyFormat italic toggle-off - removes markers when selection wraps content exactly`() {
        val value = tfv("*hello*")
        val result = FormatEngine.applyFormat(value, "*", range(1, 6))
        assertEquals("hello", result.text)
        assertEquals(TextRange(0, 5), result.selection)
    }

    @Test
    fun `applyFormat strikethrough toggle-off - removes markers`() {
        val value = tfv("~~hello~~")
        val result = FormatEngine.applyFormat(value, "~~", range(2, 7))
        assertEquals("hello", result.text)
        assertEquals(TextRange(0, 5), result.selection)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // applyFormat — Toggle OFF (cursor inside span)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `applyFormat bold toggle-off - cursor inside bold span removes entire span`() {
        val value = tfv("**hello**")
        // cursor at position 4 (inside "hello")
        val result = FormatEngine.applyFormat(value, "**", range(4, 4))
        assertEquals("hello", result.text)
    }

    @Test
    fun `applyFormat italic toggle-off - cursor inside italic span removes entire span`() {
        val value = tfv("*hello*")
        val result = FormatEngine.applyFormat(value, "*", range(3, 3))
        assertEquals("hello", result.text)
    }

    @Test
    fun `applyFormat underline toggle-off - cursor inside underline span removes entire span`() {
        val value = tfv("__hello__")
        val result = FormatEngine.applyFormat(value, "__", range(4, 4))
        assertEquals("hello", result.text)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // applyFormat — Multiple spans in text
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `applyFormat bold - only the span containing the cursor is removed`() {
        val value = tfv("**hello** and **world**")
        // cursor inside second bold span "world" at position 18
        val result = FormatEngine.applyFormat(value, "**", range(18, 18))
        assertEquals("**hello** and world", result.text)
    }

    @Test
    fun `applyFormat italic - adding italic to plain text between two italic spans`() {
        val value = tfv("*a* middle *b*")
        // select " middle " (positions 3..11)
        val result = FormatEngine.applyFormat(value, "*", range(3, 11))
        assertEquals("*a** middle **b*", result.text)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // applyFormat — Boundary and edge cases
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `applyFormat bold - selection clamped to text bounds does not throw`() {
        val value = tfv("hi")
        // safeMax would clamp 100 → 2
        val result = FormatEngine.applyFormat(value, "**", range(0, 100))
        assertEquals("**hi**", result.text)
    }

    @Test
    fun `applyFormat - full text selected and formatted then toggled off`() {
        val original = tfv("hello")
        val wrapped = FormatEngine.applyFormat(original, "**", range(0, 5))
        assertEquals("**hello**", wrapped.text)

        val unwrapped = FormatEngine.applyFormat(wrapped, "**", wrapped.selection)
        assertEquals("hello", unwrapped.text)
    }

    @Test
    fun `applyFormat italic - idempotent applying twice toggles back to original`() {
        val original = tfv("world")
        val once = FormatEngine.applyFormat(original, "*", range(0, 5))
        assertEquals("*world*", once.text)

        // Pressing italic again with content selection should toggle off
        val twice = FormatEngine.applyFormat(once, "*", once.selection)
        assertEquals("world", twice.text)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Nested *** (bold + italic) interaction
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `isFormatActive bold - cursor inside bold in triple-star sequence`() {
        val text = "**Bold - *Italic***"
        // cursor at position 3 (inside "Bold")
        assertTrue(FormatEngine.isFormatActive(text, range(3, 3), "**"))
    }

    @Test
    fun `applyFormat italic - removing italic from triple-star sequence`() {
        val value = tfv("**Bold - *Italic***")
        // cursor at position 13 (inside "Italic")
        val result = FormatEngine.applyFormat(value, "*", range(13, 13))
        // Should remove the *...* markers, leaving bold intact
        assertEquals("**Bold - Italic**", result.text)
    }
}
