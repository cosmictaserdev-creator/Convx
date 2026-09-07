/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.extensions

import androidx.sqlite.db.SimpleSQLiteQuery
import java.net.InetSocketAddress
import java.net.InetSocketAddress.createUnresolved

inline fun <reified T : Enum<T>> String?.toEnum(defaultValue: T): T =
    if (this == null) {
        defaultValue
    } else {
        try {
            enumValueOf(this)
        } catch (e: IllegalArgumentException) {
            defaultValue
        }
    }

fun String.toSQLiteQuery(): SimpleSQLiteQuery = SimpleSQLiteQuery(this)

fun String.toInetSocketAddress(): InetSocketAddress {
    // Bracketed form ("[2401:4900::1]:1080") for an IPv6 literal host — an IPv6
    // address's own colons make plain split(":") ambiguous with the port
    // separator, so RFC 3986 disambiguates with brackets. IPv4/hostname never
    // start with one, so that's the branch signal.
    if (startsWith("[")) {
        val closeBracket = indexOf(']')
        val host = substring(1, closeBracket)
        val port = substring(closeBracket + 2) // skip "]:"
        return createUnresolved(host, port.toInt())
    }
    val (host, port) = split(":")
    return createUnresolved(host, port.toInt())
}
