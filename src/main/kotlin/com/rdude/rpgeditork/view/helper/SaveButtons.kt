package com.rdude.rpgeditork.view.helper

import com.rdude.rpgeditork.saveload.EntitySaver
import com.rdude.rpgeditork.view.entity.EntityView
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Side
import tornadofx.*

class SaveButtons(private val viewParent: EntityView<*>) : Fragment() {

    private val saver = find<EntitySaver>()
    private val saveTo = contextmenu {
        item("Save to module") {
            setOnAction { saver.saveToModule(viewParent.wrapper) }
        }
        item("Save to file") {
            setOnAction { saver.saveToFile(viewParent.wrapper) }
        }
    }

    override val root = hbox {
        button(" \uD83D\uDCBE ") {
            action { saver.save(viewParent.wrapper) }
        }
        button("\uD83D\uDCBE ▼") {
            action {
                saveTo.show(this, Side.BOTTOM, 0.0, 0.0)
            }
        }
    }
}