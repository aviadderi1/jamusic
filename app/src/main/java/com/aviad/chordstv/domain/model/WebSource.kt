package com.aviad.chordstv.domain.model

import java.net.URLEncoder

/**
 * External chord/lyrics sites the built-in browser can search.
 * The app only opens the site's own search page – exactly like a browser would.
 */
enum class WebSource(val label: String, val homeUrl: String) {
    TAB4U("Tab4U", "https://www.tab4u.com/") {
        override fun searchUrl(q: String) = "https://www.tab4u.com/resultsSimple?tab=songs&q=${enc(q)}"
    },
    TAB4U_EN("Tab4U EN", "https://en.tab4u.com/") {
        override fun searchUrl(q: String) = "https://en.tab4u.com/resultsSimple?tab=songs&q=${enc(q)}"
    },
    ULTIMATE_GUITAR("Ultimate Guitar", "https://www.ultimate-guitar.com/") {
        override fun searchUrl(q: String) =
            "https://www.ultimate-guitar.com/search.php?search_type=title&value=${enc(q)}"
    },
    GOOGLE("Google", "https://www.google.com/") {
        override fun searchUrl(q: String) = "https://www.google.com/search?q=${enc("$q אקורדים chords")}"
    };

    abstract fun searchUrl(q: String): String

    companion object {
        fun enc(s: String): String = URLEncoder.encode(s.trim(), "UTF-8")
        fun containsHebrew(s: String) = s.any { it in '\u0590'..'\u05FF' }

        /** Best default source for a query: Hebrew → Tab4U, otherwise Ultimate Guitar. */
        fun suggestFor(query: String): WebSource = if (containsHebrew(query)) TAB4U else ULTIMATE_GUITAR
    }
}
