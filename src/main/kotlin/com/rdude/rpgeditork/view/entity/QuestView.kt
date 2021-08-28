package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.scene.control.TextField
import ru.rdude.rpg.game.logic.data.QuestData
import tornadofx.anchorpane

class QuestView(wrapper: EntityDataWrapper<QuestData>) : EntityView<QuestData>(wrapper) {

    override val root = anchorpane {

    }

    override val nameField: TextField
        get() = TODO("Not yet implemented")
    override val nameInEditorField: TextField
        get() = TODO("Not yet implemented")

    override fun reasonsNotToSave(): List<String> {
        //TODO("Not yet implemented")
        return listOf()
    }
}