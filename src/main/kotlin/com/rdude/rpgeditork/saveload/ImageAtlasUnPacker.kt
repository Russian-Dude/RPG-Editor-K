package com.rdude.rpgeditork.saveload

import com.badlogic.gdx.backends.lwjgl.LwjglFiles
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.tools.texturepacker.TextureUnpacker
import tornadofx.Controller
import java.nio.file.Path

class ImageAtlasUnPacker : Controller() {

    private val textureUnPacker = TextureUnpacker().apply { setQuiet(true) }

    fun unpack(packFile: Path, outputDir: Path) {
        val atlasFile = LwjglFiles().absolute(packFile.toAbsolutePath().toString())
        val textureAtlasData = TextureAtlas.TextureAtlasData(atlasFile, atlasFile.parent(), false)
        textureUnPacker.splitAtlas(textureAtlasData, outputDir.toAbsolutePath().toString())
    }
}