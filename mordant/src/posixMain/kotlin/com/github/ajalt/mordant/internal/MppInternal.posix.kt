package com.github.ajalt.mordant.internal

import kotlinx.cinterop.toKString
import platform.posix.getenv

/**
 * Whether the locale says the terminal reads utf-8.
 *
 * Output is written as utf-8 bytes whatever the locale is, so this is really a question about the
 * terminal on the other end, which there's no way to ask. The locale is the same answer the JVM
 * gives, and the variables are read in the order POSIX defines.
 */
private val localeIsUtf8: Boolean by lazy {
    val locale = listOf("LC_ALL", "LC_CTYPE", "LANG")
        .firstNotNullOfOrNull { getenv(it)?.toKString()?.takeIf { l -> l.isNotEmpty() } }
        ?: return@lazy false
    // A locale looks like `cs_CZ.UTF-8@euro`, and `C` or `POSIX` means ascii
    val codeset = locale.substringAfter('.', "").substringBefore('@')
    codeset.replace("-", "").equals("utf8", ignoreCase = true)
}

internal actual fun outputCanEncode(text: String): Boolean {
    return localeIsUtf8 || text.all { it.code < 0x80 }
}
