package com.one.task.presentation.ui.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration

class RichTextVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        
        // Define strict markers and their styles
        // Bold: exactly two asterisks
        val boldRegex = Regex("(?<!\\*)\\*\\*(?!\\*)(.*?)(?<!\\*)\\*\\*(?!\\*)", RegexOption.DOT_MATCHES_ALL)
        // Italic: exactly one asterisk
        val italicRegex = Regex("(?<!\\*)\\*(?!\\*)(.*?)(?<!\\*)\\*(?!\\*)", RegexOption.DOT_MATCHES_ALL)
        // Underline: exactly two underscores
        val underlineRegex = Regex("(?<!_)__(?!_)(.*?)(?<!_)__(?!_)", RegexOption.DOT_MATCHES_ALL)
        // Strikethrough: exactly two tildes
        val strikeRegex = Regex("(?<!~)~~(?!~)(.*?)(?<!~)~~(?!~)", RegexOption.DOT_MATCHES_ALL)

        val markerIndices = mutableSetOf<Int>()
        val styles = mutableListOf<Triple<IntRange, IntRange, SpanStyle>>()

        // Find all matches and record markers and styles
        // Priority order: Bold, Underline, Strike, then Italic (to avoid single star matching double)
        
        boldRegex.findAll(originalText).forEach { match ->
            val contentRange = match.groups[1]?.range ?: return@forEach
            val startMarker = match.range.first until contentRange.first
            val endMarker = contentRange.last + 1 until match.range.last + 1
            
            startMarker.forEach { markerIndices.add(it) }
            endMarker.forEach { markerIndices.add(it) }
            styles.add(Triple(match.range, contentRange, SpanStyle(fontWeight = FontWeight.Bold)))
        }

        underlineRegex.findAll(originalText).forEach { match ->
            val contentRange = match.groups[1]?.range ?: return@forEach
            val startMarker = match.range.first until contentRange.first
            val endMarker = contentRange.last + 1 until match.range.last + 1
            
            startMarker.forEach { markerIndices.add(it) }
            endMarker.forEach { markerIndices.add(it) }
            styles.add(Triple(match.range, contentRange, SpanStyle(textDecoration = TextDecoration.Underline)))
        }

        strikeRegex.findAll(originalText).forEach { match ->
            val contentRange = match.groups[1]?.range ?: return@forEach
            val startMarker = match.range.first until contentRange.first
            val endMarker = contentRange.last + 1 until match.range.last + 1
            
            startMarker.forEach { markerIndices.add(it) }
            endMarker.forEach { markerIndices.add(it) }
            styles.add(Triple(match.range, contentRange, SpanStyle(textDecoration = TextDecoration.LineThrough)))
        }

        italicRegex.findAll(originalText).forEach { match ->
            val contentRange = match.groups[1]?.range ?: return@forEach
            val startMarker = match.range.first until contentRange.first
            val endMarker = contentRange.last + 1 until match.range.last + 1
            
            // Check if markers are already used (e.g. bold)
            if (markerIndices.contains(match.range.first) || markerIndices.contains(match.range.last)) return@forEach

            startMarker.forEach { markerIndices.add(it) }
            endMarker.forEach { markerIndices.add(it) }
            styles.add(Triple(match.range, contentRange, SpanStyle(fontStyle = FontStyle.Italic)))
        }

        // Build visual text and mapping
        val visualText = StringBuilder()
        val originalToVisual = IntArray(originalText.length + 1)
        val visualToOriginal = mutableListOf<Int>()
        
        var currentVisualIndex = 0
        for (i in 0..originalText.length) {
            originalToVisual[i] = currentVisualIndex
            if (i < originalText.length) {
                if (i !in markerIndices) {
                    visualText.append(originalText[i])
                    visualToOriginal.add(i)
                    currentVisualIndex++
                }
            } else {
                visualToOriginal.add(i)
            }
        }
        
        val annotatedString = buildAnnotatedString {
            append(visualText.toString())
            styles.forEach { (matchRange, contentRange, style) ->
                val start = originalToVisual[matchRange.first]
                val end = originalToVisual[matchRange.last + 1]
                if (start < end) {
                    addStyle(style, start, end)
                }
            }
        }
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return if (offset in originalToVisual.indices) originalToVisual[offset] else currentVisualIndex
            }
            
            override fun transformedToOriginal(offset: Int): Int {
                return if (offset in visualToOriginal.indices) visualToOriginal[offset] else originalText.length
            }
        }
        
        return TransformedText(annotatedString, offsetMapping)
    }
}
