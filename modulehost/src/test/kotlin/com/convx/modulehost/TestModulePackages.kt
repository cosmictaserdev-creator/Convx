package com.convx.modulehost

import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

internal object TestModulePackages {
    const val SCHEMA_BASE = "https://raw.githubusercontent.com/N7T0-OF/Spacemusic/main/schemas/"

    fun manifestJson(
        id: String = "spacemusic",
        version: String = "0.1.0",
        type: String = "declarative",
        permissions: String = "[\"settings\"]",
        compatibility: String = """{"minConvxVersion":"1.5.2","maxConvxVersion":null}""",
        schema: String = "${SCHEMA_BASE}manifest.schema.json",
        extra: String = "",
    ): String = """
        {
          "${'$'}schema":"$schema",
          "schemaVersion":"0.1",
          "id":"$id",
          "name":"SpaceMusic",
          "version":"$version",
          "type":"$type",
          "description":"A declarative music module for Convx.",
          "author":"N7T0-OF",
          "compatibility":$compatibility,
          "permissions":$permissions,
          "content":{"module":"module/module.json","icon":"assets/icon.svg"},
          "updates":{"releaseIndex":"https://raw.githubusercontent.com/N7T0-OF/Spacemusic/main/releases/index.json"}$extra
        }
    """.trimIndent()

    fun moduleJson(
        moduleId: String = "spacemusic",
        schema: String = "${SCHEMA_BASE}module.schema.json",
        action: String = "check-updates",
    ): String = """
        {
          "${'$'}schema":"$schema",
          "schemaVersion":"0.1",
          "moduleId":"$moduleId",
          "contributions":{
            "settings":{
              "section":"SpaceMusic",
              "icon":"assets/icon.svg",
              "actions":[{"id":"$action","label":"Check for updates","action":"$action"}]
            }
          }
        }
    """.trimIndent()

    fun packageEntries(
        manifest: ByteArray = manifestJson().toByteArray(),
        module: ByteArray = moduleJson().toByteArray(),
    ): Map<String, ByteArray> = linkedMapOf(
        "manifest.json" to manifest,
        "module/module.json" to module,
        "assets/icon.svg" to "<svg xmlns=\"http://www.w3.org/2000/svg\"/>".toByteArray(),
    )

    fun packageBytes(
        manifest: String = manifestJson(),
        module: String = moduleJson(),
    ): ByteArray = zip(
        packageEntries(manifest.toByteArray(), module.toByteArray()),
    )

    fun releaseJson(
        moduleId: String = "spacemusic",
        version: String = "0.1.0",
        status: String = "published",
        compatibility: String = """{"minConvxVersion":"1.5.2","maxConvxVersion":null}""",
        artifact: String? = "https://example.com/releases/$version.smod",
        sha256: String? = null,
    ): String = """
        {
          "${'$'}schema":"${SCHEMA_BASE}release.schema.json",
          "schemaVersion":"0.1",
          "moduleId":"$moduleId",
          "version":"$version",
          "status":"$status",
          "compatibility":$compatibility,
          "artifact":${artifact?.let { "\"$it\"" } ?: "null"},
          "sha256":${sha256?.let { "\"$it\"" } ?: "null"},
          "notes":"test release"
        }
    """.trimIndent()

    fun releaseIndex(
        moduleId: String = "spacemusic",
        releases: List<String>,
    ): String = """
        {
          "${'$'}schema":"${SCHEMA_BASE}release-index.schema.json",
          "schemaVersion":"0.1",
          "moduleId":"$moduleId",
          "releases":[${releases.joinToString(",")}]
        }
    """.trimIndent()

    fun zip(entries: Map<String, ByteArray>): ByteArray =
        zipEntries(entries.entries.map { it.key to it.value })

    fun zipEntries(entries: Iterable<Pair<String, ByteArray>>): ByteArray {
        val output = ByteArrayOutputStream()
        ZipOutputStream(output).use { zip ->
            entries.forEach { (name, payload) ->
                zip.putNextEntry(ZipEntry(name))
                zip.write(payload)
                zip.closeEntry()
            }
        }
        return output.toByteArray()
    }
}