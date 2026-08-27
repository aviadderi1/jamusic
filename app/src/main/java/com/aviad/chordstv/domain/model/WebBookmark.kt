package com.aviad.chordstv.domain.model

import org.json.JSONObject

/** A saved web page (song page on an external site). */
data class WebBookmark(val url: String, val title: String) {

    fun encode(): String = JSONObject().put("url", url).put("title", title).toString()

    companion object {
        fun decode(raw: String): WebBookmark? = runCatching {
            val o = JSONObject(raw)
            WebBookmark(o.getString("url"), o.optString("title", o.getString("url")))
        }.getOrNull()
    }
}
