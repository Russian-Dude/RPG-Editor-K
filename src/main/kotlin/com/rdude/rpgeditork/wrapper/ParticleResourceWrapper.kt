package com.rdude.rpgeditork.wrapper

import com.rdude.rpgeditork.settings.Settings
import javafx.beans.property.SimpleStringProperty
import ru.rdude.rpg.game.logic.data.resources.Resource
import ru.rdude.rpg.game.utils.Functions
import tornadofx.onChange
import java.nio.file.Files
import java.nio.file.Path

class ParticleResourceWrapper(override val resource: Resource) : ResourceWrapper<String>(
    resource = resource,
    file = Path.of(Settings.tempParticlesFolder.toString(), resource.guid.toString() + ".p"),
    fxRepresentation = resource.name
) {

    val guid: Long
        get() = resource.guid

    override val nameProperty = SimpleStringProperty(resource.name).apply {
        onChange { resource.name = it }
    }
    var name: String
        set(value) {
            nameProperty.set(value)
        }
        get() = resource.name

    fun copy(): ParticleResourceWrapper {
        val resourceCopy = Resource("${resource.name} (copy)", Functions.generateGuid())
        Files.copy(file, Path.of(Settings.tempParticlesFolder.toString(), resourceCopy.guid.toString() + ".p"))
        return ParticleResourceWrapper(resourceCopy)
    }

}