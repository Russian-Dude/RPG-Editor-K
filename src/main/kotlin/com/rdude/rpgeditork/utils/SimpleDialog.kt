package com.rdude.rpgeditork.utils

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.scene.text.Font
import javafx.stage.StageStyle
import tornadofx.*

class SimpleDialog<T>(
    private val vertical: Boolean = false,
    private val defaultReturn: T,
    dialogText: String = "",
    dialogImage: Image? = null,
    vararg buttons: Pair<String, () -> T>
) : Fragment() {

    private val textProperty = SimpleStringProperty(dialogText)
    private val imageProperty = SimpleObjectProperty<Image>(dialogImage)
    private var box: Pane by singleAssign()
    private var t: T = defaultReturn

    override val root = if (vertical) {
        hbox {
            spacing = 5.0
            style {
                backgroundColor += c("#F5F6FA")
                borderColor += box(c("#353B48"))
            }
            vbox {
                alignment = Pos.CENTER
                paddingAll = 5.0
                label(textProperty) {
                    if (textProperty().get().isNotBlank()) paddingBottom = 5.0
                }
                box = vbox(5.0) {
                    alignment = Pos.CENTER
                }
            }
            imageview(imageProperty) {
                paddingAll = 5.0
            }
        }
    } else {
        hbox {
            spacing = 10.0
            alignment = Pos.CENTER
            style {
                backgroundColor += c("#F5F6FA")
                borderColor += box(c("#353B48"))
            }
            borderpane {
                alignment = Pos.CENTER
                center = label(textProperty) {
                    font = Font.font(16.0)
                }
                box = hbox(10.0) {
                    alignment = Pos.BOTTOM_CENTER
                    bottom = this
                }
                paddingAll = 5.0
            }
            imageview(imageProperty) {
                paddingAll = 5.0
            }
        }
    }

    init {
        buttons.forEach { addButton(it.first, it.second) }
    }

    fun addButton(text: String, returning: T, graphic: Node? = null) {
        val button = Button(text, graphic)
        button.action {
            t = returning
            close()
        }
        if (vertical) {
            button.prefWidth = 200.0
        }
        box.add(button)
    }


    fun addButton(text: String, returning: () -> T, graphic: Node? = null) {
        val button = Button(text, graphic)
        button.action {
            t = returning.invoke()
            close()
        }
        if (vertical) {
            button.prefWidth = 200.0
        }
        button.font = Font.font(14.0)
        box.add(button)
    }

    fun showAndWait(): T {
        openModal(StageStyle.UNDECORATED, escapeClosesWindow = false, block = true)
        val ret = t
        t = defaultReturn
        return ret
    }
}