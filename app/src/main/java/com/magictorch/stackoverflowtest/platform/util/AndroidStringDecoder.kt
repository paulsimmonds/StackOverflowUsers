package com.magictorch.stackoverflowtest.platform.util

import androidx.core.text.HtmlCompat
import com.magictorch.stackoverflowtest.domain.util.StringDecoder

class AndroidStringDecoder : StringDecoder {
    override fun decodeHtml(html: String): String {
        return HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }
}
