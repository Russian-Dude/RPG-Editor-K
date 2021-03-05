package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.saveload.EntitySaver
import com.rdude.rpgeditork.utils.SimpleDialog
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import javafx.scene.control.Tab
import javafx.scene.image.Image
import ru.rdude.rpg.game.logic.data.EntityData
import tornadofx.Fragment

abstract class EntityView<E : EntityData>(var wrapper: EntityDataWrapper<E>) : Fragment() {
    abstract fun saveTo(wrapper: EntityDataWrapper<E>): Boolean
    abstract fun load(wrapper: EntityDataWrapper<E>)

    val name: SimpleStringProperty = SimpleStringProperty(
        with(wrapper.entityData.nameInEditor) {
            if (this != null && this.isNotEmpty()) this
            else "Unnamed ${wrapper.dataType.name}${if (wrapper.entityData.isDescriber) " describer" else ""}"
        }
    )

    val saveOnCloseDialog = SimpleDialog(
        defaultReturn = false,
        dialogText = "Save changes to ${name.get()}?",
        dialogImage = Image("icons\\question.png"),
        vertical = false,
        buttons = arrayOf(
            "Yes" to { find<EntitySaver>().save(wrapper) },
            "No" to { true },
            "Cancel" to { false }
        )
    )
}