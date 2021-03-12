package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import ru.rdude.rpg.game.logic.data.QuestData
import tornadofx.anchorpane

class QuestView(wrapper: EntityDataWrapper<QuestData>) : EntityView<QuestData>(wrapper) {

    override val root = anchorpane {

    }

    override fun saveTo(wrapper: EntityDataWrapper<QuestData>): Boolean {
        TODO("Not yet implemented")
    }
}