package com.rdude.rpgeditork.view.helper

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.settings.Settings
import com.rdude.rpgeditork.utils.dialogs.Dialogs
import com.rdude.rpgeditork.wrapper.ImageResourceWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ListChangeListener
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.stage.FileChooser
import ru.rdude.rpg.game.logic.data.resources.Resource
import ru.rdude.rpg.game.utils.Functions
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path

class ImagePicker(
    val header: String = "",
    val imageWidth: Double = 64.0,
    val imageHeight: Double = 64.0,
    val imageWidthRestriction: Double? = null,
    val imageHeightRestriction: Double? = null
) : Fragment() {

    val imageProperty = SimpleObjectProperty<Image>()
    val nameProperty = SimpleStringProperty("No image")
    var imageResourceWrapper: ImageResourceWrapper? = null
        set(value) {
            imageProperty.set(value?.fxRepresentation)
            if (value != null) {
                nameProperty.unbind()
                nameProperty.bind(value.nameProperty)
            } else {
                nameProperty.unbind()
                nameProperty.set("No image")
            }
            field = value
        }

    init {
        Data.images.list.onChange {
            while (it.next()) {
                if (it.wasRemoved() && imageResourceWrapper != null && it.removed.contains(imageResourceWrapper)) {
                    imageResourceWrapper = null
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
                imageview(imageProperty) {
                    fitWidth = imageWidth
                    fitHeight = imageHeight
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
                        imageResourceWrapper = null
                    }
                }
                button {
                    maxWidth = Double.MAX_VALUE
                    text = "Load from file"
                    action {
                        val file = chooseFile(
                            filters = arrayOf(FileChooser.ExtensionFilter("image", "*.png")),
                            mode = FileChooserMode.Single,
                            title = "Load image",
                            initialDirectory = Settings.loadImageFolder.toFile()
                        )
                        if (file.isEmpty()) {
                            return@action
                        }
                        Settings.loadImageFolder = file[0].parentFile.toPath()
                        val guid = Functions.generateGuid()
                        Files.copy(file[0].toPath(), Path.of(Settings.tempImagesFolder.toString(), "$guid.png"))
                        val imageRes = ImageResourceWrapper(Resource(file[0].name.replace(".png", ""), guid))
                        imageRes.file.toFile().deleteOnExit()
                        if ((imageWidthRestriction != null && imageRes.fxRepresentation.width != imageWidthRestriction)
                            || imageHeightRestriction != null && imageRes.fxRepresentation.height != imageHeightRestriction
                        ) {
                            Dialogs.wrongSizeDialog(imageWidthRestriction!!, imageHeightRestriction!!).show()
                        } else {
                            imageResourceWrapper = imageRes
                            if (imageResourceWrapper != null) {
                                Data.images.add(imageResourceWrapper!!)
                            }
                        }
                    }
                }
                button {
                    maxWidth = Double.MAX_VALUE
                    text = "Load from resources"
                    action {
                        var wrapper: ImageResourceWrapper? = null
                        wrapper = if (imageWidthRestriction != null && imageHeightRestriction != null) {
                            Dialogs.imageSearchDialog(imageWidthRestriction to imageHeightRestriction).orElse(null)
                        } else {
                            Dialogs.imageSearchDialog().orElse(null)
                        }
                        if (wrapper != null) {
                            imageResourceWrapper = wrapper
                        }
                    }
                }
            }
        }
    }

}