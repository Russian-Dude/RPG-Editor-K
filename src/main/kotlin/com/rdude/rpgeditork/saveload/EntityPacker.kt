package com.rdude.rpgeditork.saveload

import com.rdude.rpgeditork.enums.entityTypeName
import com.rdude.rpgeditork.enums.imageFiles
import com.rdude.rpgeditork.enums.soundFiles
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import ru.rdude.rpg.game.logic.data.EntityData
import tornadofx.Controller
import java.io.BufferedReader
import java.io.StringReader
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class EntityPacker : Controller() {

    private val jsonSerializer = find<GameJsonSerializerController>().gameJsonSerializer

    fun <E : EntityData> pack(wrapper: EntityDataWrapper<E>, to: Path) {

        val outputStream = Files.newOutputStream(to)
        val zipOutputStream = ZipOutputStream(outputStream)
        val jsonReader = BufferedReader(StringReader(jsonSerializer.serialize(wrapper.entityData)))

        // write images
        for (imageFile in wrapper.imageFiles) {
            zipOutputStream.putNextEntry(ZipEntry("images/${imageFile.fileName}"))
            Files.copy(imageFile, zipOutputStream)
            zipOutputStream.closeEntry()
        }

        // write sounds
        for (soundFile in wrapper.soundFiles) {
            zipOutputStream.putNextEntry(ZipEntry("sounds/${soundFile.fileName}"))
            Files.copy(soundFile, zipOutputStream)
            zipOutputStream.closeEntry()
        }

        // write main data
        zipOutputStream.putNextEntry(ZipEntry(wrapper.entityTypeName))
        var b = jsonReader.read()
        while (b >= 0) {
            zipOutputStream.write(b)
            b = jsonReader.read()
        }

        zipOutputStream.close()
        outputStream.close()
        jsonReader.close()

    }
}