package com.one.task.presentation.ui.utils

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Pure, stateless formatting engine.
 * No Compose dependencies — fully unit-testable.
 */
object FormatEngine {

    data class FormatSpan(
        val marker: String,
        val fullRange: IntRange,
        val contentRange: IntRange
    )

    /**
     * Parses the text and returns a list of valid format spans.
     * Uses an O(N) state machine to perfectly match Markdown pairs,
     * handling `***` edge cases and adjacent spans properly.
     */
    fun parseFormats(text: String): List<FormatSpan> {
        val spans = mutableListOf<FormatSpan>()
        val markerIndices = mutableSetOf<Int>()

        // Helper to find non-overlapping spans of a specific marker
        fun extractSpans(marker: String) {
            var i = 0
            var openIndex = -1
            
            while (i <= text.length - marker.length) {
                if (text.substring(i, i + marker.length) == marker) {
                    val isClaimed = (i until i + marker.length).any { it in markerIndices }
                    
                    if (!isClaimed) {
                        if (openIndex == -1) {
                            openIndex = i
                            i += marker.length - 1
                        } else {
                            // We found a closing marker
                            val fullRange = openIndex until i + marker.length
                            val contentRange = openIndex + marker.length until i
                            
                            // Only add if content is not empty
                            if (contentRange.last >= contentRange.first) {
                                spans.add(FormatSpan(marker, fullRange, contentRange))
                                (openIndex until openIndex + marker.length).forEach { markerIndices.add(it) }
                                (i until i + marker.length).forEach { markerIndices.add(it) }
                                openIndex = -1
                                i += marker.length - 1
                            }
                        }
                    }
                }
                i++
            }
        }

        // Priority order
        extractSpans("**")
        extractSpans("__")
        extractSpans("~~")
        extractSpans("*")

        return spans
    }

    // ── isFormatActive ────────────────────────────────────────────────────────
    /**
     * Returns true if the given [selection] is inside (or exactly wrapping) a
     * span of [marker]-formatted text in [text].
     */
    fun isFormatActive(text: String, selection: TextRange, marker: String): Boolean {
        if (text.isEmpty()) return false
        val min = minOf(selection.start, selection.end)
        val max = maxOf(selection.start, selection.end)

        val spans = parseFormats(text).filter { it.marker == marker }
        
        return spans.any { span ->
            // Cursor exactly wrapping the content
            if (min == span.contentRange.first && max == span.contentRange.last + 1) return@any true
            // Cursor/selection inside the content
            min >= span.contentRange.first && max <= span.contentRange.last + 1
        }
    }

    // ── applyFormat ───────────────────────────────────────────────────────────
    fun applyFormat(
        value: TextFieldValue,
        marker: String,
        frozenSelection: TextRange
    ): TextFieldValue {
        val text = value.text
        val min  = minOf(frozenSelection.start, frozenSelection.end)
        val max  = maxOf(frozenSelection.start, frozenSelection.end)

        // Clamp to valid range
        val safeMin = min.coerceIn(0, text.length)
        val safeMax = max.coerceIn(0, text.length)

        val spans = parseFormats(text).filter { it.marker == marker }

        // Check if selection exactly wraps existing content
        val exactMatch = spans.find { span ->
            safeMin == span.contentRange.first && safeMax == span.contentRange.last + 1
        }
        
        // Or if cursor/selection is inside an existing span
        val containingMatch = spans.find { span ->
            safeMin >= span.contentRange.first && safeMax <= span.contentRange.last + 1
        }

        return when {
            // Toggle OFF — selection exactly wrapped
            exactMatch != null -> {
                val newText = text.substring(0, exactMatch.fullRange.first) +
                              text.substring(exactMatch.contentRange.first, exactMatch.contentRange.last + 1) +
                              text.substring(exactMatch.fullRange.last + 1)
                TextFieldValue(newText, TextRange(safeMin - marker.length, safeMax - marker.length))
            }
            // Toggle OFF — cursor/selection inside a span
            containingMatch != null -> {
                val mStart = containingMatch.fullRange.first
                val mEnd   = containingMatch.fullRange.last + 1
                val newText = text.substring(0, mStart) +
                              text.substring(containingMatch.contentRange.first, containingMatch.contentRange.last + 1) +
                              text.substring(mEnd)
                val adjustedMin = (safeMin - marker.length).coerceAtLeast(0)
                val adjustedMax = (safeMax - marker.length).coerceAtLeast(0)
                TextFieldValue(newText, TextRange(adjustedMin, adjustedMax))
            }
            // Toggle ON — no selection, insert empty markers and place cursor between them
            safeMin == safeMax -> {
                val newText = text.substring(0, safeMin) + marker + marker + text.substring(safeMin)
                TextFieldValue(newText, TextRange(safeMin + marker.length))
            }
            // Toggle ON — wrap selection
            else -> {
                val newText = text.substring(0, safeMin) +
                              marker +
                              text.substring(safeMin, safeMax) +
                              marker +
                              text.substring(safeMax)
                TextFieldValue(newText, TextRange(safeMin + marker.length, safeMax + marker.length))
            }
        }
    }
}
