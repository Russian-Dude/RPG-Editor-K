package com.rdude.rpgeditork.view.helper

import com.rdude.rpgeditork.saveload.EntitySaver
import com.rdude.rpgeditork.style.LightTheme
import com.rdude.rpgeditork.utils.loadDialog
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.layout.HBox
import ru.rdude.rpg.game.logic.data.EntityData
import ru.rdude.rpg.game.logic.data.Module
import tornadofx.*

class EntityTopMenu<E : EntityData>(
    private val wrapperProperty: SimpleObjectProperty<EntityDataWrapper<E>>
) : HBox() {

    private val saver = find<EntitySaver>()
    private val wrapper get() = wrapperProperty.get()

    private val saveToContextMenu = contextmenu {
        item("Save to module").action { saver.saveToModule(wrapper) }
        item("Save to file").action { saver.saveToFile(wrapper) }
    }

    private val saveButton = button("Save") {
        action {
            if (wrapper.isInsideModule || wrapper.isInsideFile) {
                loadDialog("Saving...") {
                    saver.save(wrapper)
                }
            } else {
                saveToContextMenu.show(this, Side.BOTTOM, 0.0, 0.0)
            }
        }
    }

    private val saveToButton = button("Save to") {
        action { saveToContextMenu.show(this, Side.BOTTOM, 0.0, 0.0) }
    }

    init {
        styleClass.add("entity-top-menu")
        alignment = Pos.CENTER_LEFT
        anchorpaneConstraints {
            leftAnchor = if (wrapper.entityData is Module) 165.0 else if (wrapper.entityData.isDescriber) 60.0 else 160.0
            topAnchor = 1.0
        }
        add(saveButton)
        add(saveToButton)
        label(wrapper.insideAsStringProperty) {
            isDisable = true
            paddingLeftProperty.set(25.0)
        }
        label("Has ${wrapper.dependencies.size} dependencies") {
            isDisable = true
            paddingLeftProperty.set(25.0)
            wrapper.dependencies.sizeProperty.stringBinding(this.textProperty()) { i -> "Has $i dependencies" }
        }
    }
}