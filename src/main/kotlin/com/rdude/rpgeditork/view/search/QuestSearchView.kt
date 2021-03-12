package com.rdude.rpgeditork.view.search

import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import ru.rdude.fxlib.dialogs.SearchDialog
import ru.rdude.rpg.game.logic.data.QuestData
import tornadofx.gridpane

class QuestSearchView : EntitySearchView<QuestData>() {

    override fun configPopup(searchDialog: SearchDialog<EntityDataWrapper<QuestData>>) {
        //TODO("Not yet implemented")
    }

    override val root = gridpane {  }
}