package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import ru.rdude.rpg.game.logic.data.MonsterData
import tornadofx.anchorpane

class MonsterDescriberView(wrapper: EntityDataWrapper<MonsterData>) : EntityView<MonsterData>(wrapper) {

    override val root = anchorpane {

    }

    override fun reasonsNotToSave(): List<String> {
        //TODO("Not yet implemented")
        return listOf()
    }
}