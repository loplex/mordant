package com.github.ajalt.mordant.rendering

import com.github.ajalt.mordant.internal.DEFAULT_STYLE
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.TerminalRecorder
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
                // Rendered rather than copied, so this covers the shipped frames
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

    @[Test JsName("theme_is_chosen_from_what_the_output_can_encode")]
    fun `theme is chosen from what the output can encode`() {
        // An output that can encode everything keeps the unicode theme unchanged
        Terminal(terminalInterface = TerminalRecorder()).theme shouldBe Theme.Default

        // An ascii-only output falls back, but keeps Default's styles: color is unrelated
        val ascii = Terminal(terminalInterface = recorder { s -> s.all { it.code < 0x80 } })
        ascii.theme.strings shouldBe Theme.Ascii.strings
        ascii.theme.flag("borders.ascii") shouldBe true
        ascii.theme.style("danger") shouldBe Theme.Default.style("danger")

        // cp932 encodes the bar but no braille or bullets, so only some widgets downgrade
        val cp932 = Terminal(
            terminalInterface = recorder { "\u280b" !in it && "\u2022" !in it }
        )
        cp932.theme.string("progressbar.complete") shouldBe Theme.Default.string("progressbar.complete")
        cp932.theme.string("progressbar.separator") shouldBe Theme.Default.string("progressbar.separator")
        cp932.theme.string("list.bullet.text") shouldBe Theme.Ascii.string("list.bullet.text")
        cp932.theme.string("select.cursor") shouldBe Theme.Ascii.string("select.cursor")
        cp932.canEncode("\u2501") shouldBe true
        cp932.canEncode("\u280b") shouldBe false

        // An explicit theme is always used as given
        Terminal(
            theme = Theme.Default,
            terminalInterface = recorder { false },
        ).theme shouldBe Theme.Default
    }

    @[Test JsName("every_ascii_alternative_belongs_to_one_fallback_group")]
    fun `every ascii alternative belongs to one fallback group`() {
        val grouped = ASCII_FALLBACK_GROUPS.flatten()
        withClue("a string is in two groups: $grouped") {
            grouped.toSet().size shouldBe grouped.size
        }

        // Pins which strings are swapped together, so the fallback can't silently change shape
        val alternatives = Theme.Default.strings
            .filterKeys { Theme.Ascii.strings[it] != Theme.Default.strings[it] }
            .keys
        withClue("every string Theme.Ascii changes needs a group to take part in the fallback") {
            grouped.toSet() shouldBe alternatives
        }
    }

    private fun recorder(canEncodeOutput: (String) -> Boolean): TerminalRecorder {
        return TerminalRecorder().also { it.outputEncodable = canEncodeOutput }
    }
}
