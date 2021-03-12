package com.rdude.rpgeditork.enums

import ru.rdude.rpg.game.logic.enums.Size

fun Size.nullableVersion(): NullableSize {
    return when(this) {
        Size.SMALL -> NullableSize.SMALL
        Size.MEDIUM -> NullableSize.MEDIUM
        Size.BIG -> NullableSize.BIG
    }
}

enum class NullableSize(val size: Size?) {
    NO(null),
    SMALL(Size.SMALL),
    MEDIUM(Size.MEDIUM),
    BIG(Size.BIG)
}