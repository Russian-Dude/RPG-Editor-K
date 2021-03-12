package com.rdude.rpgeditork.utils

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.text.Font
import tornadofx.*

class LoadDialog : View() {

    private val textProperty = SimpleStringProperty("")
    var text: String
        get() = textProperty.get()
        set(value) = textProperty.set(value)

    private val graphicProperty = SimpleObjectProperty<Image?>(null)
    var graphic: Image?
        get() = graphicProperty.get()
        set(value) = graphicProperty.set(value)

    override val root = hbox(spacing = 10.0, alignment = Pos.CENTER) {
        style {
            backgroundColor += c("#F5F6FA")
            borderColor += box(c("#353B48"))
        }
        alignment = Pos.CENTER
        label(textProperty) {
            font = Font.font(16.0)
            paddingAll = 10.0
        }
        imageview(graphicProperty) {
            paddingAll = 10.0
        }
    }
}