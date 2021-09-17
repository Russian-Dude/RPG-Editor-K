package com.rdude.rpgeditork.wrapper

import com.rdude.rpgeditork.settings.Settings
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import ru.rdude.rpg.game.logic.data.resources.Resource
import ru.rdude.rpg.game.utils.Functions
import tornadofx.onChange
import java.nio.file.Files
import java.nio.file.Path

data class ImageResourceWrapper(override val resource: Resource) : ResourceWrapper<Image>(
    resource = resource,
    file = Path.of(Settings.tempImagesFolder.toString(), resource.guid.toString() + ".png"),
    fxRepresentation = Image(
        Path.of(Settings.tempImagesFolder.toString(), resource.guid.toString() + ".png").toUri().toString()
    )
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

    fun copy(): ImageResourceWrapper {
        val resourceCopy = Resource("${resource.name} (copy)", Functions.generateGuid())
        Files.copy(file, Path.of(Settings.tempImagesFolder.toString(), resourceCopy.guid.toString() + ".png"))
        return ImageResourceWrapper(resourceCopy)
    }

}