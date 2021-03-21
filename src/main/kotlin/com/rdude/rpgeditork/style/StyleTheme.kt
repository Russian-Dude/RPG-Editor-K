package com.rdude.rpgeditork.style

import tornadofx.Stylesheet
import kotlin.reflect.KClass

enum class StyleTheme(val themeName: String, val clazz: KClass<out Stylesheet>) {

    LIGHT("Light", LightTheme::class),
    DARK("Dark", DarkTheme::class)

}