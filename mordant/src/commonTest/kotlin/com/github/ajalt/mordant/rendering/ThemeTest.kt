package com.github.ajalt.mordant.rendering

import com.github.ajalt.mordant.internal.DEFAULT_STYLE
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.widgets.Spinner
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import kotlin.js.JsName
import kotlin.test.Test

private fun BorderTypeSection.chars(): List<String> =
    listOf(es, esw, sw, nes, nesw, nsw, ne, new, nw, ew, ns, s, n, w, e)

private fun BorderType.chars(): List<String> =
    listOf(head, headBottom, body, bodyBottom, foot).flatMap { it.chars() }

class ThemeTest {
    private val theme = Theme(Theme.PlainAscii) {
        styles["s"] = TextColors.blue
    }

    @Test
    fun style() {
        theme.style("s") shouldBe TextColors.blue
        theme.style("foo") shouldBe DEFAULT_STYLE
        theme.style("foo", TextColors.blue) shouldBe TextColors.blue
    }

    @Test
    fun styleOrNull() {
        theme.styleOrNull("s") shouldBe TextColors.blue
        theme.styleOrNull("foo") shouldBe null
    }

    @Test
    fun flag() {
        theme.flag("markdown.code.block.border") shouldBe true
        theme.flag("foo") shouldBe false
        theme.flag("foo", true) shouldBe true
    }

    @Test
    fun flagOrNull() {
        theme.flagOrNull("markdown.code.block.border") shouldBe true
        theme.flagOrNull("foo") shouldBe null
    }

    @Test
    fun string() {
        theme.string("list.number.separator") shouldBe "."
        theme.string("foo") shouldBe ""
        theme.string("foo", "bar") shouldBe "bar"
    }

    @Test
    fun stringOrNull() {
        theme.stringOrNull("list.number.separator") shouldBe "."
        theme.stringOrNull("foo") shouldBe null
    }

    @Test
    fun dimension() {
        theme.dimension("hr.title.padding") shouldBe 1
        theme.dimension("foo") shouldBe 0
        theme.dimension("foo", -1) shouldBe -1
    }

    @Test
    fun dimensionOrNull() {
        theme.dimensionOrNull("hr.title.padding") shouldBe 1
        theme.dimensionOrNull("foo") shouldBe null
    }

    @Test
    fun plus() {
        val l = Theme {
            dimensions["foo"] = 11
            dimensions["bar"] = 22
        }
        val r = Theme {
            dimensions["bar"] = 23
            dimensions["baz"] = 33
        }
        val t = l + r
        t.dimension("foo") shouldBe 11
        t.dimension("bar") shouldBe 23
        t.dimension("baz") shouldBe 33
    }

    @[Test JsName("ascii_alternatives_contain_only_ascii")]
    fun `ascii alternatives contain only ascii`() {
        // These are what a caller falls back to when the default can't be encoded
        val cases = mapOf(
            "Theme.PlainAscii" to Theme.PlainAscii.strings.values.toList(),
            "Spinner.Lines" to Spinner.Lines().let { spinner ->
                // Rendered rather than copied, so the assertion covers the shipped frames
                List(4) { Terminal(ansiLevel = AnsiLevel.NONE).render(spinner).also { spinner.advanceTick() } }
            },
            "BorderType.ASCII" to BorderType.ASCII.chars(),
            "BorderType.ASCII_DOUBLE_SECTION_SEPARATOR" to
                    BorderType.ASCII_DOUBLE_SECTION_SEPARATOR.chars(),
            "BorderType.BLANK" to BorderType.BLANK.chars(),
        )
        for ((name, strings) in cases) {
            val nonAscii = strings.filter { s -> s.any { it.code >= 0x80 } }
            withClue("$name contains non-ascii characters: $nonAscii") {
                nonAscii.isEmpty() shouldBe true
            }
        }
    }
}
