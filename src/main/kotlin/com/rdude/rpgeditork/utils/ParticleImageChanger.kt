package com.rdude.rpgeditork.utils

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.saveload.ParticleFileLoader
import com.rdude.rpgeditork.settings.Settings
import com.rdude.rpgeditork.wrapper.ImageResourceWrapper
import com.rdude.rpgeditork.wrapper.ParticleResourceWrapper
import ru.rdude.rpg.game.logic.data.resources.Resource
import ru.rdude.rpg.game.utils.Functions
import tornadofx.isLong
import java.nio.file.Files
import java.nio.file.Path

object ParticleImageChanger {

    fun change(wrapper: ParticleResourceWrapper, old: Long, new: Long) {

        val file = Path.of(Settings.tempParticlesFolder.toString(), "${wrapper.guid}.p")
        val lines = Files.readAllLines(file)

        var pathLines = false
        val newLines: MutableList<String> = ArrayList();

        for (i in 0 until lines.size) {
            val line = lines[i]
            if (line.contains("- Image Paths -")) {
                pathLines = true
                newLines.add(line)
                continue
            }
            if (pathLines) {
                if (line.isNotBlank()) {
                    newLines += if (line.isLong() && line.toLong() == old) {
                        new.toString()
                    } else {
                        line
                    }
                }
                else {
                    pathLines = false
                }
            }
            else {
                newLines.add(line)
            }
        }

        Files.write(Path.of(Settings.tempParticlesFolder.toString(), "${wrapper.guid}.p"), newLines)
    }

}