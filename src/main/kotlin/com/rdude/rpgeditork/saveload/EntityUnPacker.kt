package com.rdude.rpgeditork.saveload

import com.rdude.rpgeditork.enums.entityDataTypeOf
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import ru.rdude.rpg.game.logic.data.EntityData
import tornadofx.Controller
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipInputStream

class EntityUnPacker : Controller() {

    private val jsonSerializer = find<GameJsonSerializerController>().gameJsonSerializer

    fun unpack(file: Path, to: Path): EntityData? {
        var result: EntityData? = null
        val fileInputStream = Files.newInputStream(file)
        val zipInputStream = ZipInputStream(fileInputStream)

        var zipEntry = zipInputStream.nextEntry
        while (zipEntry != null) {
            // check only files
            if (!zipEntry.isDirectory) {
                Files.copy(zipInputStream, Path.of(to.toString(), zipEntry.name))
            }
            // deserialize entityData
            entityDataTypeOf(zipEntry.name)?.let {
                result = jsonSerializer.deSerializeEntityData(zipInputStream.readAllBytes().toString(), it.clazz)
            }
            zipEntry = zipInputStream.nextEntry
        }
        zipInputStream.close()
        fileInputStream.close()
        return result
    }

}