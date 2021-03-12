package com.rdude.rpgeditork.utils.dialogs

import com.rdude.rpgeditork.utils.InfoDialog
import javafx.scene.image.Image

object Dialogs {

    private val wrongSizeDialog = InfoDialog(
        image = Image("icons\\warning.png")
    )

    fun wrongSizeDialog(width: Double, height: Double): InfoDialog {
        wrongSizeDialog.headerText = "Size of this image must be ${width.toInt()}x${height.toInt()}"
        return wrongSizeDialog
    }
}