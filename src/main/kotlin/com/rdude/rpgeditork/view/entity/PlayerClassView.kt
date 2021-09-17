package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.enums.StatisticType
import com.rdude.rpgeditork.utils.removeSpaces
import com.rdude.rpgeditork.utils.row
import com.rdude.rpgeditork.view.helper.AbilitiesTable
import com.rdude.rpgeditork.view.helper.EntityTopMenu
import com.rdude.rpgeditork.view.helper.PlayerClassRequirementSelectorElement
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.scene.text.Font
import javafx.scene.text.Text
import ru.rdude.fxlib.containers.elementsholder.ElementsHolder
import ru.rdude.rpg.game.logic.data.AbilityData
import ru.rdude.rpg.game.logic.data.PlayerClassData
import ru.rdude.rpg.game.logic.playerClass.AbilityPath
import tornadofx.*

class PlayerClassView(wrapper: EntityDataWrapper<PlayerClassData>) : EntityView<PlayerClassData>(wrapper) {

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

    val description = textarea {
        isWrapText = true
        text = entityData.description
        changesChecker.add(this) { text }
        fieldsSaver.add { it.description = text }
    }

    val preferredConnectionsType = ComboBox(mutableListOf("vertical", "horizontal").toObservable()).apply {
        value = "vertical"
        valueProperty().onChange {
            if (it == "horizontal") {
                abilitiesTable.preferHorizontalConnections()
            }
            else if (it == "vertical") {
                abilitiesTable.preferVerticalConnections()
            }
        }
    }

    val abilitiesTable = AbilitiesTable(entityData).apply {
        changesChecker.add(this) { it.getCells() }
        fieldsSaver.add {
            entityData.abilityEntries.clear()
            entityData.resources.cells.clear()
            for (x in 0 until entityData.resources.cells.cells.size) {
                for (y in 0 until entityData.resources.cells.cells[x].size) {
                    val cell = getCell(x, y)
                    if (cell.content is AbilityPath) {
                        entityData.resources.cells.setCell(x, y, cell.content as AbilityPath)
                    }
                    else if (cell.content is AbilityData) {
                        val abilityEntry = PlayerClassData.AbilityEntry(cell.contentWrapper!!.entityData.guid)
                        abilityEntry.requirements = cell.requirements.mapKeys { it.key.contentWrapper!!.entityData.guid }
                        entityData.abilityEntries.add(abilityEntry)
                        entityData.resources.cells.setCell(x, y, cell.content as AbilityData)
                    }
                }
            }
        }
    }

    val requiredPoints = TextField().apply {
        filterInput { it.controlNewText.isInt() && it.controlNewText.toInt() >= 0 }
        text = entityData.requiredPoints.toString()
        changesChecker.add(this) { text }
        fieldsSaver.add { entityData.requiredPoints = if (text.isNotBlank()) text.toLong() else 0 }
    }

    val requirements = ElementsHolder { PlayerClassRequirementSelectorElement() }.apply {
        entityData.openRequirements.forEach {
            val element = add()
            element.beingAction = it.beingAction
            element.type = StatisticType.typeOf(it.statisticType) ?: StatisticType.ATTACK_TYPE
            element.concreteType = it.statisticType
            element.pointsForEachUse = it.pointsForEachUse.toInt()
            element.pointsForValue = it.pointsForValue.toInt()
        }
        changesChecker.add(this) {
            elements.map { listOf(it.beingAction.toString(), it.concreteType.toString(), it.pointsForEachUse.toString(), it.pointsForValue.toString()) }
        }
        fieldsSaver.add {
            entityData.openRequirements.clear()
            elements.forEach {
                entityData.openRequirements.add(PlayerClassData.PlayerClassOpenRequirement(it.beingAction, it.concreteType, it.pointsForValue.toDouble(), it.pointsForEachUse.toDouble()))
            }
        }
    }

    override val root = anchorpane {
        tabpane {
            fitToParentSize()
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("Logic") {
                hbox {
                    paddingAll = 10.0
                    spacing = 25.0
                    vbox {
                        paddingAll = 10.0
                        spacing = 25.0
                        gridpane {
                            hgap = 5.0
                            vgap = 5.0
                            constraintsForColumn(1).maxWidth = 145.0
                            row("Name", nameField)
                            row("Name in editor", nameInEditorField)
                        }
                        add(text("Abilities tree").apply { font = Font.font(16.0) })
                        add(abilitiesTable)
                        hbox {
                            paddingAll = 5.0
                            spacing = 10.0
                            alignment = Pos.CENTER_LEFT
                            text("Preferred connections")
                            add(preferredConnectionsType)
                        }
                    }
                    vbox {
                        alignment = Pos.TOP_CENTER
                        spacing = 10.0
                        add(text("Requirements").apply { font = Font.font(16.0) })
                        hbox {
                            alignment = Pos.CENTER
                            spacing = 10.0
                            add(Text("Points"))
                            add(requiredPoints.apply { maxWidth = 100.0 })
                        }
                        add(requirements.apply { minWidth = 902.0; prefHeight = 500.0 })
                    }

                }
            }
            tab("Visual and sound") {
                hbox {
                    paddingAll = 10.0
                    vbox {
                        spacing = 20.0
                        alignment = Pos.TOP_CENTER
                        text("Main") {
                            font = Font.font(16.0)
                        }
                        hbox {
                            spacing = 5.0
                            vbox {
                                spacing = 5.0
                                alignment = Pos.TOP_CENTER
                                text("Description")
                                add(description)
                            }
                        }
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