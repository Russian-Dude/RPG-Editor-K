package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import ru.rdude.rpg.game.logic.data.MonsterData
import tornadofx.anchorpane

class MonsterDescriberView(wrapper: EntityDataWrapper<MonsterData>) : EntityView<MonsterData>(wrapper) {

    override val root = anchorpane {

    }

    override fun saveTo(wrapper: EntityDataWrapper<MonsterData>): Boolean {
        TODO("Not yet implemented")
    }

    override fun load(wrapper: EntityDataWrapper<MonsterData>) {
        TODO("Not yet implemented")
    }
}