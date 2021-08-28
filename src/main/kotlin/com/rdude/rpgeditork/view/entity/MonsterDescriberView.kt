package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.enums.ObservableEnums
import com.rdude.rpgeditork.enums.nullableVersion
import com.rdude.rpgeditork.utils.isPositive
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
import ru.rdude.rpg.game.logic.data.MonsterData
import ru.rdude.rpg.game.logic.enums.EntityReferenceInfo
import tornadofx.*

class MonsterDescriberView(wrapper: EntityDataWrapper<MonsterData>) : EntityView<MonsterData>(wrapper) {

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

    val elements = SelectorContainer.simple(ObservableEnums.ELEMENTS).get().apply {
        setHasSearchButton(false)
        addAll(entityData.elements)
        changesChecker.add(this) { selected.sorted() }
        fieldsSaver.add { it.elements = selected.toHashSet() }
    }

    val beingTypes = SelectorContainer.simple(ObservableEnums.BEING_TYPES).get().apply {
        setHasSearchButton(false)
        addAll(entityData.beingTypes)
        changesChecker.add(this) { selected.sorted() }
        fieldsSaver.add { it.beingTypes = selected.toHashSet() }
    }

    val size = combobox(values = ObservableEnums.SIZES_WITH_NULL) {
        setNullToStringConverter("Any")
        value = entityData.size?.nullableVersion()
        changesChecker.add(this) { value }
        fieldsSaver.add { it.size = value.size }
    }

    val attackType = combobox(values = ObservableEnums.ATTACK_WITHOUT_WEAPON_TYPE_NULLABLE) {
        setNullToStringConverter("Any")
        value = entityData.defaultAttackType
        changesChecker.add(this) { value }
        fieldsSaver.add { it.defaultAttackType = value }
    }

    val minLvl = textfield {
        filterInput {
            it.controlNewText.length <= 3 && it.controlNewText.isInt() && it.controlNewText.toInt().isPositive()
        }
        alignment = Pos.CENTER
        text = entityData.minLvl.toInt().toString()
        changesChecker.add(this) { text }
        fieldsSaver.add { it.minLvl = text.toDouble() }
    }

    val maxLvl = textfield {
        filterInput {
            it.controlNewText.length <= 3 && it.controlNewText.isInt() && it.controlNewText.toInt().isPositive()
        }
        alignment = Pos.CENTER
        text = entityData.maxLvl.toInt().toString()
        changesChecker.add(this) { text }
        fieldsSaver.add { it.maxLvl = text.toDouble() }
    }

    val spawnBioms = SelectorContainer.simple(ObservableEnums.BIOMS).get().apply {
        addAll(entityData.spawnBioms)
        setHasSearchButton(false)
        changesChecker.add(this) { selected.sorted() }
        fieldsSaver.add { it.spawnBioms = selected.toHashSet() }
    }

    val spawnReliefs = SelectorContainer.simple(ObservableEnums.RELIEFS).get().apply {
        addAll(entityData.spawnReliefs)
        setHasSearchButton(false)
        changesChecker.add(this) { selected.sorted() }
        fieldsSaver.add { it.spawnReliefs = selected.toHashSet() }
    }

    val monsterInfo = ComboBox(ObservableEnums.ENTITY_INFO).apply {
        value = entityData.entityInfo ?: EntityReferenceInfo.ALL
        changesChecker.add(this) { value }
        fieldsSaver.add { it.entityInfo = value }
    }

    val referenceInfo = ComboBox(ObservableEnums.ENTITY_REFERENCE_INFO).apply {
        value = entityData.entityReferenceInfo ?: EntityReferenceInfo.NAME
        changesChecker.add(this) { value }
        fieldsSaver.add { it.entityReferenceInfo = value }
    }

    val description = textarea {
        isWrapText = true
        text = entityData.description
        changesChecker.add(this) { text }
        fieldsSaver.add { it.description = text }
    }

    override val root = anchorpane {
        tabpane {
            fitToParentSize()
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("Logic") {
                hbox {
                    paddingAll = 10.0
                    spacing = 25.0
                    scrollpane {
                        isFitToHeight = true
                        vbox {
                            alignment = Pos.TOP_CENTER
                            spacing = 5.0
                            paddingAll = 5.0
                            gridpane {
                                hgap = 5.0
                                vgap = 10.0
                                constraintsForColumn(1).maxWidth = 145.0
                                row("Name", nameField)
                                row("Name in editor", nameInEditorField)
                                row {
                                    text("Levels")
                                    hbox {
                                        spacing = 5.0
                                        alignment = Pos.CENTER_LEFT
                                        vbox {
                                            spacing = 3.0
                                            alignment = Pos.CENTER
                                            text("Min")
                                            add(minLvl.apply { prefWidth = 50.0 })
                                        }
                                        vbox {
                                            spacing = 3.0
                                            alignment = Pos.CENTER
                                            text("Max")
                                            add(maxLvl.apply { prefWidth = 50.0 })
                                        }
                                    }
                                }
                                row("Attack type", attackType)
                                row("Size", size)
                                row("Elements", elements.apply { prefHeight = 80.0 })
                                row("Types", beingTypes.apply { prefHeight = 80.0 })
                                row("Spawn biomes", spawnBioms.apply { prefHeight = 80.0 })
                                row("Spawn reliefs", spawnReliefs.apply { prefHeight = 80.0 })
                                row("Monster info", monsterInfo)
                                row("References info", referenceInfo)
                            }
                        }
                    }
                    vbox {
                        spacing = 25.0
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
        if (minLvl.text.isBlank()) {
            messages.add("Minimum level field is empty")
        }
        if (maxLvl.text.isBlank()) {
            messages.add("Maximum level field is empty")
        }
        if (minLvl.text.isNotBlank() && maxLvl.text.isNotBlank()) {
            val minLvl = minLvl.text.toInt()
            val maxLvl = maxLvl.text.toInt()
            if (minLvl > maxLvl) {
                messages.add("Maximum level must be greater or equals than minimum level")
            }
        }
        return messages
    }
}