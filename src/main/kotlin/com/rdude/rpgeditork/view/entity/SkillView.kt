package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.view.helper.SaveButtons
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import ru.rdude.rpg.game.logic.data.SkillData
import tornadofx.anchorpane
import tornadofx.tabpane

class SkillView(wrapper: EntityDataWrapper<SkillData>) : EntityView<SkillData>(wrapper) {

    override val root = anchorpane {
        add(find<SaveButtons>())
        tabpane {

        }
    }

    override fun saveTo(wrapper: EntityDataWrapper<SkillData>): Boolean {
        TODO("Not yet implemented")
    }

    override fun load(wrapper: EntityDataWrapper<SkillData>) {
        TODO("Not yet implemented")
    }

}