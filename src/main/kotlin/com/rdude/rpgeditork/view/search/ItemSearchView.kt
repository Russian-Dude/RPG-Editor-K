package com.rdude.rpgeditork.view.search

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.enums.ObservableEnums
import com.rdude.rpgeditork.utils.setNullToStringConverter
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.ToggleGroup
import ru.rdude.fxlib.containers.selector.SelectorContainer
import ru.rdude.fxlib.dialogs.SearchDialog
import ru.rdude.rpg.game.logic.data.ItemData
import ru.rdude.rpg.game.logic.enums.ItemMainType
import ru.rdude.rpg.game.logic.enums.ItemType
import tornadofx.*
import java.util.function.Predicate

class ItemSearchView : EntitySearchView<ItemData>() {

    override fun configPopup(searchDialog: SearchDialog<EntityDataWrapper<ItemData>>) {
        searchDialog.searchPane.popupBuilder()
            .addText { it.entityData.description }
            .addText { "In game name: ${it.entityData.name}" }
            .addText { "Type: ${it.entityData.itemType}" }
            .addText { "Rarity: ${it.entityData.rarity}" }
            .addText { if (it.entityData.isStackable) "Stackable" else "Not stackable" }

            .addText { "Elements: ${it.entityData.elements
                .map { el -> el.toString() }
                .ifEmpty { listOf("-") }
                .reduce { a, b -> "$a, $b" }}" }

            .addText { "Stats: ${it.entityData.stats
                .streamWithNestedStats()
                .filter { st -> st.value() != 0.0 }
                .map { st -> "${st.name}: ${st.value()}" }
                .reduce { a, b -> "$a\r\n       $b" }
                .orElse("-")}" }

            .addText { "Stats requirements: ${it.entityData.requirements
                .streamWithNestedStats()
                .filter { st -> st.value() != 0.0 }
                .map { st -> "${st.name}: ${st.value()}" }
                .reduce { a, b -> "$a\r\n                    $b" }
                .orElse("-")}" }

            .addText { "Skills on equip: ${
                it.entityData.skillsEquip
                    .map { guid -> Data.skills[guid]?.entityNameProperty?.get() }
                    .ifEmpty { listOf("-") }
                    .reduce { a, b -> "$a\r\n                 $b" }
            }" }

            .addText { "Skills on use: ${
                it.entityData.skillsOnUse
                    .map { guid -> Data.skills[guid]?.entityNameProperty?.get() }
                    .ifEmpty { listOf("-") }
                    .reduce { a, b -> "$a\r\n               $b" }
            }" }
            .apply()
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

        var mainType: ComboBox<ItemMainType?>? = null
        vbox {
            text("Main type")
            mainType = combobox(values = ObservableEnums.ITEM_MAIN_TYPES_NULLABLE) {
                maxWidth = Double.MAX_VALUE
                setNullToStringConverter("ANY")
                searchOptions.put(this) { it.entityData.itemType?.mainType }
                resetSearchFunctions.add { value = null }
            }
        }

        vbox {
            text("Type")
            combobox<ItemType?> {
                val predicate = Predicate<ItemType?> { itemType -> mainType?.value == itemType?.mainType || itemType == null }
                val filteredList = FilteredList(ObservableEnums.ITEM_TYPES_NULLABLE, predicate)
                items = filteredList
                mainType?.valueProperty()?.onChange {
                    filteredList.predicate = null
                    filteredList.predicate = predicate
                    if (value == null || value?.mainType != mainType?.value) {
                        value = filteredList.first()
                    }
                }
                maxWidth = Double.MAX_VALUE
                setNullToStringConverter("ANY")
                searchOptions.put(this) { it.entityData.itemType }
                resetSearchFunctions.add { value = null }
            }
        }

        vbox {
            text("Rarity")
            combobox(values = ObservableEnums.ITEM_RARITY_NULLABLE) {
                maxWidth = Double.MAX_VALUE
                setNullToStringConverter("ANY")
                searchOptions.put(this) { it.entityData.rarity }
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
            mainType?.valueProperty()?.onChange { isDisable = it != ItemMainType.WEAPON }
            text("Attack type")
            combobox(values = ObservableEnums.ATTACK_TYPES_NULLABLE) {
                mainType?.valueProperty()?.onChange {
                    if (it != ItemMainType.WEAPON) {
                        value = null
                    }
                }
                maxWidth = Double.MAX_VALUE
                setNullToStringConverter("Any")
                searchOptions.put(this) { it.entityData.weaponData.attackType }
                resetSearchFunctions.add { value = null }
            }
        }
    }
}