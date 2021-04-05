package com.rdude.rpgeditork.utils.dialogs

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.view.helper.SoundPlayer
import com.rdude.rpgeditork.wrapper.ImageResourceWrapper
import javafx.beans.property.StringProperty
import javafx.collections.transformation.FilteredList
import javafx.scene.image.Image
import ru.rdude.fxlib.dialogs.SearchDialog
import tornadofx.FX.Companion.find
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


    // images
    private val imagesFiltered = FilteredList(Data.imagesList)
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
    val soundsSearchDialog = SearchDialog(Data.soundsList).apply {
        with(searchPane) {
            setNameBy { w -> w.nameProperty.get() }
            setTextFieldSearchBy({ w -> w.nameProperty.get() })
            setCellGraphic( { SoundPlayer() }, { res, player -> player.resource = res } )
        }
    }
}