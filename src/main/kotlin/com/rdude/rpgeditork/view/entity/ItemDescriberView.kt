package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import ru.rdude.rpg.game.logic.data.ItemData
import tornadofx.anchorpane

class ItemDescriberView(wrapper: EntityDataWrapper<ItemData>) : EntityView<ItemData>(wrapper) {


    override val root = anchorpane {

    }

    override fun saveTo(wrapper: EntityDataWrapper<ItemData>): Boolean {
        TODO("Not yet implemented")
    }

    override fun load(wrapper: EntityDataWrapper<ItemData>) {
        TODO("Not yet implemented")
    }
}