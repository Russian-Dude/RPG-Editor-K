package com.rdude.rpgeditork.view.search

import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import ru.rdude.fxlib.dialogs.SearchDialog
import ru.rdude.rpg.game.logic.data.EventData
import tornadofx.gridpane

class EventSearchView : EntitySearchView<EventData>() {

    override fun configPopup(searchDialog: SearchDialog<EntityDataWrapper<EventData>>) {
        //TODO("Not yet implemented")
    }

    override val root = gridpane {  }
}