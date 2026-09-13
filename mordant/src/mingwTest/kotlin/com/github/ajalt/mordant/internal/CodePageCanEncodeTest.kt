package com.github.ajalt.mordant.internal

import com.github.ajalt.mordant.rendering.BorderType
import com.github.ajalt.mordant.rendering.Theme
import com.github.ajalt.mordant.rendering.characters
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CodePageCanEncodeTest {
    // The default console code page on a US, a Japanese, and a central european Windows
    private val oemUs = 437u
    private val shiftJis = 932u
    private val oemLatin2 = 852u
    private val utf8 = 65001u

    @Test
    fun `only a console reading utf-8 is sent characters that aren't ascii`() {
        // cp437 has `─` and cp932 has `━`, which doesn't help: the bytes are utf-8 either way
        codePageCanEncode(oemUs, "─") shouldBe false
        codePageCanEncode(shiftJis, "━") shouldBe false
        codePageCanEncode(oemLatin2, "─") shouldBe false
        codePageCanEncode(utf8, "⠋") shouldBe true
    }

    @Test
    fun `every code page takes what the fallback falls back to`() {
        val fallbacks = Theme.Ascii.strings.values +
                BorderType.ASCII.characters() +
                BorderType.ASCII_DOUBLE_SECTION_SEPARATOR.characters()
        for (codePage in listOf(oemUs, shiftJis, oemLatin2, utf8)) {
            for (text in fallbacks) {
                withClue("code page $codePage can't take $text") {
                    codePageCanEncode(codePage, text) shouldBe true
                }
            }
        }
    }
}
