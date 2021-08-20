package com.rdude.rpgeditork.saveload

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.settings.Settings
import com.rdude.rpgeditork.wrapper.ImageResourceWrapper
import com.rdude.rpgeditork.wrapper.ParticleResourceWrapper
import javafx.stage.FileChooser
import ru.rdude.rpg.game.logic.data.resources.Resource
import ru.rdude.rpg.game.utils.Functions
import tornadofx.FileChooserMode
import tornadofx.chooseFile
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path

// when user load particle
object ParticleFileLoader {

    class ParticleAndImages(val particle: ParticleResourceWrapper, val images: List<ImageResourceWrapper>)

    fun loadParticlesFromFile(): ParticleAndImages? {
        val file = chooseFile(
            filters = arrayOf(FileChooser.ExtensionFilter("particle", "*.p")),
            mode = FileChooserMode.Single,
            title = "Load particle",
            initialDirectory = Settings.loadParticleFolder.toFile()
        )
        if (file.isEmpty()) {
            return null
        }
        Settings.loadParticleFolder = file[0].parentFile.toPath()



        val lines = Files.readAllLines(file[0].toPath())
        var currentName = ""
        val currentFiles: MutableList<Path> = ArrayList()
        var pathLines = false
        val newLines: MutableList<String> = ArrayList();
        val images: MutableList<ImageResourceWrapper> = ArrayList()

        for (i in 0 until lines.size) {
            var line = lines[i]
            if (line.contains("- Delay -")) {
                currentName = lines[i - 1]
            }
            if (line.contains("- Image Paths -")) {
                pathLines = true
                newLines.add(line)
                continue
            }
            if (pathLines) {
                if (line.isNotBlank()) {
                    val parent = file[0].parent
                    if (line.startsWith("\\") || line.startsWith("/")) {
                        line = line.substring(1)
                    }
                    if (line.startsWith(parent)) {
                        currentFiles.add(Path.of(line))
                    }
                    else {
                        if (line.contains(":")) {
                            currentFiles.add(Path.of(line))
                        }
                        else {
                            currentFiles.add(Path.of(parent, line))
                        }
                    }
                }
                else {
                    pathLines = false
                    for (j in 0 until currentFiles.size) {
                        val name = if (j == 0) currentName else "$currentName ($j)"
                        val guid = Functions.generateGuid()
                        val path = Path.of(Settings.tempImagesFolder.toString(), "$guid.png")
                        Files.copy(currentFiles[j], path)
                        val imageWrapper = ImageResourceWrapper(Resource(name, guid))
                        images.add(imageWrapper)
                        newLines.add(guid.toString())
                    }
                    currentName = ""
                    currentFiles.clear()
                }
            }
            else {
                newLines.add(line)
            }
        }


        val particleGuid = Functions.generateGuid()
        Files.write(Path.of(Settings.tempParticlesFolder.toString(), "$particleGuid.p"), newLines)

        val particleResourceWrapper = ParticleResourceWrapper(Resource(file[0].name.replace(".p", ""), particleGuid))
        particleResourceWrapper.file.toFile().deleteOnExit()
        return ParticleAndImages(particleResourceWrapper, images)
    }

}