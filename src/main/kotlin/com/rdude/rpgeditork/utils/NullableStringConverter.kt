package com.rdude.rpgeditork.utils

import javafx.util.StringConverter

class NullableStringConverter(private val nullAsString: String) : StringConverter<Any>() {

    private val map: MutableMap<String, Any?> = HashMap()

    override fun toString(value: Any?): String {
        val s = value?.toString() ?: nullAsString
        map[s] = value
        return s
    }

    override fun fromString(string: String?): Any? = map[string]
}