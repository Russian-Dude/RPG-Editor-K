package com.rdude.rpgeditork.wrapper

import com.rdude.rpgeditork.settings.Settings
import javafx.scene.image.Image
import ru.rdude.rpg.game.logic.data.resources.Resource
import java.nio.file.Path

data class ImageResourceWrapper(override val resource: Resource) : ResourceWrapper<Image>(
    resource = resource,
    file = Path.of(Settings.tempImagesFolder.toString(), resource.guid.toString(), ".png"),
    fxRepresentation = Image(Path.of(Settings.tempImagesFolder.toString(), resource.guid.toString(), ".png").toString())
)