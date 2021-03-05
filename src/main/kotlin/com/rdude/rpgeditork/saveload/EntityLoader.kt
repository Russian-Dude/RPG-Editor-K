package com.rdude.rpgeditork.saveload

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.enums.hasPackedImages
import com.rdude.rpgeditork.settings.Settings
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import ru.rdude.rpg.game.utils.Functions
import tornadofx.Controller
import java.nio.file.Files
import java.nio.file.Path

class EntityLoader : Controller() {

    private val jsonSerializer = find<GameJsonSerializerController>().gameJsonSerializer
    private val entityUnPacker = find<EntityUnPacker>()
    private val imageAtlasUnPacker = find<ImageAtlasUnPacker>()

    fun loadFromFile(file: Path): EntityDataWrapper<*>? {
        // create temp folder to hold unpacked data from entity
        val tempFolder = Path.of(Settings.tempFolder.toString(), Functions.generateGuid().toString())
        val entityData = entityUnPacker.unpack(file, tempFolder)
        val wrapper: EntityDataWrapper<*>
        if (entityData != null) {
            val insideData: EntityDataWrapper<*>? = Data.getEntity(entityData.guid)
            // if this entity is not already inside data
            if (insideData == null) {
                wrapper = EntityDataWrapper(entityData)
            }
            // if entity already inside data return it
            else {
                return insideData
            }
        }
        // if entity unpacking failed return null
        else {
            return null
        }

        val directoryToMoveImages =
            if (wrapper.hasPackedImages) Path.of(
                Settings.tempPackedImagesFolder.toString(),
                wrapper.entityData.guid.toString()
            )
            else Settings.tempImagesFolder

        // move unpacked files and unpack images if needed
        // images
        val imagesDir = Path.of(tempFolder.toString(), "images")
        if (Files.exists(imagesDir)) {
            Files.list(imagesDir)
                .forEach {
                    if (wrapper.hasPackedImages && it.toString().endsWith(".atlas")) {
                        imageAtlasUnPacker.unpack(it, Settings.tempImagesFolder)
                    }
                    Files.move(it, Path.of(directoryToMoveImages.toString(), it.fileName.toString()))
                }
        }
        // sounds
        val soundsDir = Path.of(tempFolder.toString(), "sounds")
        if (Files.exists(soundsDir)) {
            Files.list(soundsDir)
                .forEach {
                    Files.move(it, Path.of(Settings.tempSoundsFolder.toString(), it.fileName.toString()))
                }
        }

        // delete temp directory from which files were moved to actual temp dir
        Files.deleteIfExists(tempFolder)

        // add to data
        Data.addEntity(wrapper)

        return wrapper
    }
}