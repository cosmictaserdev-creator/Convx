package com.convxy.music.betterlyrics

import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

object TTMLParser {

    data class ParsedLine(
        val text: String,
        val startTime: Double,
        val words: List<ParsedWord>,
        val agent: String? = null,
        val isBackground: Boolean = false,
        val backgroundLines: List<ParsedLine> = emptyList()
    )

    data class ParsedWord(
        val text: String,
        val startTime: Double,
        val endTime: Double
    )

    /**
     * A vocalist declared in the TTML metadata registry, mirroring Apple's
     * `<ttm:agent type="person" xml:id="v1"><ttm:name type="full">Name</ttm:name></ttm:agent>`.
     *
     * @param id the voice id referenced by `ttm:agent` attributes (e.g. "v1").
     * @param name the performer's display name, when delivered.
     * @param type "person", "group" (shared vocals, conventionally v1000) or "other".
     */
    data class TtmlAgent(
        val id: String,
        val name: String?,
        val type: String?
    )

    
    private data class SpanInfo(
        val text: String,
        val startTime: Double,
        val endTime: Double,
        val hasTrailingSpace: Boolean
    )
    
    /**
     * Walks this element and its ancestors for a `ttm:agent` (or unprefixed
     * `agent`) attribute. Apple often stamps the voice on the enclosing `<div>`
     * and omits it on continuation `<p>`s — without inheritance those lines
     * get painted as the wrong singer.
     */
    private fun Element.findAgent(): String? {
        var current: Node? = this
        while (current != null) {
            if (current is Element) {
                val value = current.getAttributeByLocalName("agent")
                if (value.isNotEmpty()) return value
            }
            current = current.parentNode
        }
        return null
    }

    // Helper function to get attribute by local name (handles namespace prefixes)
    private fun Element.getAttributeByLocalName(localName: String): String {
        // First try namespace-aware lookup
        val nsValue = getAttributeNS("http://www.w3.org/ns/ttml#metadata", localName)
        if (nsValue.isNotEmpty()) return nsValue
        
        // Then try with common prefixes
        val prefixedValue = getAttribute("ttm:$localName")
        if (prefixedValue.isNotEmpty()) return prefixedValue
        
        // Finally, search through all attributes
        val attrs = attributes
        for (i in 0 until attrs.length) {
            val attr = attrs.item(i)
            val attrName = attr.nodeName ?: continue
            if (attrName == localName || attrName.endsWith(":$localName")) {
                return attr.nodeValue ?: ""
            }
        }
        return ""
    }
    
    /**
     * Extracts the vocalist registry from the TTML `<head><metadata>` section.
     *
     * Elements with local name "agent" are collected regardless of namespace
     * prefix (`ttm:agent` or bare). The performer name is read from a child
     * `ttm:name` element first (Apple's delivery format) and from a `ttm:name`
     * attribute as fallback. Returns an empty list for TTML documents without
     * agent metadata (anonymous v1/v2 duets), which remain fully functional.
     */
    fun parseAgents(ttml: String): List<TtmlAgent> {
        return try {
            val factory = DocumentBuilderFactory.newInstance()
            factory.isNamespaceAware = true
            val builder = factory.newDocumentBuilder()
            val doc = builder.parse(ttml.byteInputStream())

            val agents = mutableListOf<TtmlAgent>()
            val allElements = doc.getElementsByTagName("*")
            for (i in 0 until allElements.length) {
                val element = allElements.item(i) as? Element ?: continue
                val localName = element.localName?.takeIf { it.isNotEmpty() }
                    ?: element.nodeName.substringAfterLast(':')
                if (localName != "agent") continue

                // xml:id is the reference used by ttm:agent attributes
                val id = element.getAttributeByLocalName("id")
                if (id.isEmpty()) continue

                val type = element.getAttributeByLocalName("type").takeIf { it.isNotEmpty() }

                var name: String? = null
                val children = element.childNodes
                for (j in 0 until children.length) {
                    val child = children.item(j)
                    if (child.nodeType != Node.ELEMENT_NODE) continue
                    val childElement = child as? Element ?: continue
                    val childLocalName = childElement.localName?.takeIf { it.isNotEmpty() }
                        ?: childElement.nodeName.substringAfterLast(':')
                    if (childLocalName == "name") {
                        name = childElement.textContent?.trim()?.takeIf { it.isNotEmpty() }
                        break
                    }
                }
                if (name == null) {
                    name = element.getAttributeByLocalName("name").takeIf { it.isNotEmpty() }
                }

                agents.add(TtmlAgent(id = id, name = name, type = type))
            }
            agents
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseTTML(ttml: String): List<ParsedLine> {
        val lines = mutableListOf<ParsedLine>()
        
        try {
            val factory = DocumentBuilderFactory.newInstance()
            factory.isNamespaceAware = true
            val builder = factory.newDocumentBuilder()
            val doc = builder.parse(ttml.byteInputStream())
            
            val pElements = doc.getElementsByTagName("p")
            
            for (i in 0 until pElements.length) {
                val pElement = pElements.item(i) as? Element ?: continue
                
                val begin = pElement.getAttribute("begin")
                if (begin.isNullOrEmpty()) continue
                
                val startTime = parseTime(begin)
                val spanInfos = mutableListOf<SpanInfo>()
                val backgroundLines = mutableListOf<ParsedLine>()
                
                // Get agent/vocalist info (ttm:agent on the <p>, or inherited
                // from a parent <div> — Apple often stamps the voice on the
                // verse block and omits it on continuation lines).
                val agent = pElement.findAgent()
                
                // Parse child nodes to preserve whitespace between spans
                val childNodes = pElement.childNodes
                for (j in 0 until childNodes.length) {
                    val node = childNodes.item(j)
                    
                    when (node.nodeType) {
                        Node.ELEMENT_NODE -> {
                            val span = node as? Element
                            if (span?.tagName?.lowercase() == "span") {
                                // Check for background vocal role (ttm:role="x-bg")
                                val role = span.getAttributeByLocalName("role")
                                
                                when (role) {
                                    "x-bg" -> {
                                        // Parse background vocal line
                                        val bgLine = parseBackgroundSpan(span, startTime)
                                        if (bgLine != null) {
                                            backgroundLines.add(bgLine)
                                        }
                                    }
                                    "x-translation", "x-roman" -> {
                                        // Skip translation and romanization spans
                                    }
                                    else -> {
                                        // Regular word span
                                        val wordBegin = span.getAttribute("begin")
                                        val wordEnd = span.getAttribute("end")
                                        val wordText = span.textContent?.trim() ?: ""
                                        
                                        if (wordText.isNotEmpty() && wordBegin.isNotEmpty() && wordEnd.isNotEmpty()) {
                                            val nextSibling = node.nextSibling
                                            val hasTrailingSpace = nextSibling?.nodeType == Node.TEXT_NODE && 
                                                nextSibling.textContent?.contains(Regex("\\s")) == true
                                            
                                            spanInfos.add(
                                                SpanInfo(
                                                    text = wordText,
                                                    startTime = parseTime(wordBegin),
                                                    endTime = parseTime(wordEnd),
                                                    hasTrailingSpace = hasTrailingSpace
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Merge consecutive spans without whitespace between them into single words
                val words = mergeSpansIntoWords(spanInfos)
                val lineText = words.joinToString(" ") { it.text }
                
                // If no spans found, use text content directly (excluding background text)
                val finalText = if (lineText.isEmpty()) {
                    getDirectTextContent(pElement).trim()
                } else {
                    lineText
                }
                
                if (finalText.isNotEmpty()) {
                    lines.add(
                        ParsedLine(
                            text = finalText,
                            startTime = startTime,
                            words = words,
                            agent = agent,
                            isBackground = false,
                            backgroundLines = backgroundLines
                        )
                    )
                }
            }
        } catch (e: Exception) {
            return emptyList()
        }

        // Carry the last known lead voice onto lines that still have none.
        // Apple's convention is "agent persists until it changes"; a missing
        // attribute is a continuation, not a different singer.
        var lastAgent: String? = null
        return lines.map { line ->
            if (line.isBackground) {
                line
            } else {
                val agent = line.agent ?: lastAgent
                if (agent != null) lastAgent = agent
                if (agent == line.agent) line else line.copy(agent = agent)
            }
        }
    }
    
    private fun parseBackgroundSpan(span: Element, parentStartTime: Double): ParsedLine? {
        val bgBegin = span.getAttribute("begin")
        val bgEnd = span.getAttribute("end")
        val bgStartTime = if (bgBegin.isNotEmpty()) parseTime(bgBegin) else parentStartTime
        
        val spanInfos = mutableListOf<SpanInfo>()
        val childNodes = span.childNodes
        
        for (j in 0 until childNodes.length) {
            val node = childNodes.item(j)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val innerSpan = node as? Element
                if (innerSpan?.tagName?.lowercase() == "span") {
                    val role = innerSpan.getAttributeByLocalName("role")
                    
                    // Skip translation and romanization spans
                    if (role == "x-translation" || role == "x-roman") continue
                    
                    val wordBegin = innerSpan.getAttribute("begin")
                    val wordEnd = innerSpan.getAttribute("end")
                    val wordText = innerSpan.textContent?.trim() ?: ""
                    
                    if (wordText.isNotEmpty() && wordBegin.isNotEmpty() && wordEnd.isNotEmpty()) {
                        val nextSibling = node.nextSibling
                        val hasTrailingSpace = nextSibling?.nodeType == Node.TEXT_NODE && 
                            nextSibling.textContent?.contains(Regex("\\s")) == true
                        
                        spanInfos.add(
                            SpanInfo(
                                text = wordText,
                                startTime = parseTime(wordBegin),
                                endTime = parseTime(wordEnd),
                                hasTrailingSpace = hasTrailingSpace
                            )
                        )
                    }
                }
            }
        }
        
        val words = mergeSpansIntoWords(spanInfos)
        val lineText = words.joinToString(" ") { it.text }
        
        val finalText = if (lineText.isEmpty()) {
            getDirectTextContent(span).trim()
        } else {
            lineText
        }
        
        return if (finalText.isNotEmpty()) {
            ParsedLine(
                text = finalText,
                startTime = bgStartTime,
                words = words,
                agent = null,
                isBackground = true,
                backgroundLines = emptyList()
            )
        } else null
    }
    
    private fun getDirectTextContent(element: Element): String {
        val sb = StringBuilder()
        val childNodes = element.childNodes
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node.nodeType == Node.TEXT_NODE) {
                sb.append(node.textContent)
            } else if (node.nodeType == Node.ELEMENT_NODE) {
                val el = node as? Element
                val role = el?.getAttributeByLocalName("role") ?: ""
                // Skip background, translation, and romanization spans
                if (role != "x-bg" && role != "x-translation" && role != "x-roman") {
                    if (el?.tagName?.lowercase() == "span") {
                        sb.append(el.textContent ?: "")
                    }
                }
            }
        }
        return sb.toString()
    }
    
    private fun mergeSpansIntoWords(spanInfos: List<SpanInfo>): List<ParsedWord> {
        if (spanInfos.isEmpty()) return emptyList()
        
        val words = mutableListOf<ParsedWord>()
        var currentText = StringBuilder()
        var currentStartTime = spanInfos[0].startTime
        var currentEndTime = spanInfos[0].endTime
        
        for ((index, span) in spanInfos.withIndex()) {
            if (index == 0) {
                currentText.append(span.text)
                currentStartTime = span.startTime
                currentEndTime = span.endTime
            } else {
                // Check if previous span had trailing space (word boundary)
                val prevSpan = spanInfos[index - 1]
                if (prevSpan.hasTrailingSpace) {
                    // Save current word and start new one
                    if (currentText.isNotEmpty()) {
                        words.add(
                            ParsedWord(
                                text = currentText.toString().trim(),
                                startTime = currentStartTime,
                                endTime = currentEndTime
                            )
                        )
                    }
                    currentText = StringBuilder(span.text)
                    currentStartTime = span.startTime
                    currentEndTime = span.endTime
                } else {
                    // No space between spans - merge into same word (syllables)
                    currentText.append(span.text)
                    currentEndTime = span.endTime
                }
            }
        }
        
        // Add the last word
        if (currentText.isNotEmpty()) {
            words.add(
                ParsedWord(
                    text = currentText.toString().trim(),
                    startTime = currentStartTime,
                    endTime = currentEndTime
                )
            )
        }
        
        return words
    }
    
    fun toLRC(lines: List<ParsedLine>, agents: List<TtmlAgent> = emptyList()): String {
        return buildString {
            // Singer registry header so named vocalists survive the TTML -> LRC flattening
            val namedAgents = agents.filter { !it.name.isNullOrBlank() }
            if (namedAgents.isNotEmpty()) {
                appendLine(
                    "[singers:" + namedAgents.joinToString("|") { agent ->
                        "${agent.id}=${agent.name}"
                    } + "]"
                )
            }
            lines.forEach { line ->
                val timeMs = (line.startTime * 1000).toLong()
                val minutes = timeMs / 60000
                val seconds = (timeMs % 60000) / 1000
                val centiseconds = (timeMs % 1000) / 10
                
                // Add agent info if present
                val agentPrefix = if (!line.agent.isNullOrEmpty()) "{agent:${line.agent}}" else ""
                
                appendLine(String.format("[%02d:%02d.%02d]%s%s", minutes, seconds, centiseconds, agentPrefix, line.text))
                
                if (line.words.isNotEmpty()) {
                    val wordsData = line.words.joinToString("|") { word ->
                        "${word.text}:${word.startTime}:${word.endTime}"
                    }
                    appendLine("<$wordsData>")
                }
                
                // Add background vocals as separate lines
                line.backgroundLines.forEach { bgLine ->
                    val bgTimeMs = (bgLine.startTime * 1000).toLong()
                    val bgMinutes = bgTimeMs / 60000
                    val bgSeconds = (bgTimeMs % 60000) / 1000
                    val bgCentiseconds = (bgTimeMs % 1000) / 10
                    
                    appendLine(String.format("[%02d:%02d.%02d]{bg}%s", bgMinutes, bgSeconds, bgCentiseconds, bgLine.text))
                    
                    if (bgLine.words.isNotEmpty()) {
                        val bgWordsData = bgLine.words.joinToString("|") { word ->
                            "${word.text}:${word.startTime}:${word.endTime}"
                        }
                        appendLine("<$bgWordsData>")
                    }
                }
            }
        }
    }
    
    private fun parseTime(timeStr: String): Double {
        return try {
            when {
                timeStr.contains(":") -> {
                    val parts = timeStr.split(":")
                    when (parts.size) {
                        2 -> {
                            val minutes = parts[0].toDouble()
                            val seconds = parts[1].toDouble()
                            minutes * 60 + seconds
                        }
                        3 -> {
                            val hours = parts[0].toDouble()
                            val minutes = parts[1].toDouble()
                            val seconds = parts[2].toDouble()
                            hours * 3600 + minutes * 60 + seconds
                        }
                        else -> timeStr.toDoubleOrNull() ?: 0.0
                    }
                }
                else -> timeStr.toDoubleOrNull() ?: 0.0
            }
        } catch (e: Exception) {
            0.0
        }
    }
}
