package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.enums.EVENT
import com.rdude.rpgeditork.enums.ObservableEnums
import com.rdude.rpgeditork.enums.SKILL
import com.rdude.rpgeditork.utils.isPositive
import com.rdude.rpgeditork.utils.removeSpaces
import com.rdude.rpgeditork.utils.row
import com.rdude.rpgeditork.view.helper.EntityTopMenu
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.ColumnConstraints
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import ru.rdude.fxlib.boxes.SearchComboBox
import ru.rdude.fxlib.containers.selector.SelectorContainer
import ru.rdude.rpg.game.logic.data.*
import tornadofx.*

class QuestView(wrapper: EntityDataWrapper<QuestData>) : EntityView<QuestData>(wrapper) {

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

    val preferredLevel = TextField().apply {
        filterInput { it.controlNewText.isInt() && it.controlNewText.toInt().isPositive() }
        text = entityData.lvl.toString()
        changesChecker.add(this) { text }
        fieldsSaver.add { it.lvl = if (text.isBlank()) 0 else text.toInt() }
    }

    val unique = ComboBox(ObservableEnums.QUEST_UNIQUE).apply {
        value = entityData.unique ?: QuestData.Unique.NO
        changesChecker.add(this) { value }
        fieldsSaver.add { it.unique = value }
    }

    val endPlace = ComboBox(ObservableEnums.QUEST_END_PLACE).apply {
        value = entityData.endQuestPlace ?: QuestData.EndQuestPlace.WHERE_GET
        changesChecker.add(this) { value }
        fieldsSaver.add { it.endQuestPlace = value }
    }

    val killMonsters = SelectorContainer.withTextField(Data.monsters.list)
        .sizePercentages(70.0, 30.0)
        .nameByProperty(EntityDataWrapper<MonsterData>::entityNameProperty)
        .searchBy({ w -> w.entityData.name }, { w -> w.entityData.nameInEditor })
        .addOption { el -> el.textField.filterInput { it.controlNewText.isInt() && it.controlNewText.toInt().isPositive() } }
        .get()
        .apply {
            entityData.killMonsters.forEach { (guid, amount) ->
                add(Data.monsters[guid]).textField.text = amount.toString()
            }
            changesChecker.add(this) {
                selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted()
            }
            fieldsSaver.add {
                entityData.killMonsters.clear()
                selectedElementsNodes.forEach { elementNode ->
                    val textValue = elementNode.textField.text;
                    entityData.killMonsters[elementNode.value.entityData.guid] = if (textValue.isBlank()) 0 else textValue.toInt()
                }
            }
        }

    val killMonstersDescriberToReal = CheckBox("Convert describers to concrete monsters on creation").apply {
        isSelected = entityData.isKillMonstersDescriberToReal
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.isKillMonstersDescriberToReal = isSelected }
    }

    val collectItems = SelectorContainer.withTextField(Data.items.list)
        .sizePercentages(70.0, 30.0)
        .nameByProperty(EntityDataWrapper<ItemData>::entityNameProperty)
        .searchBy({ w -> w.entityData.name }, { w -> w.entityData.nameInEditor })
        .addOption { el -> el.textField.filterInput { it.controlNewText.isInt() && it.controlNewText.toInt().isPositive() } }
        .get()
        .apply {
            entityData.collectItems.forEach { (guid, amount) ->
                add(Data.items[guid]).textField.text = amount.toString()
            }
            changesChecker.add(this) {
                selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted()
            }
            fieldsSaver.add {
                entityData.collectItems.clear()
                selectedElementsNodes.forEach { elementNode ->
                    val textValue = elementNode.textField.text;
                    entityData.collectItems[elementNode.value.entityData.guid] = if (textValue.isBlank()) 0 else textValue.toInt()
                }
            }
        }

    val collectItemsDescriberToReal = CheckBox("Convert describers to concrete items on creation").apply {
        isSelected = entityData.isCollectItemsDescriberToReal
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.isCollectItemsDescriberToReal = isSelected }
    }

    val takeItems = CheckBox("Take items away").apply {
        isSelected = entityData.isTakeItems
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.isTakeItems = isSelected }
    }

    val collectGold = TextField().apply {
        filterInput { it.controlNewText.isInt() && it.controlNewText.toInt().isPositive() }
        text = entityData.collectGold.toString()
        changesChecker.add(this) { text }
        fieldsSaver.add { entityData.collectGold = if (text.isBlank()) 0 else text.toInt() }
    }

    val takeGold = CheckBox("Take gold away").apply {
        isSelected = entityData.isTakeGold
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.isTakeGold = isSelected }
    }

    val useSkills = SelectorContainer.withTextField(Data.skills.list)
        .sizePercentages(70.0, 30.0)
        .nameByProperty(EntityDataWrapper<SkillData>::entityNameProperty)
        .searchBy({ w -> w.entityData.name }, { w -> w.entityData.nameInEditor })
        .addOption { el -> el.textField.filterInput { it.controlNewText.isInt() && it.controlNewText.toInt().isPositive() } }
        .get()
        .apply {
            entityData.useSkills.forEach { (guid, amount) ->
                add(Data.skills[guid]).textField.text = amount.toString()
            }
            changesChecker.add(this) {
                selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted()
            }
            fieldsSaver.add {
                entityData.useSkills.clear()
                selectedElementsNodes.forEach { elementNode ->
                    val textValue = elementNode.textField.text;
                    entityData.useSkills[elementNode.value.entityData.guid] = if (textValue.isBlank()) 0 else textValue.toInt()
                }
            }
        }

    val useSkillsDescriberToReal = CheckBox("Convert describers to concrete skills on creation").apply {
        isSelected = entityData.isUseSkillsDescriberToReal
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.isUseSkillsDescriberToReal = isSelected }
    }

    val learnSkills = SelectorContainer.simple(Data.skills.list)
        .nameByProperty(EntityDataWrapper<SkillData>::entityNameProperty)
        .searchBy({ w -> w.entityData.name }, { w -> w.entityData.nameInEditor })
        .setUnique(true)
        .get()
        .apply {
            entityData.learnSkills.forEach { add(Data.skills[it]) }
            changesChecker.add(this) { selected.sorted() }
            fieldsSaver.add {
                it.learnSkills.clear()
                selected.forEach { selected -> it.learnSkills.add(selected.entityData.guid) }
            }
        }

    val learnSkillsRewardTarget = ComboBox(ObservableEnums.QUEST_REWARD_TARGET).apply {
        value = entityData.learnSkillsRewardTarget ?: QuestData.RewardTarget.ALL
        changesChecker.add(this) { value }
        fieldsSaver.add { it.learnSkillsRewardTarget = value }
    }

    val receiveItems = SelectorContainer.withTextField(Data.items.list)
        .sizePercentages(80.0, 20.0)
        .nameByProperty(EntityDataWrapper<ItemData>::entityNameProperty)
        .searchBy({ w -> w.entityData.name }, { w -> w.entityData.nameInEditor })
        .addOption { el -> el.textField.filterInput { it.controlNewText.isInt() && it.controlNewText.toInt().isPositive() } }
        .get()
        .apply {
            entityData.receiveItems.forEach { (guid, amount) ->
                add(Data.items[guid]).textField.text = amount.toString()
            }
            changesChecker.add(this) {
                selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted()
            }
            fieldsSaver.add {
                entityData.receiveItems.clear()
                selectedElementsNodes.forEach { elementNode ->
                    val textValue = elementNode.textField.text;
                    entityData.receiveItems[elementNode.value.entityData.guid] = if (textValue.isBlank()) 0 else textValue.toInt()
                }
            }
        }

    val receiveStats = SelectorContainer.withTextField(ObservableEnums.STAT_NAMES)
        .sizePercentages(80.0, 20.0)
        .addOption { el -> el.textField.filterInput { it.controlNewText.isDouble() } }
        .get()
        .apply {
            entityData.receiveStats.forEach { (statName, amount) ->
                add(statName).textField.text = amount.toString()
            }
            changesChecker.add(this) {
                selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted()
            }
            fieldsSaver.add {
                entityData.receiveStats.clear()
                selectedElementsNodes.forEach { elementNode ->
                    val textValue = elementNode.textField.text;
                    if (textValue.isNotBlank() && textValue.isDouble()) {
                        entityData.receiveStats[elementNode.value] = textValue.toDouble()
                    }
                }
            }
        }

    val receiveStatsRewardTarget = ComboBox(ObservableEnums.QUEST_REWARD_TARGET).apply {
        value = entityData.receiveStatsRewardTarget ?: QuestData.RewardTarget.ALL
        changesChecker.add(this) { value }
        fieldsSaver.add { it.receiveStatsRewardTarget = value }
    }

    val receiveGold = TextField().apply {
        filterInput { it.controlNewText.isInt() && it.controlNewText.toInt().isPositive() }
        text = entityData.receiveGold.toString()
        changesChecker.add(this) { text }
        fieldsSaver.add { entityData.receiveGold = if (text.isBlank()) 0 else text.toInt() }
    }

    val receiveExp = TextField().apply {
        filterInput { it.controlNewText.isInt() && it.controlNewText.toInt().isPositive() }
        text = entityData.expReward.toString()
        changesChecker.add(this) { text }
        fieldsSaver.add { entityData.expReward = if (text.isBlank()) 0 else text.toInt() }
    }

    val startQuests = SelectorContainer.simple(Data.quests.list)
        .nameByProperty(EntityDataWrapper<QuestData>::entityNameProperty)
        .searchBy({ w -> w.entityData.name }, { w -> w.entityData.nameInEditor })
        .get()
        .apply {
            entityData.startQuests.forEach { add(Data.quests[it]) }
            changesChecker.add(this) { selected.sorted() }
            fieldsSaver.add {
                it.startQuests.clear()
                selected.forEach { selected -> it.startQuests.add(selected.entityData.guid) }
            }
        }

    val startEvent = SearchComboBox(Data.events.list).apply {
        setNameByProperty(EntityDataWrapper<EventData>::entityNameProperty)
        setSearchBy({ w -> w.entityData.name }, { w -> w.entityData.nameInEditor })
        value = entityData.startEvent?.let { Data.events[it] }
        changesChecker.add(this) { value }
        fieldsSaver.add { it.startEvent = value?.entityData?.guid }
    }

    val startEventSearchButton = Button("\uD83D\uDD0D").apply {
        action {
            EVENT.defaultSearchDialog.showAndWait().ifPresent {
                startEvent.value = it
            }
        }
    }

    val clearStartEventButton = Button("X").apply {
        action {
            startEvent.value = null
        }
    }

    override val root = anchorpane {
        tabpane {
            fitToParentSize()
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("Logic") {
                hbox {
                    paddingAll = 10.0
                    spacing = 50.0
                    vbox {
                        spacing = 10.0
                        alignment = Pos.TOP_CENTER
                        text("Main") {
                            font = Font.font(16.0)
                        }
                        gridpane {
                            hgap = 5.0
                            vgap = 5.0
                            row("Name", nameField)
                            row("Name in editor", nameInEditorField)
                            row("Preferred level", preferredLevel)
                            row("Complete quest", endPlace)
                            row("Unique", unique)
                        }
                        text("Description")
                        add(description).apply {
                            minWidth = 245.0
                            maxWidth = 245.0
                        }
                    }
                    vbox {
                        spacing = 40.0
                        alignment = Pos.TOP_CENTER
                        vbox {
                            spacing = 10.0
                            alignment = Pos.TOP_CENTER
                            text("Requirements") {
                                font = Font.font(16.0)
                            }
                            hbox {
                                alignment = Pos.CENTER
                                spacing = 5.0
                                text("Collect gold")
                                add(collectGold.apply {
                                    minWidth = 60.0
                                    maxWidth = 60.0
                                })
                                add(takeGold)
                            }
                            gridpane {
                                hgap = 10.0
                                vgap = 5.0
                                alignment = Pos.TOP_CENTER
                                this.columnConstraints.addAll(
                                    ColumnConstraints(250.0).apply { halignment = HPos.CENTER },
                                    ColumnConstraints(250.0).apply { halignment = HPos.CENTER },
                                    ColumnConstraints(250.0).apply { halignment = HPos.CENTER }
                                )
                                row {
                                    text("Kill monsters")
                                    text("Collect items")
                                    text("Use skills")
                                }
                                row {
                                    add(killMonsters.apply {
                                        prefHeight = 100.0
                                    })
                                    add(collectItems.apply {
                                        prefHeight = 100.0
                                    })
                                    add(useSkills.apply {
                                        prefHeight = 100.0
                                    })
                                }
                                row {
                                    add(killMonstersDescriberToReal)
                                    add(collectItemsDescriberToReal)
                                    add(useSkillsDescriberToReal)
                                }
                                row {
                                    text("")
                                    add(takeItems)
                                }
                            }
                        }
                        vbox {
                            spacing = 10.0
                            alignment = Pos.TOP_CENTER
                            text("Rewards") {
                                font = Font.font(16.0)
                                paddingTop = 10.0
                            }
                            hbox {
                                spacing = 15.0
                                alignment = Pos.CENTER
                                hbox {
                                    spacing = 5.0
                                    alignment = Pos.CENTER
                                    text("Receive gold")
                                    add(receiveGold.apply {
                                        minWidth = 60.0
                                        maxWidth = 60.0
                                    })
                                }
                                hbox {
                                    spacing = 5.0
                                    alignment = Pos.CENTER
                                    text("Receive experience")
                                    add(receiveExp.apply {
                                        minWidth = 60.0
                                        maxWidth = 60.0
                                    })
                                }
                                hbox {
                                    spacing = 5.0
                                    alignment = Pos.CENTER
                                    text("Start event")
                                    add(startEvent.apply {
                                        minWidth = 150.0
                                        maxWidth = 150.0
                                    })
                                    add(startEventSearchButton.apply {
                                        minWidth = 40.0
                                        maxWidth = 40.0
                                    })
                                    add(clearStartEventButton)
                                }

                            }
                            gridpane {
                                hgap = 10.0
                                vgap = 5.0
                                this.columnConstraints.addAll(
                                    ColumnConstraints(250.0).apply { halignment = HPos.CENTER },
                                    ColumnConstraints(250.0).apply { halignment = HPos.CENTER },
                                    ColumnConstraints(250.0).apply { halignment = HPos.CENTER },
                                    ColumnConstraints(250.0).apply { halignment = HPos.CENTER }
                                )
                                row {
                                    text("Stats")
                                    text("Items")
                                    text("Skills")
                                    text("Start quests")
                                }
                                row {
                                    add(receiveStats.apply {
                                        prefHeight = 100.0
                                    })
                                    add(receiveItems.apply {
                                        prefHeight = 100.0
                                    })
                                    add(learnSkills.apply {
                                        prefHeight = 100.0
                                    })
                                    add(startQuests.apply {
                                        prefHeight = 100.0
                                    })
                                }
                                row {
                                    hbox {
                                        alignment = Pos.CENTER
                                        spacing = 5.0
                                        text("Reward target")
                                        add(receiveStatsRewardTarget)
                                    }
                                    text("")
                                    hbox {
                                        alignment = Pos.CENTER
                                        spacing = 5.0
                                        text("Reward target")
                                        add(learnSkillsRewardTarget)
                                    }
                                }
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