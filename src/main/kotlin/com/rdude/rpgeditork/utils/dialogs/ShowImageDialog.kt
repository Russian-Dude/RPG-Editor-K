package com.rdude.rpgeditork.utils.dialogs

import com.rdude.rpgeditork.view.MainView
import com.rdude.rpgeditork.wrapper.ImageResourceWrapper
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import javafx.stage.StageStyle
import tornadofx.View
import tornadofx.action
import tornadofx.paddingAll
import tornadofx.vbox
import kotlin.math.min

class ShowImageDialog : View() {

    private val closeButton = Button("Close").apply { action { this@ShowImageDialog.close() } }
    private val imageView = ImageView()

    override val root = vbox {
        styleClass.add("editor-dialog")
        paddingAll = 10.0
        spacing = 10.0
        alignment = Pos.CENTER
        add(imageView)
        add(closeButton)
    }

    fun showAndWait(wrapper: ImageResourceWrapper) {
        with(wrapper.fxRepresentation) {
            imageView.fitWidth = min(width, find<MainView>().screenWidth - 200.0)
            imageView.fitHeight = min(height, find<MainView>().screenHeight - 200.0)
            imageView.image = this
        }
        openModal(StageStyle.UNDECORATED)
    }

}