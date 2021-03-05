package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import ru.rdude.rpg.game.logic.data.EventData
import tornadofx.anchorpane

class EventView(wrapper: EntityDataWrapper<EventData>) : EntityView<EventData>(wrapper) {


    override val root = anchorpane {

    }

    override fun saveTo(wrapper: EntityDataWrapper<EventData>): Boolean {
        TODO("Not yet implemented")
    }

    override fun load(wrapper: EntityDataWrapper<EventData>) {
        TODO("Not yet implemented")
    }
}