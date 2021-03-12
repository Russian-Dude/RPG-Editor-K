package com.rdude.rpgeditork.view.search

import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import ru.rdude.fxlib.dialogs.SearchDialog
import ru.rdude.rpg.game.logic.data.ItemData
import tornadofx.gridpane

class ItemSearchView : EntitySearchView<ItemData>() {

    override fun configPopup(searchDialog: SearchDialog<EntityDataWrapper<ItemData>>) {
        //TODO("Not yet implemented")
    }

    override val root = gridpane {  }
}