package com.rdude.rpgeditork.wrapper

import com.rdude.rpgeditork.settings.Settings
import javafx.beans.property.SimpleStringProperty
import javafx.scene.media.Media
import ru.rdude.rpg.game.logic.data.resources.Resource
import ru.rdude.rpg.game.utils.Functions
import tornadofx.onChange
import java.nio.file.Files
import java.nio.file.Path

class SoundResourceWrapper(override val resource: Resource) : ResourceWrapper<Media>(
    resource = resource,
    fxRepresentation = Media(
        Path.of(Settings.tempSoundsFolder.toString(), resource.guid.toString() + ".mp3").toUri().toString()
    ),
    file = Path.of(Settings.tempSoundsFolder.toString(), resource.guid.toString() + ".mp3")
) {

    val guid: Long
        get() = resource.guid

    val nameProperty = SimpleStringProperty(resource.name).apply {
        onChange { resource.name = it }
    }
    var name: String
        set(value) {
            nameProperty.set(value)
        }
        get() = resource.name

    fun copy(): SoundResourceWrapper {
        val resourceCopy = Resource("${resource.name} (copy)", Functions.generateGuid())
        Files.copy(file, Path.of(Settings.tempSoundsFolder.toString(), resourceCopy.guid.toString() + ".mp3"))
        return SoundResourceWrapper(resourceCopy)
    }



}