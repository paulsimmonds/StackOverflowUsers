package com.magictorch.stackoverflowtest.domain.util

interface StringDecoder {
    fun decodeHtml(html: String): String
}
