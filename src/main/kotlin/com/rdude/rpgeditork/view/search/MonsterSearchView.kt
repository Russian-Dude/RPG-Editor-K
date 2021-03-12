package com.rdude.rpgeditork.view.search

import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import ru.rdude.fxlib.dialogs.SearchDialog
import ru.rdude.rpg.game.logic.data.MonsterData
import tornadofx.gridpane

class MonsterSearchView : EntitySearchView<MonsterData>() {

    override fun configPopup(searchDialog: SearchDialog<EntityDataWrapper<MonsterData>>) {
        //TODO("Not yet implemented")
    }

    override val root = gridpane {  }
}