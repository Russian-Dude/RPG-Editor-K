package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.enums.ObservableEnums
import com.rdude.rpgeditork.utils.removeSpaces
import com.rdude.rpgeditork.utils.row
import com.rdude.rpgeditork.utils.setNullToStringConverter
import com.rdude.rpgeditork.view.helper.EntityTopMenu
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import ru.rdude.fxlib.containers.selector.SelectorContainer
import ru.rdude.rpg.game.logic.data.SkillData
import ru.rdude.rpg.game.logic.enums.EntityReferenceInfo
import tornadofx.*

class SkillDescriberView (wrapper: EntityDataWrapper<SkillData>) : EntityView<SkillData>(wrapper) {

    override val nameField: TextField = textfield {
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

    override val nameInEditorField: TextField = textfield {
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

    val attackType = ComboBox(ObservableEnums.ATTACK_TYPES_NULLABLE).apply {
        setNullToStringConverter("Any")
        value = entityData.attackType ?: null
        changesChecker.add(this) { value }
        fieldsSaver.add { it.attackType = value }
    }

    val skillType = ComboBox(ObservableEnums.SKILL_TYPES_NULLABLE).apply {
        setNullToStringConverter("Any")
        value = entityData.type ?: null
        changesChecker.add(this) { value }
        fieldsSaver.add { it.type = value }
    }

    val elements = SelectorContainer.simple(ObservableEnums.ELEMENTS).get().apply {
        setHasSearchButton(false)
        addAll(entityData.elements)
        changesChecker.add(this) { selected.sorted() }
        fieldsSaver.add { it.elements = selected.toHashSet() }
    }

    val effectField = ComboBox(ObservableEnums.SKILL_EFFECTS_NULLABLE).apply {
        setNullToStringConverter("Any")
        value = entityData.effect ?: null
        changesChecker.add(this) { value }
        fieldsSaver.add { it.effect = value }
    }

    val buffType = ComboBox(ObservableEnums.BUFF_TYPES_NULLABLE).apply {
        setNullToStringConverter("Any")
        value = entityData.buffType ?: null
        changesChecker.add(this) { value }
        fieldsSaver.add { it.buffType = value }
    }

    val description = textarea {
        isWrapText = true
        text = entityData.description
        changesChecker.add(this) { text }
        fieldsSaver.add { it.description = text }
    }

    val skillInfo = ComboBox(ObservableEnums.ENTITY_INFO).apply {
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
                scrollpane {
                    fitToParentSize()
                    paddingAll = 10.0
                    hbox {
                        spacing = 15.0
                        gridpane {
                            hgap = 5.0
                            vgap = 5.0
                            constraintsForColumn(1).maxWidth = 145.0
                            row("Name", nameField)
                            row("Name in editor", nameInEditorField)
                            row("Skill type", skillType)
                            row("Attack type", attackType)
                            row("Elements", elements.apply { prefHeight = 80.0 })
                            row("Effect", effectField)
                            row("Buff type", buffType)
                            row("Skill info", skillInfo)
                            row("References info", referenceInfo)
                        }
                        vbox {
                            spacing = 5.0
                            alignment = Pos.TOP_CENTER
                            label("Description")
                            add(description)
                        }
                    }
                }
            }
        }
        add(EntityTopMenu(wrapperProperty))
    }


    override fun reasonsNotToSave() : List<String> {
        val messages: MutableList<String> = ArrayList()
        if (nameField.text.removeSpaces().isEmpty() && nameInEditorField.text.removeSpaces().isEmpty()) {
            messages.add("Either one of the fields NAME or NAME IN EDITOR must not be empty")
        }
        return messages
    }
}