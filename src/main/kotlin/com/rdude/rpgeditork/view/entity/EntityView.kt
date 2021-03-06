package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.enums.entityTypeName
import com.rdude.rpgeditork.saveload.EntitySaver
import com.rdude.rpgeditork.utils.ChangesChecker
import com.rdude.rpgeditork.utils.InfoDialog
import com.rdude.rpgeditork.utils.SimpleDialog
import com.rdude.rpgeditork.utils.ViewFieldsSaver
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import javafx.scene.control.Tab
import javafx.scene.image.Image
import ru.rdude.rpg.game.logic.data.EntityData
import tornadofx.Fragment
import tornadofx.onChange

abstract class EntityView<E : EntityData>(entityWrapper: EntityDataWrapper<E>) : Fragment() {

    abstract fun saveTo(wrapper: EntityDataWrapper<E>): Boolean

    val wrapperProperty: SimpleObjectProperty<EntityDataWrapper<E>> = SimpleObjectProperty(entityWrapper).apply {
        this.onChange {
            with(wrapper.entityData.nameInEditor) {
                if (this != null && this.isNotEmpty()) this@EntityView.name.set(this)
                else this@EntityView.name.set("Unnamed ${wrapper.dataType.name}${if (wrapper.entityData.isDescriber) " describer" else ""}")
            }
        }
    }
    var wrapper: EntityDataWrapper<E>
        get() = wrapperProperty.get()
        set(value) = wrapperProperty.set(value)

    val entityData: E
        get() = wrapper.entityData

    val saver = find<EntitySaver>()

    val fieldsSaver = ViewFieldsSaver(this)

    val name: SimpleStringProperty = SimpleStringProperty(
        with(wrapper.entityData.nameInEditor) {
            if (this != null && this.isNotEmpty()) this
            else "Unnamed ${wrapper.dataType.name}${if (wrapper.entityData.isDescriber) " describer" else ""}"
        }
    ).apply {
        wrapper.entityNameProperty.onChange { set(it) }
    }

    val changesChecker = ChangesChecker()

    val canNotSaveDialog = InfoDialog("Can not save ${name.get()} because of:", image = Image("icons\\warning.png"))

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