package com.rdude.rpgeditork.view.helper

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.settings.Settings
import com.rdude.rpgeditork.utils.dialogs.Dialogs
import com.rdude.rpgeditork.wrapper.SoundResourceWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.media.Media
import javafx.stage.FileChooser
import ru.rdude.rpg.game.logic.data.resources.Resource
import ru.rdude.rpg.game.utils.Functions
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path

class SoundPicker(
    val header: String,
    val audioButtonAreaWidth: Double = 0.0,
    val audioButtonAreaHeight: Double = 0.0
) : Fragment() {

    private val mediaProperty = SimpleObjectProperty<Media>()
    private val nameProperty = SimpleStringProperty("No sound")
    private val soundPlayer = SoundPlayer(false)
    var soundResourceWrapper: SoundResourceWrapper? = null
        set(value) {
            mediaProperty.set(value?.fxRepresentation)
            soundPlayer.resource = value
            if (value != null) {
                soundPlayer.isDisable = false
                nameProperty.unbind()
                nameProperty.bind(value.nameProperty)
            } else {
                soundPlayer.isDisable = true
                nameProperty.unbind()
                nameProperty.set("No sound")
            }
            field = value
        }

    init {
        Data.soundsList.onChange {
            while (it.next()) {
                if (it.wasRemoved() && soundResourceWrapper != null && it.removed.contains(soundResourceWrapper)) {
                    soundResourceWrapper = null
                }
            }
        }
    }

    override val root = borderpane {
        top {
            vbox {
                alignment = Pos.CENTER
                label(header)
            }
        }
        center {
            vbox {
                alignment = Pos.CENTER
                vbox {
                    alignment = Pos.CENTER
                    if (audioButtonAreaWidth != 0.0) {
                        prefWidth = audioButtonAreaWidth
                    }
                    if (audioButtonAreaHeight != 0.0) {
                        prefHeight = audioButtonAreaHeight
                    }
                    add(soundPlayer)
                }
                label(nameProperty)
            }
        }
        bottom {
            vbox {
                alignment = Pos.CENTER
                button {
                    maxWidth = Double.MAX_VALUE
                    text = "Remove"
                    action {
                        soundResourceWrapper = null
                    }
                }
                button {
                    maxWidth = Double.MAX_VALUE
                    text = "Load from file"
                    action {
                        val file = chooseFile(
                            filters = arrayOf(FileChooser.ExtensionFilter("sound", "*.mp3")),
                            mode = FileChooserMode.Single,
                            title = "Load sound",
                            initialDirectory = Settings.loadSoundFolder.toFile()
                        )
                        if (file.isEmpty()) {
                            return@action
                        }
                        Settings.loadSoundFolder = file[0].parentFile.toPath()
                        val guid = Functions.generateGuid()
                        Files.copy(file[0].toPath(), Path.of(Settings.tempSoundsFolder.toString(), "$guid.mp3"))
                        val soundRes = SoundResourceWrapper(Resource(file[0].name.replace(".mp3", ""), guid))
                        soundRes.file.toFile().deleteOnExit()
                        Data.sounds[soundRes.guid] = soundRes
                        soundResourceWrapper = soundRes
                    }
                }
                button {
                    maxWidth = Double.MAX_VALUE
                    text = "Load from resources"
                    action {
                        Dialogs.soundsSearchDialog.showAndWait().ifPresent { soundResourceWrapper = it }
                    }
                }
            }
        }
    }

}