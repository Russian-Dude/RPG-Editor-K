package com.rdude.rpgeditork.view.search

import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.scene.Parent
import javafx.scene.layout.VBox
import ru.rdude.fxlib.dialogs.SearchDialog
import ru.rdude.rpg.game.logic.data.PlayerClassData

class PlayerClassSearchView: EntitySearchView<PlayerClassData>() {

    override fun configPopup(searchDialog: SearchDialog<EntityDataWrapper<PlayerClassData>>) {
    }

    override val root: Parent = VBox()
}