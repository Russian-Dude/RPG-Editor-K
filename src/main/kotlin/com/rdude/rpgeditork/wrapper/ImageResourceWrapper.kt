package com.rdude.rpgeditork.wrapper

import com.rdude.rpgeditork.settings.Settings
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import ru.rdude.rpg.game.logic.data.resources.Resource
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

    val nameProperty = SimpleStringProperty(resource.name)
    var name: String
        set(value) {
            nameProperty.set(value)
            resource.name = value
        }
        get() = resource.name

}