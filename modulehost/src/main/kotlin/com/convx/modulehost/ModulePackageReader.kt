package com.convx.modulehost

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream

object ModulePackageReader {
    val packageFiles: Set<String> = setOf(
        "manifest.json",
        "module/module.json",
        "assets/icon.svg",
    )

    private val safePath = Regex("[A-Za-z0-9._-]+(?:/[A-Za-z0-9._-]+)*")

    fun read(bytes: ByteArray): Map<String, ByteArray> {
        validationRequire(bytes.isNotEmpty(), "module package is empty")

        val centralDirectoryFiles = readCentralDirectoryFiles(bytes)
        val contents = linkedMapOf<String, ByteArray>()
        try {
            ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
                while (true) {
                    val entry = zip.nextEntry ?: break
                    val name = entry.name
                    requireSafePath(name)
                    validationRequire(!entry.isDirectory, "module package contains a directory entry: $name")
                    validationRequire(centralDirectoryFiles.remove(name), "module package entry is missing from its central directory: $name")
                    validationRequire(name in packageFiles, "module package contains an undeclared path: $name")
                    validationRequire(name !in contents, "module package contains a duplicate path: $name")
                    contents[name] = readEntry(zip)
                }
            }
        } catch (error: ModuleValidationException) {
            throw error
        } catch (error: Exception) {
            throw ModuleValidationException("invalid .smod archive", error)
        }

        validationRequire(centralDirectoryFiles.isEmpty(), "module package central directory does not match its entries")
        validationRequire(contents.keys == packageFiles) {
            "module package must contain exactly ${packageFiles.sorted()}"
        }
        return contents.toMap()
    }

    private fun readCentralDirectoryFiles(bytes: ByteArray): MutableSet<String> {
        val endOfCentralDirectory = findEndOfCentralDirectory(bytes)
        validationRequire(endOfCentralDirectory >= 0, "module package has no ZIP end record")

        val commentLength = readUnsignedShort(bytes, endOfCentralDirectory + 20)
        validationRequire(endOfCentralDirectory.toLong() + END_OF_CENTRAL_DIRECTORY_SIZE + commentLength == bytes.size.toLong()) {
            "module package has trailing data after the ZIP end record"
        }

        val diskNumber = readUnsignedShort(bytes, endOfCentralDirectory + 4)
        val directoryDisk = readUnsignedShort(bytes, endOfCentralDirectory + 6)
        val entriesOnDisk = readUnsignedShort(bytes, endOfCentralDirectory + 8)
        val totalEntries = readUnsignedShort(bytes, endOfCentralDirectory + 10)
        val directorySize = readUnsignedInt(bytes, endOfCentralDirectory + 12)
        val directoryOffset = readUnsignedInt(bytes, endOfCentralDirectory + 16)
        validationRequire(
            diskNumber == 0 && directoryDisk == 0 && entriesOnDisk == totalEntries,
            "module package uses multiple ZIP disks",
        )
        validationRequire(totalEntries != UINT16_MAX && directorySize != UINT32_MAX && directoryOffset != UINT32_MAX) {
            "module package ZIP64 archives are not supported"
        }

        val directoryEnd = directoryOffset + directorySize
        validationRequire(directoryOffset <= Int.MAX_VALUE && directoryEnd <= bytes.size.toLong()) {
            "module package central directory is outside the archive"
        }
        validationRequire(directoryEnd == endOfCentralDirectory.toLong()) {
            "module package central directory is not adjacent to the ZIP end record"
        }

        val files = linkedSetOf<String>()
        var cursor = directoryOffset
        repeat(totalEntries) {
            validationRequire(cursor + CENTRAL_DIRECTORY_HEADER_SIZE <= directoryEnd) {
                "module package central directory is truncated"
            }
            val headerOffset = cursor.toInt()
            validationRequire(readUnsignedInt(bytes, headerOffset) == CENTRAL_DIRECTORY_SIGNATURE) {
                "module package central directory is invalid"
            }
            val nameLength = readUnsignedShort(bytes, headerOffset + 28)
            val extraLength = readUnsignedShort(bytes, headerOffset + 30)
            val commentLength = readUnsignedShort(bytes, headerOffset + 32)
            val nameStart = cursor + CENTRAL_DIRECTORY_HEADER_SIZE
            val entryEnd = nameStart + nameLength + extraLength + commentLength
            validationRequire(entryEnd <= directoryEnd) {
                "module package central directory entry is truncated"
            }
            val name = bytes.copyOfRange(nameStart.toInt(), (nameStart + nameLength).toInt())
                .toString(Charsets.UTF_8)
            requireSafePath(name)
            validationRequire(name in packageFiles, "module package contains an undeclared path: $name")
            validationRequire(files.add(name), "module package contains a duplicate path: $name")

            val externalAttributes = readUnsignedInt(bytes, headerOffset + 38)
            val unixMode = ((externalAttributes ushr 16) and UINT16_MAX.toLong()).toInt()
            val unixType = unixMode and UNIX_FILE_TYPE_MASK
            validationRequire(externalAttributes and DOS_DIRECTORY_ATTRIBUTE.toLong() == 0L) {
                "module package contains DOS directory metadata: $name"
            }
            validationRequire(unixType == 0 || unixType == UNIX_REGULAR_FILE) {
                "module package contains a non-regular entry: $name"
            }
            cursor = entryEnd
        }
        validationRequire(cursor == directoryEnd) {
            "module package central directory has unexpected data"
        }
        return files
    }

    private fun requireSafePath(name: String) {
        validationRequire(name.isNotEmpty() && safePath.matches(name)) {
            "unsafe module package path: ${name.toDebugString()}"
        }
        validationRequire(name.split('/').none { it == "." || it == ".." }) {
            "module package path escapes its root: ${name.toDebugString()}"
        }
    }

    private fun readEntry(zip: ZipInputStream): ByteArray {
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (true) {
            val count = zip.read(buffer)
            if (count < 0) break
            output.write(buffer, 0, count)
        }
        return output.toByteArray()
    }

    private fun findEndOfCentralDirectory(bytes: ByteArray): Int {
        if (bytes.size < END_OF_CENTRAL_DIRECTORY_SIZE) return -1
        val firstCandidate = maxOf(0, bytes.size - (END_OF_CENTRAL_DIRECTORY_SIZE + MAX_ZIP_COMMENT_SIZE))
        for (offset in bytes.size - END_OF_CENTRAL_DIRECTORY_SIZE downTo firstCandidate) {
            if (readUnsignedInt(bytes, offset) == END_OF_CENTRAL_DIRECTORY_SIGNATURE) return offset
        }
        return -1
    }

    private fun readUnsignedShort(bytes: ByteArray, offset: Int): Int {
        validationRequire(offset >= 0 && offset + 2 <= bytes.size, "module package ZIP header is truncated")
        return (bytes[offset].toInt() and 0xff) or
            ((bytes[offset + 1].toInt() and 0xff) shl 8)
    }

    private fun readUnsignedInt(bytes: ByteArray, offset: Int): Long {
        validationRequire(offset >= 0 && offset + 4 <= bytes.size, "module package ZIP header is truncated")
        var value = 0L
        repeat(4) { index ->
            value = value or ((bytes[offset + index].toLong() and 0xff) shl (index * 8))
        }
        return value
    }

    private fun validationRequire(condition: Boolean, message: () -> String) {
        if (!condition) throw ModuleValidationException(message())
    }

    private fun validationRequire(condition: Boolean, message: String) {
        if (!condition) throw ModuleValidationException(message)
    }

    private fun String.toDebugString(): String = replace("\u0000", "\\0")

    private const val CENTRAL_DIRECTORY_HEADER_SIZE = 46L
    private const val END_OF_CENTRAL_DIRECTORY_SIZE = 22
    private const val MAX_ZIP_COMMENT_SIZE = 0xffff
    private const val CENTRAL_DIRECTORY_SIGNATURE = 0x02014b50L
    private const val END_OF_CENTRAL_DIRECTORY_SIGNATURE = 0x06054b50L
    private const val DOS_DIRECTORY_ATTRIBUTE = 0x10
    private const val UNIX_FILE_TYPE_MASK = 0xf000
    private const val UNIX_REGULAR_FILE = 0x8000
    private const val UINT16_MAX = 0xffff
    private const val UINT32_MAX = 0xffff_ffffL
}
