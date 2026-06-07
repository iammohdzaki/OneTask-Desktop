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
        
        val markerIndices = mutableSetOf<Int>()
        val styles = mutableListOf<Triple<IntRange, IntRange, SpanStyle>>()

        // Rely on FormatEngine's centralized, AST-like priority parsing
        val spans = FormatEngine.parseFormats(originalText)
        
        for (span in spans) {
            val startMarker = span.fullRange.first until span.contentRange.first
            val endMarker = span.contentRange.last + 1 until span.fullRange.last + 1
            
            startMarker.forEach { markerIndices.add(it) }
            endMarker.forEach { markerIndices.add(it) }
            
            val spanStyle = when (span.marker) {
                "**" -> SpanStyle(fontWeight = FontWeight.Bold)
                "*"  -> SpanStyle(fontStyle = FontStyle.Italic)
                "__" -> SpanStyle(textDecoration = TextDecoration.Underline)
                "~~" -> SpanStyle(textDecoration = TextDecoration.LineThrough)
                else -> continue
            }
            styles.add(Triple(span.fullRange, span.contentRange, spanStyle))
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
