package com.rdude.rpgeditork.view.search

import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import ru.rdude.fxlib.dialogs.SearchDialog
import ru.rdude.rpg.game.logic.data.Module
import tornadofx.gridpane

class ModuleSearchView : EntitySearchView<Module>() {

    override fun configPopup(searchDialog: SearchDialog<EntityDataWrapper<Module>>) {
        //TODO("Not yet implemented")
    }

    override val root = gridpane {  }
}