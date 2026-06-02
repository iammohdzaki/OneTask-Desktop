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
        val markers = mutableListOf<IntRange>()
        
        // Find all markers
        val boldRegex = Regex("\\*\\*(.*?)\\*\\*", RegexOption.DOT_MATCHES_ALL)
        boldRegex.findAll(originalText).forEach { match ->
            markers.add(match.range.first..match.range.first + 1)
            markers.add(match.range.last - 1..match.range.last)
        }
        
        val italicRegex = Regex("(?<!\\*)\\*(?!\\*)(.*?)(?<!\\*)\\*(?!\\*)", RegexOption.DOT_MATCHES_ALL)
        italicRegex.findAll(originalText).forEach { match ->
            markers.add(match.range.first..match.range.first)
            markers.add(match.range.last..match.range.last)
        }
        
        val underlineRegex = Regex("__(.*?)__", RegexOption.DOT_MATCHES_ALL)
        underlineRegex.findAll(originalText).forEach { match ->
            markers.add(match.range.first..match.range.first + 1)
            markers.add(match.range.last - 1..match.range.last)
        }
        
        val strikeRegex = Regex("~~(.*?)~~", RegexOption.DOT_MATCHES_ALL)
        strikeRegex.findAll(originalText).forEach { match ->
            markers.add(match.range.first..match.range.first + 1)
            markers.add(match.range.last - 1..match.range.last)
        }
        
        // Flatten and deduplicate markers
        val markerIndices = mutableSetOf<Int>()
        markers.forEach { range ->
            for (i in range) {
                markerIndices.add(i)
            }
        }
        
        val visualText = StringBuilder()
        val originalToVisual = IntArray(originalText.length + 1)
        val visualToOriginal = mutableListOf<Int>()
        
        var currentVisualIndex = 0
        for (i in 0..originalText.length) {
            if (i < originalText.length) {
                if (i !in markerIndices) {
                    visualText.append(originalText[i])
                    visualToOriginal.add(i)
                    originalToVisual[i] = currentVisualIndex
                    currentVisualIndex++
                } else {
                    originalToVisual[i] = currentVisualIndex
                }
            } else {
                originalToVisual[i] = currentVisualIndex
                visualToOriginal.add(i)
            }
        }
        
        val annotatedString = buildAnnotatedString {
            append(visualText.toString())
            
            // Apply styles using the mapped offsets
            boldRegex.findAll(originalText).forEach { match ->
                val start = originalToVisual[match.range.first]
                val end = originalToVisual[match.range.last + 1]
                addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
            }
            
            italicRegex.findAll(originalText).forEach { match ->
                val start = originalToVisual[match.range.first]
                val end = originalToVisual[match.range.last + 1]
                addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
            }
            
            underlineRegex.findAll(originalText).forEach { match ->
                val start = originalToVisual[match.range.first]
                val end = originalToVisual[match.range.last + 1]
                addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
            }
            
            strikeRegex.findAll(originalText).forEach { match ->
                val start = originalToVisual[match.range.first]
                val end = originalToVisual[match.range.last + 1]
                addStyle(SpanStyle(textDecoration = TextDecoration.LineThrough), start, end)
            }
        }
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return if (offset in originalToVisual.indices) originalToVisual[offset] else originalToVisual.lastOrNull() ?: 0
            }
            
            override fun transformedToOriginal(offset: Int): Int {
                return if (offset in visualToOriginal.indices) visualToOriginal[offset] else visualToOriginal.lastOrNull() ?: 0
            }
        }
        
        return TransformedText(annotatedString, offsetMapping)
    }
}