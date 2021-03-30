package com.rdude.rpgeditork.utils.dialogs

import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.stage.StageStyle
import tornadofx.*

class RenameDialog : View() {

    lateinit var renamingProperty: StringProperty

    private val header = text("Rename")
    private val textField = TextField()
    private val ok = Button("  Ok  ").apply {
        shortcut("Enter")
        action {
            renamingProperty.value = textField.text
            this@RenameDialog.close()
        }
    }
    private val cancel = Button("Cancel").apply { action { this@RenameDialog.close() } }

    override val root = vbox {
        styleClass.add("editor-dialog")
        paddingAll = 10.0
        spacing = 10.0
        alignment = Pos.CENTER
        add(header)
        add(textField)
        hbox {
            spacing = 15.0
            alignment = Pos.CENTER
            add(ok)
            add(cancel)
        }
    }

    fun showAndWait(rename: StringProperty) {
        renamingProperty = rename
        header.text = "Rename \"${rename.get() ?: ""}\""
        textField.text = ""
        openModal(StageStyle.UNDECORATED)
    }
}