package com.rdude.rpgeditork.wrapper

import ru.rdude.rpg.game.logic.data.resources.Resource
import java.nio.file.Path

abstract class ResourceWrapper<T>(
    open val resource: Resource,
    val fxRepresentation: T,
    val file: Path
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ResourceWrapper<*>) return false

        if (resource.guid != other.resource.guid) return false

        return true
    }

    override fun hashCode(): Int {
        return resource.hashCode()
    }
}
