package com.rdude.rpgeditork.utils.dialogs

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.utils.isPositive
import com.rdude.rpgeditork.view.helper.SoundPlayer
import com.rdude.rpgeditork.wrapper.ImageResourceWrapper
import javafx.beans.property.StringProperty
import javafx.collections.transformation.FilteredList
import javafx.scene.control.TextField
import javafx.scene.image.Image
import ru.rdude.fxlib.dialogs.SearchDialog
import tornadofx.FX.Companion.find
import tornadofx.filterInput
import tornadofx.isInt
import java.util.*
import java.util.function.Predicate

object Dialogs {

    private val wrongSizeDialog = InfoDialog(
        image = Image("icons\\warning.png")
    )

    fun wrongSizeDialog(width: Double, height: Double): InfoDialog {
        wrongSizeDialog.headerText = "Size of this image must be ${width.toInt()}x${height.toInt()}"
        return wrongSizeDialog
    }

    private val renameDialog = find<RenameDialog>()
    fun renameDialog(rename: StringProperty) = renameDialog.showAndWait(rename)

    private val showImageDialog = find<ShowImageDialog>()
    fun showImageDialog(wrapper: ImageResourceWrapper) = showImageDialog.showAndWait(wrapper)

    private val confirmationDialog = SimpleDialog(
        defaultReturn = false,
        dialogImage = Image("icons\\question.png"),
        buttons = arrayOf(
            "Yes" to { true },
            "No" to { false }))

    fun confirmationDialog(text: String): Boolean {
        confirmationDialog.textProperty.set(text)
        return confirmationDialog.showAndWait()
    }

    private val inputNumberDialogTextField = TextField()
    private val inputNumberDialog = SimpleDialog(
        defaultReturn = -1,
        contentNodes = listOf(inputNumberDialogTextField),
        buttons = arrayOf(
            "Ok" to { if (inputNumberDialogTextField.text.isBlank()) 0 else inputNumberDialogTextField.text.toInt() }
        )
    )

    fun inputNumberDialog(text: String, positive: Boolean = false): Int {
        inputNumberDialog.textProperty.set(text)
        if (positive) {
            inputNumberDialogTextField.filterInput { it.controlNewText.isInt() && it.controlNewText.toInt().isPositive() }
        }
        else {
            inputNumberDialogTextField.filterInput { it.controlNewText.isInt() }
        }
        return inputNumberDialog.showAndWait()
    }


    // images
    private val imagesFiltered = FilteredList(Data.images.list)
    private val imageSearchDialog = SearchDialog(imagesFiltered).apply {
        with(searchPane) {
            setNameBy { w -> w.nameProperty.get() }
            setTextFieldSearchBy({ w -> w.nameProperty.get() })
            setIcon { w -> w.fxRepresentation }
            addContextMenuItem("Show") {
                showImageDialog(it)
            }
        }
    }

    fun imageSearchDialog(size: Pair<Double, Double>? = null): Optional<ImageResourceWrapper> {
        imagesFiltered.predicate =
            size.let { p -> Predicate { (size == null || it.fxRepresentation.width == p?.first)
                    && (size == null || it.fxRepresentation.height == p?.second) } }
        return imageSearchDialog.showAndWait()
    }

    fun imageSearchDialog(predicate: Predicate<ImageResourceWrapper>): Optional<ImageResourceWrapper> {
        imagesFiltered.predicate = predicate
        return imageSearchDialog.showAndWait()
    }

    // sounds
    val soundsSearchDialog = SearchDialog(Data.sounds.list).apply {
        with(searchPane) {
            setNameBy { w -> w.nameProperty.get() }
            setTextFieldSearchBy({ w -> w.nameProperty.get() })
            setCellGraphic( { SoundPlayer() }, { res, player -> player.resource = res } )
        }
    }

    // particles
    val particlesSearchDialog = SearchDialog(Data.particles.list).apply {
        with(searchPane) {
            setNameBy { w -> w.nameProperty.get() }
            setTextFieldSearchBy({ w -> w.nameProperty.get() })
        }
    }
}