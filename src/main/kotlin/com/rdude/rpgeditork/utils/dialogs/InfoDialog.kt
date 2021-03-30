package com.rdude.rpgeditork.utils.dialogs

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.text.Font
import javafx.stage.StageStyle
import tornadofx.*

class InfoDialog(headerText: String = "", infoText: String = "", image: Image? = null) : Fragment() {

    private val headerTextProperty = SimpleStringProperty(headerText)
    var headerText: String
        get() = headerTextProperty.get()
        set(value) = headerTextProperty.set(value)

    private val infoTextProperty = SimpleStringProperty(infoText)
    var infoText: String
        get() = infoTextProperty.get()
        set(value) = infoTextProperty.set(value)

    var infoTextLines: List<String>
        get() = infoText.lines()
        set(value) {
            infoText = value.reduce { acc, s -> "$acc\r\n$s" }
        }

    private val imageProperty = SimpleObjectProperty<Image>(image)
    var image: Image
        get() = imageProperty.get()
        set(value) = imageProperty.set(value)

    override val root = vbox {
        styleClass.add("editor-dialog")
        alignment = Pos.CENTER
        borderpane {
            paddingAll = 5.0
            left = vbox {
                alignment = Pos.CENTER
                label(headerTextProperty) {
                    font = Font.font(16.0)
                    alignment = Pos.CENTER
                }
            }
            right = imageview(imageProperty)
        }
        scrollpane {
            minHeight = 0.0
            maxHeight = 200.0
            paddingAll = 5.0
            label(infoTextProperty)
        }
        button("Ok") {
            paddingAll = 5.0
            action { close() }
        }
    }

    fun show() = openModal(StageStyle.UNDECORATED, block = true)
}