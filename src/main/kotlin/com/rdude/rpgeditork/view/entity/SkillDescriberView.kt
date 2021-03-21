package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.enums.ObservableEnums
import com.rdude.rpgeditork.utils.removeSpaces
import com.rdude.rpgeditork.utils.row
import com.rdude.rpgeditork.view.helper.EntityTopMenu
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import ru.rdude.fxlib.containers.selector.SelectorContainer
import ru.rdude.rpg.game.logic.data.SkillData
import ru.rdude.rpg.game.logic.enums.AttackType
import ru.rdude.rpg.game.logic.enums.BuffType
import ru.rdude.rpg.game.logic.enums.SkillEffect
import ru.rdude.rpg.game.logic.enums.SkillType
import tornadofx.*

class SkillDescriberView (wrapper: EntityDataWrapper<SkillData>) : EntityView<SkillData>(wrapper) {

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

    val attackType = ComboBox(ObservableEnums.ATTACK_TYPES).apply {
        value = entityData.attackType ?: AttackType.MELEE
        changesChecker.add(this) { value }
        fieldsSaver.add { it.attackType = value }
    }

    val skillType = ComboBox(ObservableEnums.SKILL_TYPES).apply {
        value = entityData.type ?: SkillType.NO_TYPE
        changesChecker.add(this) { value }
        fieldsSaver.add { it.type = value }
    }

    val elements = SelectorContainer.simple(ObservableEnums.ELEMENTS).get().apply {
        setHasSearchButton(false)
        addAll(entityData.elements)
        changesChecker.add(this) { selected.sorted() }
        fieldsSaver.add { it.elements = selected.toHashSet() }
    }

    val effectField = ComboBox(ObservableEnums.SKILL_EFFECTS).apply {
        value = entityData.effect ?: SkillEffect.NO
        changesChecker.add(this) { value }
        fieldsSaver.add { it.effect = value }
    }

    val buffType = ComboBox(ObservableEnums.BUFF_TYPES).apply {
        value = entityData.buffType ?: BuffType.PHYSIC
        changesChecker.add(this) { value }
        fieldsSaver.add { it.buffType = value }
    }

    val description = textarea {
        text = entityData.description
        changesChecker.add(this) { text }
        fieldsSaver.add { it.description = text }
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