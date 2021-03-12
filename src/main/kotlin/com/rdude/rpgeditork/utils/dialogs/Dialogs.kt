package com.rdude.rpgeditork.utils.dialogs

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.utils.InfoDialog
import com.rdude.rpgeditork.utils.config
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.scene.image.Image
import ru.rdude.fxlib.dialogs.SearchDialog
import ru.rdude.rpg.game.logic.data.SkillData

object Dialogs {

    private val wrongSizeDialog = InfoDialog(
        image = Image("icons\\warning.png")
    )

    fun wrongSizeDialog(width: Double, height: Double): InfoDialog {
        wrongSizeDialog.headerText = "Size of this image must be ${width.toInt()}x${height.toInt()}"
        return wrongSizeDialog
    }

    val skillsSearchDialog = SearchDialog(Data.skillsList).apply { config() }
}