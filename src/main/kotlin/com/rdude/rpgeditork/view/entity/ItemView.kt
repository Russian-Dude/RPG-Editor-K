package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import ru.rdude.rpg.game.logic.data.ItemData
import tornadofx.anchorpane

class ItemView(wrapper: EntityDataWrapper<ItemData>) : EntityView<ItemData>(wrapper) {

    override val root = anchorpane {

    }


    override fun reasonsNotToSave(): List<String> {
        //TODO("Not yet implemented")
        return listOf()
    }
}