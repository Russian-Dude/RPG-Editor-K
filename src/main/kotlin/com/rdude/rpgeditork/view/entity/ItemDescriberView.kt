package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.enums.ObservableEnums
import com.rdude.rpgeditork.utils.removeSpaces
import com.rdude.rpgeditork.utils.row
import com.rdude.rpgeditork.utils.setNullToStringConverter
import com.rdude.rpgeditork.view.helper.EntityTopMenu
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import ru.rdude.fxlib.containers.selector.SelectorContainer
import ru.rdude.rpg.game.logic.data.ItemData
import ru.rdude.rpg.game.logic.enums.EntityReferenceInfo
import ru.rdude.rpg.game.logic.enums.ItemMainType
import ru.rdude.rpg.game.logic.enums.ItemRarity
import ru.rdude.rpg.game.logic.enums.ItemType
import tornadofx.*
import java.util.function.Predicate

class ItemDescriberView(wrapper: EntityDataWrapper<ItemData>) : EntityView<ItemData>(wrapper) {

    val nameField: TextField = textfield {
        text = entityData.name ?: ""
        textProperty().onChange {
            if (it != null && it.isEmpty() && nameInEditorField.text.isNotEmpty()) {
                this.promptText = nameInEditorField.text
            } else if (nameInEditorField.text.isEmpty()) {
                nameInEditorField.promptText = it
            }
        }
        changesChecker.add(this) { text }
        fieldsSaver.add { it.name = if (text.isNotBlank()) text else promptText }
    }

    val nameInEditorField: TextField = textfield {
        text = entityData.nameInEditor ?: ""
        textProperty().onChange {
            if (it != null && it.isEmpty() && nameField.text.isNotEmpty()) {
                this.promptText = nameField.text
            } else if (nameField.text.isEmpty()) {
                nameField.promptText = it
            }
        }
        changesChecker.add(this) { text }
        fieldsSaver.add {
            val n = if (text.isNotBlank()) text else promptText
            wrapper.entityNameProperty.set(n)
            it.nameInEditor = n
        }
    }

    val mainType = ComboBox(ObservableEnums.ITEM_MAIN_TYPES_NULLABLE).apply {
        setNullToStringConverter("ANY")
        value = entityData.itemMainType
        changesChecker.add(this) { value }
        fieldsSaver.add { it.itemMainType = value }
    }

    val type = ComboBox<ItemType?>().apply {
        // config
        setNullToStringConverter("ANY")
        val predicate = Predicate<ItemType?> { itemType -> mainType.value == itemType?.mainType || itemType == null }
        val filteredList = FilteredList(ObservableEnums.ITEM_TYPES_NULLABLE, predicate)
        items = filteredList
        // load, save
        value = entityData.itemType
        changesChecker.add(this) { value }
        fieldsSaver.add { it.itemType = value }
    }

    val rarity = ComboBox(ObservableEnums.ITEM_RARITY_NULLABLE).apply {
        setNullToStringConverter("ANY")
        value = entityData.rarity
        changesChecker.add(this) { value }
        fieldsSaver.add { it.rarity = value }
    }

    val elements = SelectorContainer.simple(ObservableEnums.ELEMENTS).get().apply {
        setHasSearchButton(false)
        addAll(entityData.elements)
        changesChecker.add(this) { selected.sorted() }
        fieldsSaver.add { it.elements = selected.toHashSet() }
    }

    val description = textarea {
        text = entityData.description
        changesChecker.add(this) { text }
        fieldsSaver.add { it.description = text }
    }

    val itemInfo = ComboBox(ObservableEnums.ENTITY_INFO).apply {
        value = entityData.entityInfo ?: EntityReferenceInfo.ALL
        changesChecker.add(this) { value }
        fieldsSaver.add { it.entityInfo = value }
    }

    val referenceInfo = ComboBox(ObservableEnums.ENTITY_REFERENCE_INFO).apply {
        value = entityData.entityReferenceInfo ?: EntityReferenceInfo.NAME
        changesChecker.add(this) { value }
        fieldsSaver.add { it.entityReferenceInfo = value }
    }

    override val root = anchorpane {
        tabpane {
            fitToParentSize()
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("Logic") {
                hbox {
                    spacing = 10.0
                    paddingAll = 10.0
                    gridpane {
                        hgap = 5.0
                        vgap = 5.0
                        constraintsForColumn(1).maxWidth = 145.0
                        row("Name", nameField)
                        row("Name in editor", nameInEditorField)
                        row("Rarity", rarity)
                        row("Main type", mainType)
                        row("Type", type)
                        row("Elements", elements.apply { prefHeight = 80.0 })
                        row("Item info", itemInfo)
                        row("References info", referenceInfo)
                    }
                    vbox {
                        spacing = 10.0
                        alignment = Pos.TOP_CENTER
                        text("Description")
                        add(description)
                    }
                }
            }
        }
        add(EntityTopMenu(wrapperProperty))
    }

    override fun reasonsNotToSave(): List<String> {
        val messages: MutableList<String> = ArrayList()
        if (nameField.text.removeSpaces().isEmpty() && nameInEditorField.text.removeSpaces().isEmpty()) {
            messages.add("Either one of the fields NAME or NAME IN EDITOR must not be empty")
        }
        return messages
    }
}