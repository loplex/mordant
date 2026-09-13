package com.github.ajalt.mordant.rendering

import com.github.ajalt.mordant.internal.DEFAULT_STYLE


/**
 * Characters to use for one section of a [BorderType]
 */
class BorderTypeSection(
    val es: String,
    val esw: String,
    val sw: String,
    val nes: String,
    val nesw: String,
    val nsw: String,
    val ne: String,
    val new: String,
    val nw: String,
    val ew: String,
    val ns: String,
    val s: String,
    val n: String,
    val w: String,
    val e: String,
) {
    constructor(corners: String) : this(
        es = corners[0].toString(),
        esw = corners[1].toString(),
        sw = corners[2].toString(),
        nes = corners[3].toString(),
        nesw = corners[4].toString(),
        nsw = corners[5].toString(),
        ne = corners[6].toString(),
        new = corners[7].toString(),
        nw = corners[8].toString(),
        ew = corners[9].toString(),
        ns = corners[10].toString(),
        s = corners[11].toString(),
        n = corners[12].toString(),
        w = corners[13].toString(),
        e = corners[14].toString(),
    )

    private val array = arrayOf(" ", w, s, sw, e, ew, es, esw, n, nw, ns, nsw, ne, new, nes, nesw)

    fun getCorner(
        n: Boolean,
        e: Boolean,
        s: Boolean,
        w: Boolean,
        textStyle: TextStyle = DEFAULT_STYLE,
    ): Span {
        val i = (if (n) 8 else 0) or (if (e) 4 else 0) or (if (s) 2 else 0) or (if (w) 1 else 0)
        return Span.word(array[i], textStyle)
    }
}

/**
 * Characters to use to draw borders on a table or other box widget.
 */
class BorderType(
    val head: BorderTypeSection,
    val headBottom: BorderTypeSection,
    val body: BorderTypeSection,
    val bodyBottom: BorderTypeSection,
    val foot: BorderTypeSection,
) {
    companion object {
        fun build(
            head: String,
            headBottom: String = head,
            body: String = head,
            bodyBottom: String = body,
            foot: String = body,
        ): BorderType = BorderType(
            head = BorderTypeSection(head),
            headBottom = BorderTypeSection(headBottom),
            body = BorderTypeSection(body),
            bodyBottom = BorderTypeSection(bodyBottom),
            foot = BorderTypeSection(foot),
        )

        /**
         * ### Example
         *
         * ```
         *┌──┬──┐
         *│  │  │
         *├──┼──┤
         *│  │  │
         *├──┼──┤
         *│  │  │
         *├──┼──┤
         *│  │  │
         *└──┴──┘
         * ```
         */
        val SQUARE = build(
            "┌┬┐├┼┤└┴┘─│╷╵╴╶"
        )

        /**
         * ### Example
         *
         * ```
         *┌──┬──┐
         *│  │  │
         *╞══╪══╡
         *│  │  │
         *├──┼──┤
         *│  │  │
         *╞══╪══╡
         *│  │  │
         *└──┴──┘
         * ```
         */
        val SQUARE_DOUBLE_SECTION_SEPARATOR = build(
            "┌┬┐├┼┤└┴┘─│╷╵╴╶",
            "╒╤╕╞╪╡╘╧╛═│╷╵  ",
            "┌┬┐├┼┤└┴┘─│╷╵╴╶",
            "╒╤╕╞╪╡╘╧╛═│╷╵  ",
        )

        /**
         * ### Example
         *
         * ```
         *╭──┬──╮
         *│  │  │
         *├──┼──┤
         *│  │  │
         *├──┼──┤
         *│  │  │
         *├──┼──┤
         *│  │  │
         *╰──┴──╯
         * ```
         */
        val ROUNDED = build(
            "╭┬╮├┼┤╰┴╯─│╷╵╴╶",
        )

        /**
         * ### Example
         *
         * ```
         *┏━━┳━━┓
         *┃  ┃  ┃
         *┣━━╋━━┫
         *┃  ┃  ┃
         *┣━━╋━━┫
         *┃  ┃  ┃
         *┣━━╋━━┫
         *┃  ┃  ┃
         *┗━━┻━━┛
         * ```
         */
        val HEAVY = build(
            "┏┳┓┣╋┫┗┻┛━┃╻╹╸╺",
        )

        /**
         * ### Example
         *
         * ```
         *╔══╦══╗
         *║  ║  ║
         *╠══╬══╣
         *║  ║  ║
         *╠══╬══╣
         *║  ║  ║
         *╠══╬══╣
         *║  ║  ║
         *╚══╩══╝
         * ```
         */
        val DOUBLE = build(
            "╔╦╗╠╬╣╚╩╝═║    ",
        )

        /**
         * ### Example
         *
         * ```
         *┏━━┳━━┓
         *┃  ┃  ┃
         *┡━━╇━━┩
         *│  │  │
         *├──┼──┤
         *│  │  │
         *┢━━╈━━┪
         *┃  ┃  ┃
         *┗━━┻━━┛
         * ```
         */
        val HEAVY_HEAD_FOOT = build(
            "┏┳┓┣╋┫┗┻┛━┃╻╹╸╺",
            "┍┯┑┡╇┩┗┻┛━╿╷╹╸╺",
            "┌┬┐├┼┤└┴┘─│╷╵╴╶",
            "┏┳┓┢╈┪┕┷┙━╽╻╵╸╺",
            "┏┳┓┣╋┫┗┻┛━┃╻╹╸╺",
        )

        /**
         * ### Example
         *
         * ```
         *+--+--+
         *|  |  |
         *+--+--+
         *|  |  |
         *+--+--+
         *|  |  |
         *+--+--+
         *|  |  |
         *+--+--+
         * ```
         */
        val ASCII = build(
            "+++++++++-|    ",
        )

        /**
         * ### Example
         *
         * ```
         *+--+--+
         *|  |  |
         *+==+==+
         *|  |  |
         *+--+--+
         *|  |  |
         *+==+==+
         *|  |  |
         *+--+--+
         * ```
         */
        val ASCII_DOUBLE_SECTION_SEPARATOR = build(
            "+++++++++-|    ",
            "+++++++++=|    ",
            "+++++++++-|    ",
            "+++++++++=|    ",
        )

        /** Whitespace as borders */
        val BLANK = build(
            "               ",
        )
    }

    /**
     * The ascii border type that best matches this one, or `null` if this isn't one of the border
     * types defined here, since there's no basis for picking a stand-in for a custom one.
     */
    internal fun asciiEquivalentOrNull(): BorderType? = when (this) {
        SQUARE, ROUNDED, HEAVY, DOUBLE -> ASCII
        SQUARE_DOUBLE_SECTION_SEPARATOR, HEAVY_HEAD_FOOT -> ASCII_DOUBLE_SECTION_SEPARATOR
        ASCII, ASCII_DOUBLE_SECTION_SEPARATOR, BLANK -> this
        else -> null
    }
}

/** Every character this section draws with. */
internal fun BorderTypeSection.characters(): String =
    es + esw + sw + nes + nesw + nsw + ne + new + nw + ew + ns + s + n + w + e

/** Every character this border type draws with. */
internal fun BorderType.characters(): String =
    head.characters() + headBottom.characters() + body.characters() +
            bodyBottom.characters() + foot.characters()

/**
 * Return the ascii equivalent of this border type if [theme] asks for ascii borders and one is
 * known, or this border type unchanged otherwise.
 */
internal fun BorderType.forTheme(theme: Theme): BorderType {
    if (!theme.flag("borders.ascii")) return this
    return asciiEquivalentOrNull() ?: this
}
