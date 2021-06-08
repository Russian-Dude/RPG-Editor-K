package com.rdude.rpgeditork.view.search

import com.rdude.rpgeditork.enums.ObservableEnums
import com.rdude.rpgeditork.utils.isPositive
import com.rdude.rpgeditork.utils.setNullToStringConverter
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.geometry.Pos
import javafx.scene.control.TextField
import ru.rdude.fxlib.containers.selector.SelectorContainer
import ru.rdude.fxlib.dialogs.SearchDialog
import ru.rdude.fxlib.panes.SearchPane
import ru.rdude.rpg.game.logic.data.MonsterData
import ru.rdude.rpg.game.logic.enums.Biom
import ru.rdude.rpg.game.logic.enums.Relief
import tornadofx.*

class MonsterSearchView : EntitySearchView<MonsterData>() {

    override fun configPopup(searchDialog: SearchDialog<EntityDataWrapper<MonsterData>>) {
        searchDialog.searchPane.popupBuilder()
            .addText { it.entityData.description }
            .addText { "Levels: ${it.entityData.minLvl} - ${it.entityData.maxLvl}" }
            .addText { "In game name: ${it.entityData.name}" }

            .addText { "Spawn biomes: " +
                    if (it.entityData.spawnBioms.containsAll(Biom.values().asList())) "all"
                    else it.entityData.spawnBioms
                        .map { biome -> biome.toString() }
                        .reduce{ a, b -> "$a, $b" }
            }

            .addText { "Spawn reliefs: " +
                    if (it.entityData.spawnReliefs.containsAll(Relief.values().asList())) "all"
                    else it.entityData.spawnReliefs
                        .map { relief -> relief.toString() }
                        .reduce{ a, b -> "$a, $b" }
            }

            .addText { "Size: ${it.entityData.size}" }

            .addText { "Elements: ${it.entityData.elements
                .map { el -> el.toString() }
                .ifEmpty { listOf("-") }
                .reduce { a, b -> "$a, $b" }}" }

            .addText { "Types: ${it.entityData.beingTypes
                .map { el -> el.toString() }
                .ifEmpty { listOf("-") }
                .reduce { a, b -> "$a, $b" }}" }
    }

    override val root = vbox {
        spacing = 15.0
        alignment = Pos.TOP_CENTER
        add(resetSearchButton)

        combobox(values = listOf(null, "Describer", "Regular")) {
            maxWidth = Double.MAX_VALUE
            setNullToStringConverter("Any")
            searchOptions.put(this) { if (it.entityData.isDescriber) "Describer" else "Regular" }
            resetSearchFunctions.add { value = null }
        }

        val minLvl = TextField("-")
            .apply {
                prefWidth = 50.0
                filterInput { it.controlNewText == "-" || (it.controlNewText.length <= 3 && it.controlNewText.isInt() && it.controlNewText.toInt().isPositive()) }
                searchOptions.put(this) { if (text.isInt()) it.entityData.mainLvl >= text.toInt() else true }
                resetSearchFunctions.add { text = "-" }
            }
        val maxLvl = TextField("-")
            .apply {
                prefWidth = 50.0
                filterInput { it.controlNewText == "-" || (it.controlNewText.length <= 3 && it.controlNewText.isInt() && it.controlNewText.toInt().isPositive()) }
                searchOptions.put(this) { if (text.isInt()) it.entityData.mainLvl <= text.toInt() else true }
                resetSearchFunctions.add { text = "-" }
            }
        vbox {
            text("Levels")
            hbox {
                alignment = Pos.CENTER
                add(minLvl)
                add(text(" - "))
                add(maxLvl)
            }
        }

        vbox {
            text("Spawn biomes")
            val selectorContainer = SelectorContainer.simple(ObservableEnums.BIOMS).get().apply {
                setHasSearchButton(false)
                prefHeight = 80.0
                searchOptions.put(this) { it.entityData.spawnBioms }
                resetSearchFunctions.add { clear() }
            }
            add(selectorContainer)
        }

        vbox {
            text("Spawn reliefs")
            val selectorContainer = SelectorContainer.simple(ObservableEnums.RELIEFS).get().apply {
                setHasSearchButton(false)
                prefHeight = 80.0
                searchOptions.put(this) { it.entityData.spawnReliefs }
                resetSearchFunctions.add { clear() }
            }
            add(selectorContainer)
        }

        vbox {
            text("Size")
            combobox(values = ObservableEnums.SIZES_NULLABLE) {
                maxWidth = Double.MAX_VALUE
                setNullToStringConverter("ANY")
                searchOptions.put(this) { it.entityData.size }
                resetSearchFunctions.add { value = null }
            }
        }

        vbox {
            text("Elements")
            val selectorContainer = SelectorContainer.simple(ObservableEnums.ELEMENTS).get().apply {
                setHasSearchButton(false)
                prefHeight = 80.0
                searchOptions.put(this) { it.entityData.elements }
                resetSearchFunctions.add { clear() }
            }
            add(selectorContainer)
        }

        vbox {
            text("Types")
            val selectorContainer = SelectorContainer.simple(ObservableEnums.BEING_TYPES).get().apply {
                setHasSearchButton(false)
                prefHeight = 80.0
                searchOptions.put(this) { it.entityData.beingTypes }
                resetSearchFunctions.add { clear() }
            }
            add(selectorContainer)
        }
    }
}