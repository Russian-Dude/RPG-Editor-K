package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.enums.ITEM
import com.rdude.rpgeditork.enums.ObservableEnums
import com.rdude.rpgeditork.enums.SKILL
import com.rdude.rpgeditork.utils.isPositive
import com.rdude.rpgeditork.utils.removeSpaces
import com.rdude.rpgeditork.utils.row
import com.rdude.rpgeditork.utils.trimZeroes
import com.rdude.rpgeditork.view.helper.EntityTopMenu
import com.rdude.rpgeditork.view.helper.ImagePicker
import com.rdude.rpgeditork.view.helper.SoundPicker
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import ru.rdude.fxlib.containers.selector.SelectorContainer
import ru.rdude.rpg.game.logic.data.ItemData
import ru.rdude.rpg.game.logic.data.MonsterData
import ru.rdude.rpg.game.logic.data.SkillData
import ru.rdude.rpg.game.logic.enums.AttackType
import ru.rdude.rpg.game.logic.enums.EntityReferenceInfo
import ru.rdude.rpg.game.logic.enums.Size
import ru.rdude.rpg.game.logic.stats.Bonus
import ru.rdude.rpg.game.logic.stats.Stat
import ru.rdude.rpg.game.logic.stats.StatObserver
import tornadofx.*

class MonsterView(wrapper: EntityDataWrapper<MonsterData>) : EntityView<MonsterData>(wrapper) {

    val statsForCalculate = entityData.stats.copy(true).apply {
        fieldsSaver.add { this.copyTo(entityData.stats) }
    }

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

    val size = combobox(values = ObservableEnums.SIZES) {
        value = entityData.size ?: Size.SMALL
        changesChecker.add(this) { value }
        fieldsSaver.add { it.size = value }
    }

    val attackType = combobox(values = ObservableEnums.ATTACK_WITHOUT_WEAPON_TYPE) {
        value = entityData.defaultAttackType ?: AttackType.MELEE
        changesChecker.add(this) { value }
        fieldsSaver.add { it.defaultAttackType = value }
    }

    val itemsDrop = SelectorContainer.withPercents(Data.itemsList)
        .nameByProperty(EntityDataWrapper<ItemData>::entityNameProperty)
        .searchBy({ w -> w.entityData.name }, { w -> w.entityData.nameInEditor })
        .get().apply {
            ITEM.configSearchDialog(searchDialog)
            entityData.drop.forEach { add(Data.itemsMap[it.key]).percents = it.value.toDouble() }
            changesChecker.add(this) {
                selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted()
            }
            fieldsSaver.add { entity ->
                entity.drop.clear()
                selectedElementsNodes.forEach { entity.drop[it.value.entityData.guid] = it.percents }
            }
        }

    val goldDrop = textfield {
        filterInput { it.controlNewText.isInt() && it.controlNewText.toInt().isPositive() }
        text = entityData.goldDrop.toInt().toString()
        changesChecker.add(this) { text }
        fieldsSaver.add { it.goldDrop = if (text.isBlank()) 0.0 else text.toDouble() }
    }

    val expReward = textfield {
        filterInput { it.controlNewText.isInt() && it.controlNewText.toInt().isPositive() }
        text = entityData.expReward.toInt().toString()
        changesChecker.add(this) { text }
        fieldsSaver.add { it.expReward = if (text.isBlank()) 0.0 else text.toDouble() }
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

    val mainLvl = textfield {
        filterInput {
            it.controlNewText.length <= 3 && it.controlNewText.isInt() && it.controlNewText.toInt().isPositive()
        }
        textProperty().onChange {
            val v = if (it!!.isBlank()) 1.0 else it.toDouble()
            statsForCalculate.lvl().set(v)
        }
        alignment = Pos.CENTER
        text = entityData.mainLvl.toInt().toString()
        changesChecker.add(this) { text }
        fieldsSaver.add { it.mainLvl = text.toDouble() }
    }

    val skills = SelectorContainer.withPercents(Data.skillsList)
        .nameByProperty(EntityDataWrapper<SkillData>::entityNameProperty)
        .searchBy({ w -> w.entityData.name }, { w -> w.entityData.nameInEditor })
        .get()
        .apply {
            SKILL.configSearchDialog(searchDialog)
            entityData.skills.forEach { add(Data.skillsMap[it.key]).percents = it.value.toDouble() }
            changesChecker.add(this) {
                selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted()
            }
            fieldsSaver.add { entity ->
                entity.skills.clear()
                selectedElementsNodes.forEach { entity.skills[it.value.entityData.guid] = it.percents }
            }
        }

    val startBuffs = SelectorContainer.simple(Data.skillsList)
        .nameByProperty { w -> w.entityNameProperty }
        .searchBy({ w -> w.entityNameProperty.get() }, { w -> w.entityData.name })
        .get()
        .apply {
            SKILL.configSearchDialog(searchDialog)
            entityData.startBuffs.forEach { add(Data.skillsMap[it]) }
            changesChecker.add(this) { selected.sorted() }
            fieldsSaver.add { entityData.startBuffs = selected.map { w -> w.entityData.guid }.toSet() }
        }

    val spawnBiomsAll = checkbox("All") {
        isSelected = entityData.spawnBioms.containsAll(ObservableEnums.BIOMS)
        changesChecker.add(this) { isSelected }
    }
    val spawnBioms = SelectorContainer.simple(ObservableEnums.BIOMS).get().apply {
        setHasSearchButton(false)
        addOption { it.disableProperty().bind(spawnBiomsAll.selectedProperty()) }
        configDeleteButton { it.disableProperty().bind(spawnBiomsAll.selectedProperty()) }
        val buttonDefaultAction = addButton.onAction
        addButton.setOnAction {
            val size = selected.size
            buttonDefaultAction.handle(it)
            if (selected.size > size) {
                spawnBiomsAll.isSelected = false
            }
        }
        if (entityData.spawnBioms.containsAll(ObservableEnums.BIOMS)) {
            spawnBiomsAll.isSelected = true
        } else {
            addAll(entityData.spawnBioms)
        }
        changesChecker.add(this) { if (spawnBiomsAll.isSelected) ObservableEnums.BIOMS.sorted() else selected.sorted() }
        fieldsSaver.add {
            if (spawnBiomsAll.isSelected) {
                it.spawnBioms = ObservableEnums.BIOMS.toSet()
            } else {
                it.spawnBioms = selected.toHashSet()
            }
        }
    }

    val spawnReliefsAll = checkbox("All") {
        isSelected = entityData.spawnReliefs.containsAll(ObservableEnums.RELIEFS)
        changesChecker.add(this) { isSelected }
    }
    val spawnReliefs = SelectorContainer.simple(ObservableEnums.RELIEFS).get().apply {
        setHasSearchButton(false)
        addOption { it.disableProperty().bind(spawnReliefsAll.selectedProperty()) }
        configDeleteButton { it.disableProperty().bind(spawnReliefsAll.selectedProperty()) }
        val buttonDefaultAction = addButton.onAction
        addButton.setOnAction {
            val size = selected.size
            buttonDefaultAction.handle(it)
            if (selected.size > size) {
                spawnReliefsAll.isSelected = false
            }
        }
        if (entityData.spawnReliefs.containsAll(ObservableEnums.RELIEFS)) {
            spawnReliefsAll.isSelected = true
        } else {
            addAll(entityData.spawnReliefs)
        }
        changesChecker.add(this) { if (spawnReliefsAll.isSelected) ObservableEnums.RELIEFS.sorted() else selected.sorted() }
        fieldsSaver.add {
            if (spawnReliefsAll.isSelected) {
                it.spawnReliefs = ObservableEnums.RELIEFS.toSet()
            } else {
                it.spawnReliefs = selected.toHashSet()
            }
        }
    }

    val canBlock = checkbox("block") {
        isSelected = entityData.isCanBlock
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.isCanBlock = isSelected }
    }

    val canParry = checkbox("parry") {
        isSelected = entityData.isCanParry
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.isCanParry = isSelected }
    }

    val description = textarea {
        isWrapText = true
        text = entityData.description
        changesChecker.add(this) { text }
        fieldsSaver.add { it.description = text }
    }

    val monsterImage = ImagePicker(
        header = "Image",
        imageWidth = 200.0,
        imageHeight = 200.0
    ).apply {
        imageResourceWrapper = Data.images[entityData.resources.monsterImage?.guid]
        changesChecker.add(this, true) { imageResourceWrapper?.guid }
        fieldsSaver.add { it.resources.monsterImage = imageResourceWrapper?.resource }
        imagePickers.add(this)
    }

    val greetingSound = SoundPicker(
        header = "Greeting sound",
        audioButtonAreaHeight = 64.0
    ).apply {
        soundResourceWrapper = Data.sounds[entityData.resources.greetingSound?.guid]
        changesChecker.add(this) { soundResourceWrapper?.guid }
        fieldsSaver.add { it.resources.greetingSound = soundResourceWrapper?.resource }
        soundPickers.add(this)
    }

    val deathSound = SoundPicker(
        header = "Death sound",
        audioButtonAreaHeight = 64.0
    ).apply {
        soundResourceWrapper = Data.sounds[entityData.resources.deathSound?.guid]
        changesChecker.add(this) { soundResourceWrapper?.guid }
        fieldsSaver.add { it.resources.deathSound = soundResourceWrapper?.resource }
        soundPickers.add(this)
    }

    val hitReceivedSound = SoundPicker(
        header = "Hit received sound",
        audioButtonAreaHeight = 64.0
    ).apply {
        soundResourceWrapper = Data.sounds[entityData.resources.hitReceivedSound?.guid]
        changesChecker.add(this) { soundResourceWrapper?.guid }
        fieldsSaver.add { it.resources.hitReceivedSound = soundResourceWrapper?.resource }
        soundPickers.add(this)
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
                                            text("Main")
                                            add(mainLvl.apply { prefWidth = 50.0 })
                                        }
                                        vbox {
                                            spacing = 3.0
                                            alignment = Pos.CENTER
                                            text("Max")
                                            add(maxLvl.apply { prefWidth = 50.0 })
                                        }
                                    }
                                }
                                row {
                                    text("Can")
                                    hbox {
                                        spacing = 15.0
                                        alignment = Pos.CENTER
                                        add(canBlock)
                                        add(canParry)
                                    }
                                }
                                row("Attack type", attackType)
                                row("Size", size)
                                row("Elements", elements.apply { prefHeight = 80.0 })
                                row("Types", beingTypes.apply { prefHeight = 80.0 })
                                row {
                                    vbox {
                                        spacing = 5.0
                                        alignment = Pos.CENTER_LEFT
                                        text("Spawn biomes")
                                        add(spawnBiomsAll)
                                    }
                                    add(spawnBioms.apply { prefHeight = 80.0 })
                                }
                                row {
                                    vbox {
                                        spacing = 5.0
                                        alignment = Pos.CENTER_LEFT
                                        text("Spawn reliefs")
                                        add(spawnReliefsAll)
                                    }
                                    add(spawnReliefs.apply { prefHeight = 80.0 })
                                }
                            }
                        }
                    }
                    scrollpane {
                        isFitToHeight = true
                        vbox {
                            alignment = Pos.TOP_CENTER
                            spacing = 10.0
                            paddingAll = 5.0
                            prefWidth = 240.0
                            text("Rewards") {
                                font = Font.font(16.0)
                            }
                            hbox {
                                alignment = Pos.TOP_CENTER
                                spacing = 10.0
                                vbox {
                                    alignment = Pos.TOP_CENTER
                                    spacing = 5.0
                                    text("Gold") {
                                        textAlignment = TextAlignment.CENTER
                                    }
                                    add(goldDrop)
                                }
                                vbox {
                                    alignment = Pos.TOP_CENTER
                                    spacing = 5.0
                                    text("Experience") {
                                        textAlignment = TextAlignment.CENTER
                                    }
                                    add(expReward)
                                }
                            }
                            text("Items drop")
                            add(itemsDrop.apply { prefHeight = 120.0 })
                            text("Skills") {
                                font = Font.font(16.0)
                            }
                            text("Start buffs")
                            add(startBuffs.apply { prefHeight = 120.0 })
                            text("Use skills")
                            add(skills.apply { prefHeight = 120.0 })
                        }
                    }
                    scrollpane {
                        isFitToHeight = true
                        vbox {
                            alignment = Pos.TOP_CENTER
                            spacing = 10.0
                            paddingAll = 5.0
                            text("Stats") {
                                font = Font.font(16.0)
                            }
                            gridpane {
                                hgap = 20.0
                                vgap = 10.0
                                row(
                                    "Agility", MainStatView(statsForCalculate.agi()).root,
                                    "Dexterity", MainStatView(statsForCalculate.dex()).root,
                                    "Intelligence", MainStatView(statsForCalculate.intel()).root,
                                )
                                row(
                                    "Luck", MainStatView(statsForCalculate.luck()).root,
                                    "Strength", MainStatView(statsForCalculate.str()).root,
                                    "Vitality", MainStatView(statsForCalculate.vit()).root,
                                )
                                row()
                                row()
                                row(
                                    "Block", SecondaryStatView(statsForCalculate.block()).root,
                                    "Concentration", SecondaryStatView(statsForCalculate.concentration()).root,
                                    "Critical chance", SecondaryStatView(statsForCalculate.crit()).root,
                                )
                                row(
                                    "Defence", SecondaryStatView(statsForCalculate.def()).root,
                                    "Flee", SecondaryStatView(statsForCalculate.flee()).root,
                                    "Lucky dodge", SecondaryStatView(statsForCalculate.flee().luckyDodgeChance()).root,
                                )
                                row(
                                    "Melee min damage", SecondaryStatView(statsForCalculate.dmg().melee().min).root,
                                    "Range min damage", SecondaryStatView(statsForCalculate.dmg().range().min).root,
                                    "Magic min damage", SecondaryStatView(statsForCalculate.dmg().magic().min).root,
                                )
                                row(
                                    "Melee max damage", SecondaryStatView(statsForCalculate.dmg().melee().max).root,
                                    "Range max damage", SecondaryStatView(statsForCalculate.dmg().range().max).root,
                                    "Magic max damage", SecondaryStatView(statsForCalculate.dmg().magic().max).root,
                                )
                                row(
                                    "Stamina", SecondaryStatView(statsForCalculate.stm().max()).root,
                                    "Stamina recovery", SecondaryStatView(statsForCalculate.stm().recovery()).root,
                                    "Stamina per hit", SecondaryStatView(statsForCalculate.stm().perHit()).root,
                                )
                                row(
                                    "Health", SecondaryStatView(statsForCalculate.hp().max()).root,
                                    "Health recovery", SecondaryStatView(statsForCalculate.hp().recovery()).root,
                                    "Magic resistance", SecondaryStatView(statsForCalculate.magicResistance()).root,
                                )
                                row(
                                    "Parry", SecondaryStatView(statsForCalculate.parry()).root,
                                    "Physic resistance", SecondaryStatView(statsForCalculate.physicResistance()).root,
                                    "", text("")
                                )
                            }
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
                            add(monsterImage)
                            add(greetingSound)
                            add(hitReceivedSound)
                            add(deathSound)
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
                                text("Monster info")
                                add(monsterInfo)
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
        if (mainLvl.text.isBlank()) {
            messages.add("Main level field is empty")
        }
        if (minLvl.text.isBlank()) {
            messages.add("Minimum level field is empty")
        }
        if (maxLvl.text.isBlank()) {
            messages.add("Maximum level field is empty")
        }
        if (mainLvl.text.isNotBlank() && minLvl.text.isNotBlank() && maxLvl.text.isNotBlank()) {
            val mainLvl = mainLvl.text.toInt()
            val minLvl = minLvl.text.toInt()
            val maxLvl = maxLvl.text.toInt()
            if (mainLvl < minLvl || mainLvl > maxLvl) {
                messages.add("Main level value must be between minimum level and maximum level")
            }
            if (minLvl > maxLvl) {
                messages.add("Maximum level must be greater or equals than minimum level")
            }
        }
        return messages
    }


    inner class MainStatView(stat: Stat) : Fragment() {

        override val root = textfield {
            prefWidth = 60.0
            filterInput {
                it.controlNewText.length <= 3 && it.controlNewText.isInt() && it.controlNewText.toInt().isPositive()
            }
            promptText = "1"
            textProperty().onChange {
                val v = if (it?.isBlank() == true) 1.0 else it?.toDouble() ?: 1.0
                stat.set(v)
            }
            text = entityData.stats.get(stat.javaClass).value().toInt().toString()
            changesChecker.add(this) { text }
            // fields saver is inside stats for calculate
        }
    }

    inner class SecondaryStatView(stat: Stat) : Fragment(), StatObserver {

        init {
            stat.subscribe(this)
        }

        override val root = textfield {
            prefWidth = 60.0
            filterInput {
                it.controlNewText.length <= 4 && it.controlNewText.isDouble() && it.controlNewText.toInt().isPositive()
            }
            println("============ ${stat.name} ============")
            println("value = ${stat.value()}")
            println("pure value = ${stat.pureValue()}")
            text = if (stat.pureValue() == stat.value()) "" else stat.value().toString().trimZeroes()
            promptText = stat.value().toString().trimZeroes()
            textProperty().onChange {
                if (it!!.isBlank()) {
                    stat.setBuffValue(Bonus::class.java, 0.0)
                } else {
                    stat.setBuffValue(Bonus::class.java, it.toDouble() - stat.pureValue())
                }
            }
            changesChecker.add(this) { text }
        }

        override fun update(stat: Stat) {
            root.promptText = stat.pureValue().toString().trimZeroes()
        }
    }

}