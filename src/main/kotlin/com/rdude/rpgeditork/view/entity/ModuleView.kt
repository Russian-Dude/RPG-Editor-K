package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import ru.rdude.rpg.game.logic.data.Module
import tornadofx.anchorpane

class ModuleView(wrapper: EntityDataWrapper<Module>) : EntityView<Module>(wrapper) {

    override val root = anchorpane {

    }

    override fun saveTo(wrapper: EntityDataWrapper<Module>): Boolean {
        TODO("Not yet implemented")
    }
}