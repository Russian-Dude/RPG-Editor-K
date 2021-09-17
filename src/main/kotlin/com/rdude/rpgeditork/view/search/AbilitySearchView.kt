package com.rdude.rpgeditork.view.search

import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.scene.layout.VBox
import ru.rdude.fxlib.dialogs.SearchDialog
import ru.rdude.rpg.game.logic.data.AbilityData

class AbilitySearchView: EntitySearchView<AbilityData>() {

    override fun configPopup(searchDialog: SearchDialog<EntityDataWrapper<AbilityData>>) {
    }

    override val root = VBox()
}