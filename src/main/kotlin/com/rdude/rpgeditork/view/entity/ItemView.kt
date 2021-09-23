package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.enums.ObservableEnums
import com.rdude.rpgeditork.enums.SKILL
import com.rdude.rpgeditork.utils.isPositive
import com.rdude.rpgeditork.utils.removeSpaces
import com.rdude.rpgeditork.utils.row
import com.rdude.rpgeditork.view.helper.EntityTopMenu
import com.rdude.rpgeditork.view.helper.ImagePicker
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.collections.transformation.FilteredList
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.scene.layout.ColumnConstraints
import javafx.scene.text.Font
import ru.rdude.fxlib.containers.selector.SelectorContainer
import ru.rdude.rpg.game.logic.coefficients.Coefficients
import ru.rdude.rpg.game.logic.data.ItemData
import ru.rdude.rpg.game.logic.enums.*
import tornadofx.*
import java.util.function.Predicate

class ItemView(wrapper: EntityDataWrapper<ItemData>) : EntityView<ItemData>(wrapper) {

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

    val mainType = ComboBox(ObservableEnums.ITEM_MAIN_TYPES).apply {
        value = entityData.itemType?.mainType ?: ItemMainType.SIMPLE
        changesChecker.add(this) { value }
    }

    val type = ComboBox<ItemType>().apply {
        // config
        val predicate = Predicate<ItemType> { itemType -> mainType.value == itemType.mainType }
        val filteredList = FilteredList(ObservableEnums.ITEM_TYPES, predicate)
        items = filteredList
        mainType.valueProperty().onChange {
            filteredList.predicate = null
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

    val stackable = CheckBox().apply {
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
        filterInput { it.controlNewText.isInt() && it.controlNewText.toInt().isPositive() }
        text = entityData.price.toInt().toString()
        changesChecker.add(this) { text }
        fieldsSaver.add { it.price = if (text.isBlank()) 0.0 else text.toDouble() }
    }

    val elements = SelectorContainer.simple(ObservableEnums.ELEMENTS).get().apply {
        setHasSearchButton(false)
        addAll(entityData.elements)
        changesChecker.add(this) { selected.sorted() }
        fieldsSaver.add { it.elements = selected.toHashSet() }
    }

    val stats = SelectorContainer.withTextField(ObservableEnums.STAT_NAMES)
        .sizePercentages(70.0, 30.0)
        .nameBy(StatName::getName)
        .searchBy({ s -> s.variableName }, { s -> s.getName() })
        .addOption { tf -> tf.textField.filterInput { it.controlNewText.isInt() } }
        .get()
        .apply {
            setHasSearchButton(false)
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
                    entity.stats[it.value].set(it.textField.text.toDouble())
                }
            }
        }

    val statsRequirements = SelectorContainer.withTextField(ObservableEnums.STAT_NAMES)
        .disableSearch()
        .nameBy(StatName::getName)
        .searchBy({ s -> s.variableName }, { s -> s.getName() })
        .get().apply {
            setHasSearchButton(false)
            addOption { e -> e.textField.filterInput { it.controlNewText.isInt() } }
            addOption { e -> e.setSizePercentages(75.0, 25.0) }
            entityData.requirements.forEachWithNestedStats {
                if (it.value() != 0.0) {
                    add(StatName.get(it.javaClass)).textField.text = it.value().toInt().toString()
                }
            }
            changesChecker.add(this) {
                selected.sorted() to selectedElementsNodes.map { e -> e.textField.text }.sorted()
            }
            fieldsSaver.add { entity ->
                entity.requirements.forEachWithNestedStats { it.set(0.0) }
                selectedElementsNodes.forEach {
                    entity.requirements[it.value].set(it.textField.text.toDouble())
                }
            }
        }

    val skillsOnUse = SelectorContainer.simple(Data.skills.list)
        .nameByProperty { w -> w.entityNameProperty }
        .searchBy({ w -> w.entityNameProperty.get() }, { w -> w.entityData.name })
        .get()
        .apply {
            SKILL.configSearchDialog(searchDialog)
            entityData.skillsOnUse.forEach { add(Data.skills[it]) }
            changesChecker.add(this) { selected.sorted() }
            fieldsSaver.add { entityData.skillsOnUse = selected.map { w -> w.entityData.guid } }
        }

    val skillsEquip = SelectorContainer.simple(Data.skills.list)
        .nameByProperty { w -> w.entityNameProperty }
        .searchBy({ w -> w.entityNameProperty.get() }, { w -> w.entityData.name })
        .get()
        .apply {
            SKILL.configSearchDialog(searchDialog)
            entityData.skillsEquip.forEach { add(Data.skills[it]) }
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

    val dualHanded = CheckBox().apply {
        isSelected = entityData.weaponData.isDualHanded
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.weaponData.isDualHanded = isSelected }
    }

    val minDmg = textfield {
        filterInput { it.controlNewText.isInt() && it.controlNewText.toInt().isPositive() }
        text = entityData.weaponData.minDmg.toInt().toString()
        changesChecker.add(this) { text }
        fieldsSaver.add { it.weaponData.minDmg = if (text.isDouble()) text.toDouble() else 0.0}
    }

    val maxDmg = textfield {
        filterInput { it.controlNewText.isInt() && it.controlNewText.toInt().isPositive() }
        text = entityData.weaponData.maxDmg.toInt().toString()
        changesChecker.add(this) { text }
        fieldsSaver.add { it.weaponData.maxDmg = if (text.isDouble()) text.toDouble() else 0.0 }
    }

    val description = textarea {
        isWrapText = true
        text = entityData.description
        changesChecker.add(this) { text }
        fieldsSaver.add { it.description = text }
    }

    val itemIcon = ImagePicker(
        header = "Icon (64x64)",
        imageWidthRestriction = 64.0,
        imageHeightRestriction = 64.0
    ).apply {
        imageResourceWrapper = Data.images[entityData.resources.mainImage?.guid]
        changesChecker.add(this, true) { imageResourceWrapper?.guid }
        fieldsSaver.add { it.resources.mainImage = imageResourceWrapper?.resource }
        imagePickers.add(this)
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
                    paddingAll = 10.0
                    spacing = 25.0
                    scrollpane {
                        isFitToHeight = true
                        vbox {
                            alignment = Pos.TOP_CENTER
                            spacing = 5.0
                            paddingAll = 5.0
                            text("Main properties") {
                                font = Font.font(16.0)
                            }
                            gridpane {
                                hgap = 5.0
                                vgap = 5.0
                                constraintsForColumn(1).maxWidth = 145.0
                                row("Name", nameField)
                                row("Name in editor", nameInEditorField)
                                row("Rarity", rarity)
                                row("Main type", mainType)
                                row("Type", type)
                                row("Price", price)
                                row("Elements", elements.apply { prefHeight = 80.0 })
                                row("Stackable", stackable)
                            }
                            text("Requirements") {
                                font = Font.font(16.0)
                            }
                            add(statsRequirements.apply {
                                prefHeight = 130.0
                                prefWidth = 160.0
                            })
                        }
                    }
                    scrollpane {
                        isFitToHeight = true
                        vbox {
                            spacing = 10.0
                            alignment = Pos.TOP_CENTER
                            text("Other properties") {
                                font = Font.font(16.0)
                            }
                            hbox {
                                spacing = 10.0
                                alignment = Pos.TOP_CENTER
                                vbox {
                                    spacing = 10.0
                                    alignment = Pos.TOP_CENTER
                                    text("Stats")
                                    add(stats.apply {
                                        prefHeight = 120.0
                                        prefWidth = 250.0
                                    })
                                    text("Cast skills when item is used")
                                    add(skillsOnUse.apply {
                                        prefHeight = 120.0
                                        prefWidth = 250.0
                                    })
                                    text("Allow to use skills when item is equipped")
                                    add(skillsEquip.apply {
                                        prefHeight = 120.0
                                        prefWidth = 250.0
                                    })
                                }
                                vbox {
                                    spacing = 5.0
                                    alignment = Pos.TOP_CENTER
                                    prefWidth = 400.0
                                    text("Coefficients")
                                    gridpane {
                                        hgap = 10.0
                                        vgap = 5.0
                                        this.columnConstraints.addAll(
                                            ColumnConstraints(195.0).apply { halignment = HPos.CENTER },
                                            ColumnConstraints(195.0).apply { halignment = HPos.CENTER }
                                        )
                                        row("Attack with attack type", "Defence from attack type")
                                        row {
                                            add(buffAttackTypeAtk.apply { prefHeight = 80.0 })
                                            add(buffAttackTypeDef.apply { prefHeight = 80.0 })
                                        }
                                        row("Attack to being type", "Defence from being type")
                                        row {
                                            add(buffBeingTypeAtk.apply { prefHeight = 80.0 })
                                            add(buffBeingTypeDef.apply { prefHeight = 80.0 })
                                        }
                                        row("Attack to element", "Defence from element")
                                        row {
                                            add(buffElementAtk.apply { prefHeight = 80.0 })
                                            add(buffElementDef.apply { prefHeight = 80.0 })
                                        }
                                        row("Attack to size", "Defence from size")
                                        row {
                                            add(buffSizeAtk.apply { prefHeight = 80.0 })
                                            add(buffSizeDef.apply { prefHeight = 80.0 })
                                        }
                                    }
                                }
                            }
                        }
                    }
                    vbox {
                        isDisable = mainType.value != ItemMainType.WEAPON
                        mainType.valueProperty().onChange { isDisable = it != ItemMainType.WEAPON }
                        spacing = 10.0
                        alignment = Pos.TOP_CENTER
                        text("Weapon properties") {
                            font = Font.font(16.0)
                        }
                        gridpane {
                            hgap = 5.0
                            vgap = 5.0
                            constraintsForColumn(1).maxWidth = 145.0
                            row("Attack type", attackType)
                            row("Min damage", minDmg)
                            row("Max damage", maxDmg)
                            row("Dual handed", dualHanded)
                        }
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
                            add(itemIcon)
                            vbox {
                                spacing = 5.0
                                alignment = Pos.TOP_CENTER
                                text("Description")
                                add(description)
                            }
                        }
                        gridpane {
                            hgap = 5.0
                            vgap = 5.0
                            row {
                                text("Item info")
                                add(itemInfo)
                            }
                            row {
                                text("References info")
                                add(referenceInfo)
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
        stats.selectedElementsNodes.forEach {
            if (it.textField.text.removeSpaces().isEmpty()) {
                messages.add("${it.value.getName().toUpperCase()} stat field is empty")
            }
        }
        statsRequirements.selectedElementsNodes.forEach {
            if (it.textField.text.removeSpaces().isEmpty()) {
                messages.add("${it.value.getName().toUpperCase()} stat requirement field is empty")
            }
        }
        buffAttackTypeAtk.selectedElementsNodes.forEach {
            if (it.textField.text.removeSpaces().isEmpty()) {
                messages.add("Field buff ATTACK with ${it.value.name.toUpperCase()} is empty")
            }
        }
        buffAttackTypeDef.selectedElementsNodes.forEach {
            if (it.textField.text.removeSpaces().isEmpty()) {
                messages.add("Field buff DEFENCE from ${it.value.name.toUpperCase()} is empty")
            }
        }
        buffBeingTypeAtk.selectedElementsNodes.forEach {
            if (it.textField.text.removeSpaces().isEmpty()) {
                messages.add("Field buff ATTACK against ${it.value.name.toUpperCase()} is empty")
            }
        }
        buffBeingTypeDef.selectedElementsNodes.forEach {
            if (it.textField.text.removeSpaces().isEmpty()) {
                messages.add("Field buff DEFENCE from ${it.value.name.toUpperCase()} is empty")
            }
        }
        buffElementAtk.selectedElementsNodes.forEach {
            if (it.textField.text.removeSpaces().isEmpty()) {
                messages.add("Field buff ATTACK against ${it.value.name.toUpperCase()} is empty")
            }
        }
        buffElementDef.selectedElementsNodes.forEach {
            if (it.textField.text.removeSpaces().isEmpty()) {
                messages.add("Field buff DEFENCE from ${it.value.name.toUpperCase()} is empty")
            }
        }
        buffSizeAtk.selectedElementsNodes.forEach {
            if (it.textField.text.removeSpaces().isEmpty()) {
                messages.add("Field buff ATTACK against ${it.value.name.toUpperCase()} is empty")
            }
        }
        buffSizeDef.selectedElementsNodes.forEach {
            if (it.textField.text.removeSpaces().isEmpty()) {
                messages.add("Field buff DEFENCE from ${it.value.name.toUpperCase()} enemies is empty")
            }
        }
        if (mainType.value == ItemMainType.WEAPON) {
            if (minDmg.text.isBlank()) {
                messages.add("This item is weapon but MIN DAMAGE field is empty")
            }
            if (maxDmg.text.isBlank()) {
                messages.add("This item is weapon but MAX DAMAGE field is empty")
            }
        }
        return messages
    }
}