package com.github.ajalt.mordant.internal

import com.github.ajalt.mordant.terminal.TerminalInterface
import com.github.ajalt.mordant.terminal.terminalinterface.TerminalInterfaceNativeWindows
import platform.windows.*

internal actual fun ttySetEcho(echo: Boolean) = TerminalInterfaceNativeWindows.ttySetEcho(echo)
internal actual fun testsHaveFileSystem(): Boolean = true
internal actual fun getStandardTerminalInterface(): TerminalInterface =
    TerminalInterfaceNativeWindows

/**
 * A console decodes whatever it's sent with its code page, and output is written as utf-8 bytes
 * whatever that code page is, so anything but ascii comes out mangled unless the console reads
 * utf-8 too.
 *
 * The code page having a character of its own doesn't help: cp437 has `─`, but nothing transcodes
 * to it on the way out.
 */
internal actual fun outputCanEncode(text: String): Boolean {
    // 0 means there's no console, and the bytes reach a file or a pipe as utf-8
    val codePage = GetConsoleOutputCP().takeIf { it != 0u } ?: return true
    return codePageCanEncode(codePage, text)
}

/** Whether [text] survives being written as utf-8 to a console reading [codePage]. */
internal fun codePageCanEncode(codePage: UInt, text: String): Boolean {
    return codePage == CP_UTF8.toUInt() || text.all { it.code < 0x80 }
}
