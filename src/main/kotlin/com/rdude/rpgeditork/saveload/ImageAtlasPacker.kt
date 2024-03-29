package com.rdude.rpgeditork.saveload

import com.badlogic.gdx.tools.texturepacker.TexturePacker
import com.rdude.rpgeditork.settings.Settings
import tornadofx.Controller
import java.nio.file.Files
import java.nio.file.Path

class ImageAtlasPacker : Controller() {

    fun pack(inputDir: Path, outputDir: Path, packFileName: String) {
        if (Files.list(inputDir).count() < 1) {
            return
        }
        val texturePacker = TexturePacker(TexturePacker.Settings())
        Files.list(inputDir).forEach { p -> texturePacker.addImage(p.toFile()) }
        texturePacker.pack(outputDir.toFile(), packFileName)
    }

    fun pack(images: Collection<Path>, outputDir: Path, packFileName: String) {
        if (images.isEmpty()) {
            return
        }
        val texturePacker = TexturePacker(Settings.tempImagesFolder.toFile() ,TexturePacker.Settings())
        images.forEach { texturePacker.addImage(it.toFile()) }
        texturePacker.pack(outputDir.toFile(), packFileName)
    }

}