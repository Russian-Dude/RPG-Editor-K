package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.enums.FormulaVariable
import com.rdude.rpgeditork.enums.ObservableEnums
import com.rdude.rpgeditork.enums.SKILL
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.collections.transformation.FilteredList
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import ru.rdude.fxlib.boxes.SearchComboBox
import ru.rdude.fxlib.containers.selector.SelectorContainer
import ru.rdude.fxlib.containers.selector.SelectorElementAutocompletionTextField
import ru.rdude.fxlib.textfields.AutocompletionTextField
import ru.rdude.rpg.game.logic.coefficients.Coefficients
import ru.rdude.rpg.game.logic.data.ItemData
import ru.rdude.rpg.game.logic.enums.*
import tornadofx.*
import java.util.function.Predicate

class ItemView(wrapper: EntityDataWrapper<ItemData>) : EntityView<ItemData>(wrapper) {

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

    val mainType = ComboBox(ObservableEnums.ITEM_MAIN_TYPES).apply {
        value = entityData.itemType.mainType ?: ItemMainType.SIMPLE
        changesChecker.add(this) { value }
    }

    val type = ComboBox<ItemType>().apply {
        // config
        val predicate = Predicate<ItemType> { itemType -> mainType.value == itemType.mainType }
        val filteredList = FilteredList(ObservableEnums.ITEM_TYPES, predicate)
        items = filteredList
        mainType.valueProperty().onChange {
            filteredList.predicate = predicate
            if (value == null || value.mainType != mainType.value) {
                value = filteredList.first()
            }
        }
        // load, save
        value = entityData.itemType ?: ItemType.STONE
        changesChecker.add(this) { value }
        fieldsSaver.add { it.itemType = value }
    }

    val stackable = CheckBox("Stackable").apply {
        isSelected = entityData.isStackable
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.isStackable = isSelected }
    }

    val rarity = ComboBox(ObservableEnums.ITEM_RARITY).apply {
        value = entityData.rarity ?: ItemRarity.BRONZE
        changesChecker.add(this) { value }
        fieldsSaver.add { it.rarity = value }
    }

    val price = textfield {
        filterInput { it.controlNewText.isInt() }
        text = entityData.price.toInt().toString()
        changesChecker.add(this) { text }
        fieldsSaver.add { it.price = text.toDouble() }
    }

    val elements = SelectorContainer.simple(ObservableEnums.ELEMENTS).get().apply {
        setHasSearchButton(false)
        addAll(entityData.elements)
        changesChecker.add(this) { selected.sorted() }
        fieldsSaver.add { it.elements = selected.toHashSet() }
    }

    val stats = SelectorContainer.withTextField(ObservableEnums.STAT_NAMES)
        .disableSearch()
        .sizePercentages(70.0, 30.0)
        .nameBy { s -> s.name }
        .searchBy({ s -> s.variableName }, { s -> s.name })
        .addOption { tf -> tf.textField.filterInput { it.controlNewText.isInt() } }
        .get()
        .apply {
            entityData.stats.forEachWithNestedStats {
                if (it.value() != 0.0) {
                    add(StatName.get(it.javaClass)).textField.text = it.value().toInt().toString()
                }
            }
            changesChecker.add(this) {
                selected.sorted() to selectedElementsNodes.map { e -> e.textField.text }.sorted()
            }
            fieldsSaver.add { entity ->
                entity.stats.forEachWithNestedStats { it.set(0.0) }
                selectedElementsNodes.forEach {
                    entity.stats[it.value.clazz].set(it.textField.text.toDouble())
                }
            }
        }

    val statsRequirements = SelectorContainer.withTextField(ObservableEnums.STAT_NAMES).get().apply {
        setHasSearchButton(false)
        addOption { e -> e.textField.filterInput { it.controlNewText.isInt() } }
        addOption { e -> e.setSizePercentages(75.0, 25.0) }
        entityData.requirements.forEachWithNestedStats {
            if (it.value() != 0.0) {
                add(StatName.get(it.javaClass)).textField.text = it.value().toInt().toString()
            }
        }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { e -> e.textField.text }.sorted() }
        fieldsSaver.add { entity ->
            entity.requirements.forEachWithNestedStats { it.set(0.0) }
            selectedElementsNodes.forEach {
                entity.requirements[it.value.clazz].set(it.textField.text.toDouble())
            }
        }
    }

    val skillsOnUse = SelectorContainer.simple(Data.skillsList)
        .nameByProperty { w -> w.entityNameProperty }
        .searchBy({ w -> w.entityNameProperty.get() }, { w -> w.entityData.name })
        .get()
        .apply {
            SKILL.configSearchDialog(searchDialog)
            entityData.skillsOnUse.forEach { add(Data.skillsMap[it]) }
            changesChecker.add(this) { selected.sorted() }
            fieldsSaver.add { entityData.skillsOnUse = selected.map { w -> w.entityData.guid } }
        }

    val skillsEquip = SelectorContainer.simple(Data.skillsList)
        .nameByProperty { w -> w.entityNameProperty }
        .searchBy({ w -> w.entityNameProperty.get() }, { w -> w.entityData.name })
        .get()
        .apply {
            SKILL.configSearchDialog(searchDialog)
            entityData.skillsEquip.forEach { add(Data.skillsMap[it]) }
            changesChecker.add(this) { selected.sorted() }
            fieldsSaver.add { entityData.skillsEquip = selected.map { w -> w.entityData.guid } }
        }

    val buffAttackTypeAtk = SelectorContainer.withPercents(ObservableEnums.ATTACK_TYPES).get().apply {
        setHasSearchButton(false)
        entityData.coefficients?.atk()
            ?.attackType()?.coefficientsMap?.forEach { if (it.value != 1.0) add(it.key).setPercentsAsCoefficient(it.value) }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted() }
        fieldsSaver.add { entity ->
            if (selected.isEmpty()) return@add
            if (entity.coefficients == null) entity.coefficients = Coefficients()
            else AttackType.values().forEach { entity.coefficients.atk().attackType().set(it, 1.0) }
            selectedElementsNodes.forEach { entity.coefficients.atk().attackType().set(it.value, it.coefficient) }
        }
    }

    val buffAttackTypeDef = SelectorContainer.withPercents(ObservableEnums.ATTACK_TYPES).get().apply {
        setHasSearchButton(false)
        entityData.coefficients?.def()
            ?.attackType()?.coefficientsMap?.forEach { if (it.value != 1.0) add(it.key).setPercentsAsCoefficient(it.value) }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted() }
        fieldsSaver.add { entity ->
            if (selected.isEmpty()) return@add
            if (entity.coefficients == null) entity.coefficients = Coefficients()
            else AttackType.values().forEach { entity.coefficients.def().attackType().set(it, 1.0) }
            selectedElementsNodes.forEach { entity.coefficients.def().attackType().set(it.value, it.coefficient) }
        }
    }

    val buffBeingTypeAtk = SelectorContainer.withPercents(ObservableEnums.BEING_TYPES).get().apply {
        setHasSearchButton(false)
        entityData.coefficients?.atk()
            ?.beingType()?.coefficientsMap?.forEach { if (it.value != 1.0) add(it.key).setPercentsAsCoefficient(it.value) }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted() }
        fieldsSaver.add { entity ->
            if (selected.isEmpty()) return@add
            if (entity.coefficients == null) entity.coefficients = Coefficients()
            else BeingType.values().forEach { entity.coefficients.atk().beingType().set(it, 1.0) }
            selectedElementsNodes.forEach { entity.coefficients.atk().beingType().set(it.value, it.coefficient) }
        }
    }

    val buffBeingTypeDef = SelectorContainer.withPercents(ObservableEnums.BEING_TYPES).get().apply {
        setHasSearchButton(false)
        entityData.coefficients?.def()
            ?.beingType()?.coefficientsMap?.forEach { if (it.value != 1.0) add(it.key).setPercentsAsCoefficient(it.value) }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted() }
        fieldsSaver.add { entity ->
            if (selected.isEmpty()) return@add
            if (entity.coefficients == null) entity.coefficients = Coefficients()
            else BeingType.values().forEach { entity.coefficients.def().beingType().set(it, 1.0) }
            selectedElementsNodes.forEach { entity.coefficients.def().beingType().set(it.value, it.coefficient) }
        }
    }

    val buffElementAtk = SelectorContainer.withPercents(ObservableEnums.ELEMENTS).get().apply {
        setHasSearchButton(false)
        entityData.coefficients?.atk()
            ?.element()?.coefficientsMap?.forEach { if (it.value != 1.0) add(it.key).setPercentsAsCoefficient(it.value) }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted() }
        fieldsSaver.add { entity ->
            if (selected.isEmpty()) return@add
            if (entity.coefficients == null) entity.coefficients = Coefficients()
            else Element.values().forEach { entity.coefficients.atk().element().set(it, 1.0) }
            selectedElementsNodes.forEach { entity.coefficients.atk().element().set(it.value, it.coefficient) }
        }
    }

    val buffElementDef = SelectorContainer.withPercents(ObservableEnums.ELEMENTS).get().apply {
        setHasSearchButton(false)
        entityData.coefficients?.def()
            ?.element()?.coefficientsMap?.forEach { if (it.value != 1.0) add(it.key).setPercentsAsCoefficient(it.value) }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted() }
        fieldsSaver.add { entity ->
            if (selected.isEmpty()) return@add
            if (entity.coefficients == null) entity.coefficients = Coefficients()
            else Element.values().forEach { entity.coefficients.def().element().set(it, 1.0) }
            selectedElementsNodes.forEach { entity.coefficients.def().element().set(it.value, it.coefficient) }
        }
    }

    val buffSizeAtk = SelectorContainer.withPercents(ObservableEnums.SIZES).get().apply {
        setHasSearchButton(false)
        entityData.coefficients?.atk()
            ?.size()?.coefficientsMap?.forEach { if (it.value != 1.0) add(it.key).setPercentsAsCoefficient(it.value) }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted() }
        fieldsSaver.add { entity ->
            if (selected.isEmpty()) return@add
            if (entity.coefficients == null) entity.coefficients = Coefficients()
            else Size.values().forEach { entity.coefficients.atk().size().set(it, 1.0) }
            selectedElementsNodes.forEach { entity.coefficients.atk().size().set(it.value, it.coefficient) }
        }
    }

    val buffSizeDef = SelectorContainer.withPercents(ObservableEnums.SIZES).get().apply {
        setHasSearchButton(false)
        entityData.coefficients?.def()
            ?.size()?.coefficientsMap?.forEach { if (it.value != 1.0) add(it.key).setPercentsAsCoefficient(it.value) }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted() }
        fieldsSaver.add { entity ->
            if (selected.isEmpty()) return@add
            if (entity.coefficients == null) entity.coefficients = Coefficients()
            else Size.values().forEach { entity.coefficients.def().size().set(it, 1.0) }
            selectedElementsNodes.forEach { entity.coefficients.def().size().set(it.value, it.coefficient) }
        }
    }

    val attackType = ComboBox(ObservableEnums.ATTACK_WITHOUT_WEAPON_TYPE).apply {
        value = entityData.weaponData?.attackType ?: AttackType.MELEE
        changesChecker.add(this) { value }
        fieldsSaver.add {
            it.weaponData.attackType = value
        }
    }

    val dualHanded = CheckBox("Dual handed").apply {
        isSelected = entityData.weaponData.isDualHanded
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.weaponData.isDualHanded = isSelected }
    }

    val minDmg = textfield {
        filterInput { it.controlNewText.isInt() }
        text = entityData.weaponData.minDmg.toInt().toString()
        changesChecker.add(this) { text }
        fieldsSaver.add { it.weaponData.minDmg = text.toDouble() }
    }

    val maxDmg = textfield {
        filterInput { it.controlNewText.isInt() }
        text = entityData.weaponData.maxDmg.toInt().toString()
        changesChecker.add(this) { text }
        fieldsSaver.add { it.weaponData.maxDmg = text.toDouble() }
    }

    override val root = anchorpane {

    }


    override fun reasonsNotToSave(): List<String> {
        //TODO("Not yet implemented")
        return listOf()
    }
}