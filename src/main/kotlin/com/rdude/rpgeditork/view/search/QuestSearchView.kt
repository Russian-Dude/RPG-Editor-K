package com.rdude.rpgeditork.view.search

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.utils.isPositive
import com.rdude.rpgeditork.utils.trimZeroes
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.VBox
import ru.rdude.fxlib.dialogs.SearchDialog
import ru.rdude.rpg.game.logic.data.QuestData
import tornadofx.*

class QuestSearchView : EntitySearchView<QuestData>() {

    override fun configPopup(searchDialog: SearchDialog<EntityDataWrapper<QuestData>>) {
        searchDialog.searchPane.popupBuilder()
            .addText { it.entityData.description }
            .addText { "Level: ${it.entityData.lvl}" }
            .addText("Requirements:")
            .addText { "Collect gold: ${if (it.entityData.collectGold > 0) it.entityData.collectGold else "-"}" }
            .addText { "Kill monsters: ${it.entityData.killMonsters
                .mapNotNull { (guid, amount) -> "${Data.monsters[guid]?.entityData?.nameInEditor} - $amount" }
                .ifEmpty { listOf("-") }
                .reduce { a, b -> "$a\r\n               $b" }}" }
            .addText { "Collect items: ${it.entityData.collectItems
                .mapNotNull { (guid, amount) -> "${Data.items[guid]?.entityData?.nameInEditor} - $amount" }
                .ifEmpty { listOf("-") }
                .reduce { a, b -> "$a\r\n               $b" }}" }
            .addText { "Use skills: ${it.entityData.useSkills
                .mapNotNull { (guid, amount) -> "${Data.skills[guid]?.entityData?.nameInEditor} - $amount" }
                .ifEmpty { listOf("-") }
                .reduce { a, b -> "$a\r\n               $b" }}" }
            .addText("Rewards:")
            .addText { "Receive gold: ${if (it.entityData.receiveGold > 0) it.entityData.receiveGold else "-"}" }
            .addText { "Receive experience: ${if (it.entityData.expReward > 0) it.entityData.expReward else "-"}" }
            .addText { "Start event: ${it.entityData.startEvent?.let { guid -> Data.events[guid] } ?: "-"}" }
            .addText { "Receive stats: ${it.entityData.receiveStats
                .filter { (_, amount) -> amount > 0 }
                .map { (stat, amount) -> "${stat.getName()}: $amount" }
                .ifEmpty { listOf("-") }
                .reduce { a, b -> "$a\r\n       $b" }}" }
            .addText { "Receive items: ${it.entityData.receiveItems
                .mapNotNull { (guid, amount) -> "${Data.items[guid]?.entityData?.nameInEditor} - $amount" }
                .ifEmpty { listOf("-") }
                .reduce { a, b -> "$a\r\n               $b" }}" }
            .addText { "Learn skills: ${it.entityData.learnSkills
                .mapNotNull { guid -> "${Data.skills[guid]?.entityData?.nameInEditor}" }
                .ifEmpty { listOf("-") }
                .reduce { a, b -> "$a\r\n               $b" }}" }
            .addText { "Start quests: ${it.entityData.useSkills
                .mapNotNull { (guid, amount) -> "${Data.quests[guid]?.entityData?.nameInEditor} - ${amount}" }
                .ifEmpty { listOf("-") }
                .reduce { a, b -> "$a\r\n               $b" }}" }
            .apply()
    }

    override val root = hbox {
        paddingLeft = 15.0
        spacing = 25.0
        alignment = Pos.TOP_CENTER

        vbox {
            spacing = 25.0
            alignment = Pos.TOP_CENTER

            add(resetSearchButton)

            val minLvl = TextField("-")
                .apply {
                    prefWidth = 50.0
                    filterInput { it.controlNewText == "-" || (it.controlNewText.length <= 3 && it.controlNewText.isInt() && it.controlNewText.toInt().isPositive()) }
                    searchOptions.put(this) { if (text.isInt()) it.entityData.lvl >= text.toInt() else true }
                    resetSearchFunctions.add { text = "-" }
                }
            val maxLvl = TextField("-")
                .apply {
                    prefWidth = 50.0
                    filterInput { it.controlNewText == "-" || (it.controlNewText.length <= 3 && it.controlNewText.isInt() && it.controlNewText.toInt().isPositive()) }
                    searchOptions.put(this) { if (text.isInt()) it.entityData.lvl <= text.toInt() else true }
                    resetSearchFunctions.add { text = "-" }
                }
            vbox {
                text("Level")
                hbox {
                    alignment = Pos.CENTER
                    add(minLvl)
                    add(text(" - "))
                    add(maxLvl)
                }
            }

            vbox {
                text("Required to collect gold")
                val toggle = ToggleGroup()
                radiobutton {
                    text = "Not important"
                    isSelected = true
                    toggleGroup = toggle
                    searchOptions.put(this) { true }
                    resetSearchFunctions.add { isSelected = true }
                }
                radiobutton {
                    text = "Yes"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.collectGold > 0 }
                    resetSearchFunctions.add { isSelected = false }
                }
                radiobutton {
                    text = "No"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.collectGold <= 0 }
                    resetSearchFunctions.add { isSelected = false }
                }
            }

            add(threeOptionsFromMap("Required to kill monsters") { it.entityData.killMonsters })
            add(threeOptionsFromMap("Required to collect items") { it.entityData.collectItems })
            add(threeOptionsFromMap("Required to use skills") { it.entityData.useSkills })
        }

        vbox {
            spacing = 25.0
            alignment = Pos.TOP_CENTER

            vbox {
                text("Has gold reward")
                val toggle = ToggleGroup()
                radiobutton {
                    text = "Not important"
                    isSelected = true
                    toggleGroup = toggle
                    searchOptions.put(this) { true }
                    resetSearchFunctions.add { isSelected = true }
                }
                radiobutton {
                    text = "Yes"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.receiveGold > 0 }
                    resetSearchFunctions.add { isSelected = false }
                }
                radiobutton {
                    text = "No"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.receiveGold > 0 }
                    resetSearchFunctions.add { isSelected = false }
                }
            }

            add(threeOptionsFromMap("Has stats reward") { it.entityData.receiveStats })
            add(threeOptionsFromMap("Has items rewards") { it.entityData.receiveItems })

            vbox {
                text("Has skills reward")
                val toggle = ToggleGroup()
                radiobutton {
                    text = "Not important"
                    isSelected = true
                    toggleGroup = toggle
                    searchOptions.put(this) { true }
                    resetSearchFunctions.add { isSelected = true }
                }
                radiobutton {
                    text = "Yes"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.learnSkills != null && it.entityData.learnSkills.isNotEmpty() }
                    resetSearchFunctions.add { isSelected = false }
                }
                radiobutton {
                    text = "No"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.learnSkills.isNullOrEmpty() }
                    resetSearchFunctions.add { isSelected = false }
                }
            }

            vbox {
                text("Start another quest")
                val toggle = ToggleGroup()
                radiobutton {
                    text = "Not important"
                    isSelected = true
                    toggleGroup = toggle
                    searchOptions.put(this) { true }
                    resetSearchFunctions.add { isSelected = true }
                }
                radiobutton {
                    text = "Yes"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.startQuests != null && it.entityData.startQuests.isNotEmpty() }
                    resetSearchFunctions.add { isSelected = false }
                }
                radiobutton {
                    text = "No"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.startQuests.isNullOrEmpty() }
                    resetSearchFunctions.add { isSelected = false }
                }
            }
        }
    }

    private fun threeOptionsFromMap(title: String, map: (EntityDataWrapper<QuestData>) -> Map<*, *>?): VBox = vbox {
        text(title)
        val toggle = ToggleGroup()
        radiobutton {
            text = "Not important"
            isSelected = true
            toggleGroup = toggle
            searchOptions.put(this) { true }
            resetSearchFunctions.add { isSelected = true }
        }
        radiobutton {
            text = "Yes"
            isSelected = false
            toggleGroup = toggle
            searchOptions.put(this) { val m = map.invoke(it); m != null && m.isNotEmpty() }
            resetSearchFunctions.add { isSelected = false }
        }
        radiobutton {
            text = "No"
            isSelected = false
            toggleGroup = toggle
            searchOptions.put(this) { map.invoke(it).isNullOrEmpty() }
            resetSearchFunctions.add { isSelected = false }
        }
    }
}