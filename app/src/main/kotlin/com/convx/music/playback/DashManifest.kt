package com.convx.music.playback

import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.IOException
import java.io.StringReader
import java.net.URI
import javax.xml.parsers.DocumentBuilderFactory

/** The init segment and ordered media segments of a single-representation audio DASH manifest. */
internal class DashSegments(val init: String?, val segments: List<String>)

internal object DashManifest {
    fun looksLikeMpd(body: String) = body.trimStart().let { it.startsWith("<") && "<MPD" in it }

    fun parse(manifestUrl: String, xml: String): DashSegments {
        val document = DocumentBuilderFactory.newInstance().apply {
            runCatching { setFeature("http://apache.org/xml/features/disallow-doctype-decl", true) }
        }.newDocumentBuilder().parse(InputSource(StringReader(xml)))
        val root = document.documentElement
        if (root.getElementsByTagName("ContentProtection").length > 0) throw IOException("Encrypted DASH")

        val representation = root.descendants("Representation").filter { rep ->
            (rep.parentNode as? Element)?.getAttribute("contentType").let { it.isNullOrEmpty() || it == "audio" }
        }.maxByOrNull { it.getAttribute("bandwidth").toLongOrNull() ?: 0L }
            ?: throw IOException("DASH manifest has no audio representation")
        val template = representation.child("SegmentTemplate")
            ?: (representation.parentNode as? Element)?.child("SegmentTemplate")
            ?: throw IOException("Unsupported DASH segment addressing")

        val mediaPattern = template.getAttribute("media").takeIf { it.isNotEmpty() }
            ?: throw IOException("DASH template has no media pattern")
        val startNumber = template.getAttribute("startNumber").toLongOrNull() ?: 1L
        val timescale = template.getAttribute("timescale").toLongOrNull() ?: 1L
        val id = representation.getAttribute("id")
        val bandwidth = representation.getAttribute("bandwidth")

        fun fill(pattern: String, number: Long, time: Long): String {
            val withNumbers = Regex("""\$(Number|Time)(?:%0(\d+)d)?\$""").replace(pattern) { match ->
                val value = if (match.groupValues[1] == "Number") number else time
                val width = match.groupValues[2].toIntOrNull()
                if (width != null) value.toString().padStart(width, '0') else value.toString()
            }
            return withNumbers.replace("\$RepresentationID\$", id).replace("\$Bandwidth\$", bandwidth)
        }

        fun absolute(url: String) = URI(manifestUrl).resolve(url).toString()

        val init = template.getAttribute("initialization").takeIf { it.isNotEmpty() }
            ?.let { absolute(fill(it, 0, 0)) }

        val segments = ArrayList<String>()
        val timeline = template.child("SegmentTimeline")
        if (timeline != null) {
            var number = startNumber
            var time = 0L
            for (entry in timeline.childNodes.elements("S")) {
                entry.getAttribute("t").toLongOrNull()?.let { time = it }
                val duration = entry.getAttribute("d").toLongOrNull() ?: throw IOException("DASH segment has no duration")
                val repeat = entry.getAttribute("r").toIntOrNull() ?: 0
                repeat(repeat + 1) {
                    segments += absolute(fill(mediaPattern, number++, time))
                    time += duration
                }
            }
        } else {
            val duration = template.getAttribute("duration").toLongOrNull() ?: throw IOException("DASH template has no duration")
            val total = root.getAttribute("mediaPresentationDuration").takeIf { it.isNotEmpty() }
                ?.let { runCatching { java.time.Duration.parse(it).toMillis() }.getOrNull() }
                ?: throw IOException("DASH manifest has no total duration")
            val count = Math.ceil(total / 1000.0 * timescale / duration).toLong()
            for (i in 0 until count) segments += absolute(fill(mediaPattern, startNumber + i, i * duration))
        }
        if (segments.isEmpty()) throw IOException("DASH manifest lists no segments")
        return DashSegments(init, segments)
    }

    private fun Element.descendants(name: String): List<Element> =
        getElementsByTagName(name).let { nodes -> (0 until nodes.length).map { nodes.item(it) as Element } }

    private fun Element.child(name: String): Element? = childNodes.elements(name).firstOrNull()

    private fun org.w3c.dom.NodeList.elements(name: String): List<Element> =
        (0 until length).mapNotNull { item(it) as? Element }.filter { it.tagName == name }
}
