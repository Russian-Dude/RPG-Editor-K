package com.rdude.rpgeditork.saveload

import com.rdude.rpgeditork.enums.entityDataTypeOf
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import ru.rdude.rpg.game.logic.data.EntityData
import tornadofx.Controller
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipInputStream
import kotlin.io.path.exists

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
                val path = Path.of(to.toString(), zipEntry.name)
                if (Files.notExists(path.parent)) {
                    Files.createDirectories(path.parent)
                }
                Files.copy(zipInputStream, path)
            }
            // deserialize entityData
            entityDataTypeOf(zipEntry.name)?.let {
                result = jsonSerializer.deSerializeEntityData(String(Files.readAllBytes(Path.of(to.toString(), zipEntry.name))), it.clazz)
            }
            zipEntry = zipInputStream.nextEntry
        }
        zipInputStream.close()
        fileInputStream.close()
        return result
    }

}