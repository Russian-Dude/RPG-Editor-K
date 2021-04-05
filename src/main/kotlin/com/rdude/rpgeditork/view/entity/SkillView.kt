package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.enums.*
import com.rdude.rpgeditork.utils.*
import com.rdude.rpgeditork.view.helper.EntityTopMenu
import com.rdude.rpgeditork.view.helper.ImagePicker
import com.rdude.rpgeditork.view.helper.SkillsOnBeingActionSelectorElement
import com.rdude.rpgeditork.view.helper.SoundPicker
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.ColumnConstraints
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import ru.rdude.fxlib.containers.selector.SelectorContainer
import ru.rdude.fxlib.containers.selector.SelectorElementAutocompletionTextField
import ru.rdude.fxlib.textfields.AutocompletionTextField
import ru.rdude.rpg.game.logic.coefficients.Coefficients
import ru.rdude.rpg.game.logic.data.ItemData
import ru.rdude.rpg.game.logic.data.MonsterData
import ru.rdude.rpg.game.logic.data.SkillData
import ru.rdude.rpg.game.logic.entities.beings.Player
import ru.rdude.rpg.game.logic.entities.skills.SkillParser
import ru.rdude.rpg.game.logic.enums.*
import ru.rdude.rpg.game.logic.enums.Target
import tornadofx.*

class SkillView(wrapper: EntityDataWrapper<SkillData>) : EntityView<SkillData>(wrapper) {

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

    val requiredStamina = textfield {
        maxWidth = 50.0
        alignment = Pos.CENTER
        promptText = "0"
        filterInput { it.controlNewText.isInt() }
        if (entityData.staminaReq != 0) {
            text = entityData.staminaReq.toString()
        }
        changesChecker.add(this) { text }
        fieldsSaver.add { it.staminaReq = if (text.isNotBlank()) text.toInt() else 0 }
    }

    val requiredConcentration = textfield {
        maxWidth = 50.0
        alignment = Pos.CENTER
        promptText = "0"
        filterInput { it.controlNewText.isInt() }
        if (entityData.concentrationReq != 0) {
            text = entityData.concentrationReq.toString()
        }
        changesChecker.add(this) { text }
        fieldsSaver.add { it.concentrationReq = if (text.isNotBlank()) text.toInt() else 0 }
    }

    val damage = AutocompletionTextField(ObservableEnums.FORMULA_VARIABLES, AutocompletionTextField.Type.WORDS).apply {
        setElementDescriptionFunction { v -> v.description }
        wordsDelimiter = "[+-/*\\s]"
        text = entityData.damage ?: ""
        changesChecker.add(this) { text }
        fieldsSaver.add { it.damage = text }
    }

    val mainTarget = ComboBox(ObservableEnums.MAIN_TARGETS).apply {
        value = entityData.mainTarget ?: Target.NO
        changesChecker.add(this) { value }
        fieldsSaver.add { it.mainTarget = value }
    }

    val targets = SelectorContainer.simple(ObservableEnums.SUB_TARGETS).get().apply {
        setHasSearchButton(false)
        addAll(entityData.targets)
        changesChecker.add(this) { selected.sorted() }
        fieldsSaver.add { it.targets = selected }
    }

    val damageElementCoefficients = SelectorContainer.withPercents(ObservableEnums.ELEMENTS).get().apply {
        setHasSearchButton(false)
        entityData.coefficients.atk().element().coefficientsMap
            .filter { it.value != 1.0 }
            .forEach { (element, coefficient) -> add(element).setPercentsAsCoefficient(coefficient) }
        changesChecker.add(this) { selected to selectedElementsNodes.map { s -> s.percents }.sorted() }
        fieldsSaver.add { entity ->
            ObservableEnums.ELEMENTS.forEach { entity.coefficients.atk().element().coefficientsMap[it] = 1.0 }
            selectedElementsNodes.forEach {
                entity.coefficients.atk().element().set(it.value, it.coefficient)
            }
        }
    }

    val damageBeingTypeCoefficients = SelectorContainer.withPercents(ObservableEnums.BEING_TYPES).get().apply {
        setHasSearchButton(false)
        entityData.coefficients.atk().beingType().coefficientsMap
            .filter { it.value != 1.0 }
            .forEach { (type, coefficient) -> add(type).setPercentsAsCoefficient(coefficient) }
        changesChecker.add(this) { selected to selectedElementsNodes.map { s -> s.percents }.sorted() }
        fieldsSaver.add { entity ->
            ObservableEnums.BEING_TYPES.forEach { entity.coefficients.atk().beingType().coefficientsMap[it] = 1.0 }
            selectedElementsNodes.forEach {
                entity.coefficients.atk().beingType().set(it.value, it.coefficient)
            }
        }
    }

    val damageSizeCoefficients = SelectorContainer.withPercents(ObservableEnums.SIZES).get().apply {
        setHasSearchButton(false)
        entityData.coefficients.atk().size().coefficientsMap
            .filter { it.value != 1.0 }
            .forEach { (size, coefficient) -> add(size).setPercentsAsCoefficient(coefficient) }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { s -> s.percents }.sorted() }
        fieldsSaver.add { entity ->
            ObservableEnums.SIZES.forEach { entity.coefficients.atk().size().coefficientsMap[it] = 1.0 }
            selectedElementsNodes.forEach { entity.coefficients.atk().size().set(it.value, it.coefficient) }
        }
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

    val canBeUsedInBattle = CheckBox("Battle").apply {
        isSelected = entityData.usableInGameStates[GameState.BATTLE] ?: true
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.usableInGameStates[GameState.BATTLE] = isSelected }
    }

    val canBeUsedInCamp = CheckBox("Camp").apply {
        isSelected = entityData.usableInGameStates[GameState.CAMP] ?: false
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.usableInGameStates[GameState.CAMP] = isSelected }
    }

    val canBeUsedInMap = CheckBox("Map").apply {
        isSelected = entityData.usableInGameStates[GameState.MAP] ?: false
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.usableInGameStates[GameState.MAP] = isSelected }
    }

    val canBeBlocked = CheckBox("Blocked").apply {
        isSelected = entityData.isCanBeBlocked
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.isCanBeBlocked = isSelected }
    }

    val canBeResisted = CheckBox("Resisted").apply {
        isSelected = entityData.isCanBeResisted
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.isCanBeResisted = isSelected }
    }

    val canBeDodged = CheckBox("Dodged").apply {
        isSelected = entityData.isCanBeDodged
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.isCanBeDodged = isSelected }
    }

    val duarationTurns = TextField().apply {
        filterInput { it.controlNewText.isInt() && it.controlNewText.toInt().isPositive() }
        text = entityData.durationInTurns ?: ""
        changesChecker.add(this) { text }
        fieldsSaver.add { it.durationInTurns = if (text.isNotBlank()) text else null }
    }

    val duarationMinutes = TextField().apply {
        filterInput { it.controlNewText.isInt() && it.controlNewText.toInt().isPositive() }
        text = entityData.durationInMinutes ?: ""
        changesChecker.add(this) { text }
        fieldsSaver.add { it.durationInMinutes = if (text.isNotBlank()) text else null }
    }

    val permanent = CheckBox("Permanent").apply {
        isSelected = entityData.isPermanent
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.isPermanent = isSelected }
    }

    val actsEveryMinute = TextField().apply {
        filterInput { it.controlNewText.isInt() }
        promptText = "0"
        alignment = Pos.CENTER
        if (entityData.actsEveryMinute != 0.0) {
            text = entityData.actsEveryMinute.toInt().toString()
        }
        changesChecker.add(this) { text }
        fieldsSaver.add { it.actsEveryMinute = if (text.isNotBlank()) text.toDouble() else 0.0 }
    }

    val actsEveryTurn = TextField().apply {
        filterInput { it.controlNewText.isInt() }
        promptText = "0"
        alignment = Pos.CENTER
        if (entityData.actsEveryTurn != 0.0) {
            text = entityData.actsEveryTurn.toInt().toString()
        }
        changesChecker.add(this) { text }
        fieldsSaver.add { it.actsEveryTurn = if (text.isNotBlank()) text.toDouble() else 0.0 }
    }

    val forcedCancelAfter = TextField().apply {
        filterInput { it.controlNewText.isInt() }
    }

    val forcedCancelHitsOrDamage = ComboBox(listOf("Hits", "Damage").asObservable())

    val forcedCancelReceivedOrDeal = ComboBox(listOf("Received", "Deal").asObservable())

    init {
        when {
            entityData.damageReceived != null -> {
                forcedCancelAfter.text = entityData.damageReceived
                forcedCancelReceivedOrDeal.value = "Received"
                forcedCancelHitsOrDamage.value = "Damage"
            }
            entityData.damageMade != null -> {
                forcedCancelAfter.text = entityData.damageMade
                forcedCancelReceivedOrDeal.value = "Deal"
                forcedCancelHitsOrDamage.value = "Damage"
            }
            entityData.hitsReceived != null -> {
                forcedCancelAfter.text = entityData.hitsReceived
                forcedCancelReceivedOrDeal.value = "Received"
                forcedCancelHitsOrDamage.value = "Hits"
            }
            entityData.hitsMade != null -> {
                forcedCancelAfter.text = entityData.hitsMade
                forcedCancelReceivedOrDeal.value = "Deal"
                forcedCancelHitsOrDamage.value = "Hits"
            }
            else -> {
                forcedCancelAfter.text = ""
                forcedCancelReceivedOrDeal.value = "Received"
                forcedCancelHitsOrDamage.value = "Hits"
            }
        }
        forcedCancelHitsOrDamage.apply { changesChecker.add(this) { value } }
        forcedCancelReceivedOrDeal.apply { changesChecker.add(this) { value } }
        forcedCancelAfter.apply { changesChecker.add(this) { text } }
        fieldsSaver.add {
            it.hitsMade = null
            it.hitsReceived = null
            it.damageMade = null
            it.damageReceived = null
            when (forcedCancelReceivedOrDeal.value) {
                "Received" -> {
                    if (forcedCancelHitsOrDamage.value == "Damage") {
                        it.damageReceived = forcedCancelAfter.text
                    } else {
                        it.hitsReceived = forcedCancelAfter.text
                    }
                }
                "Deal" -> {
                    if (forcedCancelHitsOrDamage.value == "Damage") {
                        it.damageMade = forcedCancelAfter.text
                    } else {
                        it.hitsMade = forcedCancelAfter.text
                    }
                }
            }
        }
    }

    val recalculateEveryIteration = CheckBox("Recalculate\r\nevery iteration").apply {
        isSelected = entityData.isRecalculateStatsEveryIteration
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.isRecalculateStatsEveryIteration = isSelected }
    }

    val onDuplicating = ComboBox(ObservableEnums.SKILL_OVERLAY).apply {
        value = entityData.overlay ?: SkillOverlay.UPDATE
        changesChecker.add(this) { value }
        fieldsSaver.add { it.overlay = value }
    }

    val buffType = ComboBox(ObservableEnums.BUFF_TYPES).apply {
        value = entityData.buffType ?: BuffType.PHYSIC
        changesChecker.add(this) { value }
        fieldsSaver.add { it.buffType = value }
    }

    val stats: SelectorContainer<StatName, SelectorElementAutocompletionTextField<StatName, FormulaVariable>> =
        SelectorContainer.withAutocompletionTextField(ObservableEnums.STAT_NAMES, ObservableEnums.FORMULA_VARIABLES)
            .nameBy(StatName::getName)
            .textFieldNameBy(FormulaVariable::variable)
            .textFieldDescription(FormulaVariable::description)
            .textFieldType(AutocompletionTextField.Type.WORDS)
            .wordsDelimiter("[-+/*\\s]")
            .get()
            .apply {
                setHasSearchButton(false)
                entityData.stats.forEach { if (it.value.isNotBlank()) add(it.key).setText(it.value) }
                changesChecker.add(this) {
                    selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted()
                }
                fieldsSaver.add { entity ->
                    ObservableEnums.STAT_NAMES.forEach { entity.stats[it] = "" }
                    selectedElementsNodes.forEach { entity.stats[it.value] = it.textField.text }
                }
            }

    val transformationBeingTypes = SelectorContainer.simple(ObservableEnums.BEING_TYPES).get().apply {
        setHasSearchButton(false)
        addAll(entityData.transformation.beingTypes)
        changesChecker.add(this) { selected.sorted() }
        fieldsSaver.add { it.transformation.beingTypes = selected.toHashSet() }
    }

    val transformationElements = SelectorContainer.simple(ObservableEnums.ELEMENTS).get().apply {
        setHasSearchButton(false)
        addAll(entityData.transformation.elements)
        changesChecker.add(this) { selected.sorted() }
        fieldsSaver.add { it.transformation.elements = selected.toHashSet() }
    }

    val transformationSize = ComboBox(ObservableEnums.SIZES_WITH_NULL).apply {
        value = entityData.transformation.size?.nullableVersion() ?: NullableSize.NO
        changesChecker.add(this) { value }
        fieldsSaver.add { it.transformation.size = value.size }
    }

    val transformationToggleGroup = ToggleGroup()

    val transformationOverride = radiobutton("Override", transformationToggleGroup) {
        isSelected = entityData.transformation.isOverride
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.transformation.isOverride = isSelected }
    }
    val transformationAdd = radiobutton("Add", transformationToggleGroup) {
        isSelected = !entityData.transformation.isOverride
    }

    val statsRequirements = SelectorContainer.withTextField(ObservableEnums.STAT_NAMES)
        .nameBy(StatName::getName)
        .searchBy({ s -> s.variableName }, { s -> s.getName() })
        .get().apply {
            setHasSearchButton(false)
            addOption { e -> e.textField.filterInput { it.controlNewText.isInt() } }
            addOption { e -> e.setSizePercentages(75.0, 25.0) }
            entityData.requirements.stats.forEachWithNestedStats {
                if (it.value() != 0.0) {
                    add(StatName.get(it.javaClass)).textField.text = it.value().toInt().toString()
                }
            }
            changesChecker.add(this) {
                selected.sorted() to selectedElementsNodes.map { e -> e.textField.text }.sorted()
            }
            fieldsSaver.add { entity ->
                entity.requirements.stats.forEachWithNestedStats { it.set(0.0) }
                selectedElementsNodes.forEach {
                    entity.requirements.stats.get(it.value.clazz).set(it.textField.text.toDouble())
                }
            }
        }

    val itemsRequirements = SelectorContainer.withTextField(Data.itemsList)
        .nameByProperty(EntityDataWrapper<ItemData>::entityNameProperty)
        .searchBy({ w -> w.entityData.name }, { w -> w.entityData.nameInEditor })
        .get()
        .apply {
            ITEM.configSearchDialog(searchDialog)
            addOption { e ->
                e.textField.filterInput {
                    it.controlNewText.isInt() && it.controlNewText.toInt().isPositive()
                }
            }
            entityData.requirements.items.forEach { add(Data.itemsMap[it.key]).textField.text = it.value.toString() }
            changesChecker.add(this) {
                selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted()
            }
            fieldsSaver.add { entity ->
                entity.requirements.items.clear()
                selectedElementsNodes.forEach {
                    entity.requirements.items.put(
                        it.value.entityData.guid,
                        it.textField.text.toInt()
                    )
                }
            }
        }

    val keepItemsToggleGroup = ToggleGroup()

    val takeItems = radiobutton("Take items", keepItemsToggleGroup).apply {
        isSelected = entityData.requirements.isTakeItems
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.requirements.isTakeItems = isSelected }
    }
    val keepItems = radiobutton("Keep items", keepItemsToggleGroup).apply {
        isSelected = !entityData.requirements.isTakeItems
    }

    val skillsCanCast = SelectorContainer.withPercents(Data.skillsList)
        .nameByProperty(EntityDataWrapper<SkillData>::entityNameProperty)
        .searchBy({ w -> w.entityData.name }, { w -> w.entityData.nameInEditor })
        .get()
        .apply {
            SKILL.configSearchDialog(searchDialog)
            entityData.skillsCouldCast.forEach { add(Data.skillsMap[it.key]).percents = it.value.toDouble() }
            changesChecker.add(this) {
                selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted()
            }
            fieldsSaver.add { entity ->
                entity.skillsCouldCast.clear()
                selectedElementsNodes.forEach {
                    entity.skillsCouldCast.put(
                        it.value.entityData.guid,
                        it.percents.toFloat()
                    )
                }
            }
        }

    val skillsMustCast = SelectorContainer.withPercents(Data.skillsList)
        .nameByProperty(EntityDataWrapper<SkillData>::entityNameProperty)
        .searchBy({ w -> w.entityData.name }, { w -> w.entityData.nameInEditor })
        .get()
        .apply {
            SKILL.configSearchDialog(searchDialog)
            entityData.skillsCouldCast.forEach { add(Data.skillsMap[it.key]).percents = it.value.toDouble() }
            changesChecker.add(this) {
                selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted()
            }
            fieldsSaver.add { entity ->
                entity.skillsMustCast.clear()
                selectedElementsNodes.forEach {
                    entity.skillsMustCast.put(
                        it.value.entityData.guid,
                        it.percents.toFloat()
                    )
                }
            }
        }

    val skillsOnBeingAction =
        SelectorContainer(ObservableEnums.BEING_ACTIONS) { SkillsOnBeingActionSelectorElement() }.apply {
            setHasSearchButton(false)
            isUnique = false
            entityData.skillsOnBeingAction.forEach {
                val beingAction = it.key
                it.value.forEach { entry ->
                    val selectorElement = add(beingAction)
                    selectorElement.skill = Data.skillsMap[entry.key]!!
                    selectorElement.percents = entry.value.toDouble()
                }
            }
            changesChecker.add(this) {
                selected.sorted() to selectedElementsNodes.map { it.coefficient to it.skill }
            }
            fieldsSaver.add { entity ->
                entity.skillsOnBeingAction.clear()
                selectedElementsNodes.forEach {
                    entity.skillsOnBeingAction.putIfAbsent(it.action, HashMap())
                    entity.skillsOnBeingAction[it.action]!![it.skill!!.entityData.guid] = it.percents.toFloat()
                }
            }
        }

    val castToToggleGroup = ToggleGroup()

    val onBeingActionCastToEnemy = radiobutton("Cast back to enemy", castToToggleGroup).apply {
        isSelected = entityData.isOnBeingActionCastToEnemy
        changesChecker.add(this) { isSelected }
        fieldsSaver.add { it.isOnBeingActionCastToEnemy = isSelected }
    }
    val onBeingActionCastToSelf = radiobutton("Cast to self", castToToggleGroup).apply {
        isSelected = !entityData.isOnBeingActionCastToEnemy
    }

    val summon = SelectorContainer.withPercents(Data.monstersList)
        .nameByProperty(EntityDataWrapper<MonsterData>::entityNameProperty)
        .searchBy({ w -> w.entityData.name }, { w -> w.entityData.nameInEditor })
        .get()
        .apply {
            entityData.summon.forEach { add(Data.monstersMap[it.key]).percents = it.value.toDouble() }
            changesChecker.add(this) { selected.sorted() }
            fieldsSaver.add { entity ->
                entity.summon.clear()
                selectedElementsNodes.forEach { entity.summon[it.value.entityData.guid] = it.percents.toFloat() }
            }
        }

    val receiveItems = SelectorContainer.withTextField(Data.itemsList)
        .nameByProperty(EntityDataWrapper<ItemData>::entityNameProperty)
        .searchBy({ w -> w.entityData.name }, { w -> w.entityData.nameInEditor })
        .get().apply {
            addOption { s -> s.textField.filterInput { t -> t.controlNewText.isInt() } }
            entityData.receiveItems.forEach { add(Data.itemsMap[it.key]).textField.text = it.value.toString() }
            changesChecker.add(this) {
                selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted()
            }
            fieldsSaver.add { entity ->
                entity.receiveItems.clear()
                selectedElementsNodes.forEach {
                    entity.receiveItems[it.value.entityData.guid] = it.textField.text.toInt()
                }
            }
        }

    val buffAttackTypeAtk = SelectorContainer.withPercents(ObservableEnums.ATTACK_TYPES).get().apply {
        setHasSearchButton(false)
        entityData.buffCoefficients?.atk()
            ?.attackType()?.coefficientsMap?.forEach { if (it.value != 1.0) add(it.key).setPercentsAsCoefficient(it.value) }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted() }
        fieldsSaver.add { entity ->
            if (selected.isEmpty()) return@add
            if (entity.buffCoefficients == null) entity.buffCoefficients = Coefficients()
            else AttackType.values().forEach { entity.buffCoefficients.atk().attackType().set(it, 1.0) }
            selectedElementsNodes.forEach { entity.buffCoefficients.atk().attackType().set(it.value, it.coefficient) }
        }
    }

    val buffAttackTypeDef = SelectorContainer.withPercents(ObservableEnums.ATTACK_TYPES).get().apply {
        setHasSearchButton(false)
        entityData.buffCoefficients?.def()
            ?.attackType()?.coefficientsMap?.forEach { if (it.value != 1.0) add(it.key).setPercentsAsCoefficient(it.value) }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted() }
        fieldsSaver.add { entity ->
            if (selected.isEmpty()) return@add
            if (entity.buffCoefficients == null) entity.buffCoefficients = Coefficients()
            else AttackType.values().forEach { entity.buffCoefficients.def().attackType().set(it, 1.0) }
            selectedElementsNodes.forEach { entity.buffCoefficients.def().attackType().set(it.value, it.coefficient) }
        }
    }

    val buffBeingTypeAtk = SelectorContainer.withPercents(ObservableEnums.BEING_TYPES).get().apply {
        setHasSearchButton(false)
        entityData.buffCoefficients?.atk()
            ?.beingType()?.coefficientsMap?.forEach { if (it.value != 1.0) add(it.key).setPercentsAsCoefficient(it.value) }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted() }
        fieldsSaver.add { entity ->
            if (selected.isEmpty()) return@add
            if (entity.buffCoefficients == null) entity.buffCoefficients = Coefficients()
            else BeingType.values().forEach { entity.buffCoefficients.atk().beingType().set(it, 1.0) }
            selectedElementsNodes.forEach { entity.buffCoefficients.atk().beingType().set(it.value, it.coefficient) }
        }
    }

    val buffBeingTypeDef = SelectorContainer.withPercents(ObservableEnums.BEING_TYPES).get().apply {
        setHasSearchButton(false)
        entityData.buffCoefficients?.def()
            ?.beingType()?.coefficientsMap?.forEach { if (it.value != 1.0) add(it.key).setPercentsAsCoefficient(it.value) }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted() }
        fieldsSaver.add { entity ->
            if (selected.isEmpty()) return@add
            if (entity.buffCoefficients == null) entity.buffCoefficients = Coefficients()
            else BeingType.values().forEach { entity.buffCoefficients.def().beingType().set(it, 1.0) }
            selectedElementsNodes.forEach { entity.buffCoefficients.def().beingType().set(it.value, it.coefficient) }
        }
    }

    val buffElementAtk = SelectorContainer.withPercents(ObservableEnums.ELEMENTS).get().apply {
        setHasSearchButton(false)
        entityData.buffCoefficients?.atk()
            ?.element()?.coefficientsMap?.forEach { if (it.value != 1.0) add(it.key).setPercentsAsCoefficient(it.value) }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted() }
        fieldsSaver.add { entity ->
            if (selected.isEmpty()) return@add
            if (entity.buffCoefficients == null) entity.buffCoefficients = Coefficients()
            else Element.values().forEach { entity.buffCoefficients.atk().element().set(it, 1.0) }
            selectedElementsNodes.forEach { entity.buffCoefficients.atk().element().set(it.value, it.coefficient) }
        }
    }

    val buffElementDef = SelectorContainer.withPercents(ObservableEnums.ELEMENTS).get().apply {
        setHasSearchButton(false)
        entityData.buffCoefficients?.def()
            ?.element()?.coefficientsMap?.forEach { if (it.value != 1.0) add(it.key).setPercentsAsCoefficient(it.value) }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted() }
        fieldsSaver.add { entity ->
            if (selected.isEmpty()) return@add
            if (entity.buffCoefficients == null) entity.buffCoefficients = Coefficients()
            else Element.values().forEach { entity.buffCoefficients.def().element().set(it, 1.0) }
            selectedElementsNodes.forEach { entity.buffCoefficients.def().element().set(it.value, it.coefficient) }
        }
    }

    val buffSizeAtk = SelectorContainer.withPercents(ObservableEnums.SIZES).get().apply {
        setHasSearchButton(false)
        entityData.buffCoefficients?.atk()
            ?.size()?.coefficientsMap?.forEach { if (it.value != 1.0) add(it.key).setPercentsAsCoefficient(it.value) }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted() }
        fieldsSaver.add { entity ->
            if (selected.isEmpty()) return@add
            if (entity.buffCoefficients == null) entity.buffCoefficients = Coefficients()
            else Size.values().forEach { entity.buffCoefficients.atk().size().set(it, 1.0) }
            selectedElementsNodes.forEach { entity.buffCoefficients.atk().size().set(it.value, it.coefficient) }
        }
    }

    val buffSizeDef = SelectorContainer.withPercents(ObservableEnums.SIZES).get().apply {
        setHasSearchButton(false)
        entityData.buffCoefficients?.def()
            ?.size()?.coefficientsMap?.forEach { if (it.value != 1.0) add(it.key).setPercentsAsCoefficient(it.value) }
        changesChecker.add(this) { selected.sorted() to selectedElementsNodes.map { n -> n.textField.text }.sorted() }
        fieldsSaver.add { entity ->
            if (selected.isEmpty()) return@add
            if (entity.buffCoefficients == null) entity.buffCoefficients = Coefficients()
            else Size.values().forEach { entity.buffCoefficients.def().size().set(it, 1.0) }
            selectedElementsNodes.forEach { entity.buffCoefficients.def().size().set(it.value, it.coefficient) }
        }
    }

    val description = textarea {
        text = entityData.description
        changesChecker.add(this) { text }
        fieldsSaver.add { it.description = text }
    }

    val skillIcon = ImagePicker(
        header = "Icon (64x64)",
        imageWidthRestriction = 64.0,
        imageHeightRestriction = 64.0
    ).apply {
        imageResourceWrapper = Data.images[entityData.resources.skillIcon?.guid]
        changesChecker.add(this, true) { imageResourceWrapper?.guid }
        fieldsSaver.add { it.resources.skillIcon = imageResourceWrapper?.resource }
        imagePickers.add(this)
    }

    val skillSound = SoundPicker(
        header = "Sound",
        audioButtonAreaHeight = 64.0
    ).apply {
        soundResourceWrapper = Data.sounds[entityData.resources.skillSound?.guid]
        changesChecker.add(this) { soundResourceWrapper?.guid }
        fieldsSaver.add { it.resources.skillSound = soundResourceWrapper?.resource }
        soundPickers.add(this)
    }


    override val root = anchorpane {
        tabpane {
            fitToParentSize()
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("Logic") {
                hbox {
                    paddingAll = 10.0
                    spacing = 15.0
                    scrollpane {
                        isFitToHeight = true
                        vbox {
                            alignment = Pos.TOP_CENTER
                            spacing = 5.0
                            paddingAll = 5.0
                            prefWidth = 400.0
                            text("Main properties") {
                                font = Font.font(16.0)
                            }
                            hbox {
                                spacing = 10.0
                                gridpane {
                                    hgap = 5.0
                                    vgap = 5.0
                                    constraintsForColumn(1).maxWidth = 145.0
                                    row("Name", nameField)
                                    row("Name in editor", nameInEditorField)
                                    row("Main target", mainTarget)
                                    row("Sub targets", targets.apply { prefHeight = 80.0 })
                                    row("Skill type", skillType)
                                    row("Attack type", attackType)
                                    row("Damage", damage)
                                    row("Elements", elements.apply { prefHeight = 80.0 })
                                    row("Effect", effectField)
                                }
                                vbox {
                                    prefWidth = 200.0
                                    alignment = Pos.TOP_CENTER
                                    spacing = 5.0
                                    text("Damage coefficients:")
                                    text("Elements")
                                    add(damageElementCoefficients.apply { prefHeight = 80.0 })
                                    text("Being types")
                                    add(damageBeingTypeCoefficients.apply { prefHeight = 80.0 })
                                    text("Sizes")
                                    add(damageSizeCoefficients.apply { prefHeight = 80.0 })
                                    hbox {
                                        alignment = Pos.CENTER
                                        spacing = 5.0
                                        vbox {
                                            spacing = 5.0
                                            alignment = Pos.CENTER
                                            isFillWidth = true
                                            text("Can be used in:") {
                                                textAlignment = TextAlignment.CENTER
                                            }
                                            vbox {
                                                spacing = 5.0
                                                add(canBeUsedInBattle.apply { paddingLeft = 5.0 })
                                                add(canBeUsedInCamp.apply { paddingLeft = 5.0 })
                                                add(canBeUsedInMap.apply { paddingLeft = 5.0 })
                                            }
                                        }
                                        vbox {
                                            spacing = 5.0
                                            alignment = Pos.CENTER
                                            isFillWidth = true
                                            text("Can be:") {
                                                textAlignment = TextAlignment.CENTER
                                            }
                                            vbox {
                                                spacing = 5.0
                                                add(canBeBlocked.apply { paddingLeft = 5.0 })
                                                add(canBeResisted.apply { paddingLeft = 5.0 })
                                                add(canBeDodged.apply { paddingLeft = 5.0 })
                                            }
                                        }
                                    }
                                }
                            }
                            text("Skill chaining") {
                                font = Font.font(16.0)
                            }
                            hbox {
                                spacing = 10.0
                                vbox {
                                    alignment = Pos.CENTER
                                    prefWidth = 220.0
                                    text("Must cast")
                                    add(skillsMustCast.apply { prefHeight = 80.0 })
                                }
                                vbox {
                                    alignment = Pos.CENTER
                                    prefWidth = 220.0
                                    text("Can cast")
                                    add(skillsCanCast.apply { prefHeight = 80.0 })
                                }
                            }
                            text("Skills on being action")
                            add(skillsOnBeingAction.apply { prefHeight = 80.0 })
                            hbox {
                                spacing = 5.0
                                alignment = Pos.CENTER
                                add(onBeingActionCastToSelf)
                                add(onBeingActionCastToEnemy)
                            }
                        }
                    }
                    scrollpane {
                        isFitToHeight = true
                        vbox {
                            alignment = Pos.TOP_CENTER
                            spacing = 5.0
                            paddingAll = 5.0
                            prefWidth = 220.0
                            text("Requirements") {
                                font = Font.font(16.0)
                            }
                            hbox {
                                spacing = 15.0
                                alignment = Pos.CENTER
                                vbox {
                                    spacing = 5.0
                                    alignment = Pos.CENTER
                                    text("Stamina\nusage") {
                                        textAlignment = TextAlignment.CENTER
                                    }
                                    add(requiredStamina)
                                }
                                vbox {
                                    spacing = 5.0
                                    alignment = Pos.CENTER
                                    text("Concentration\nusage") {
                                        textAlignment = TextAlignment.CENTER
                                    }
                                    add(requiredConcentration)
                                }
                            }
                            text("Stats requirements")
                            add(statsRequirements.apply { prefHeight = 80.0 })
                            text("Items requirements")
                            add(itemsRequirements.apply { prefHeight = 80.0 })
                            hbox {
                                spacing = 5.0
                                alignment = Pos.CENTER
                                add(keepItems)
                                add(takeItems)
                            }
                            text("Summon") {
                                font = Font.font(16.0)
                            }
                            text("Monsters")
                            add(summon.apply { prefHeight = 80.0 })
                            text("Items")
                            add(receiveItems.apply { prefHeight = 80.0 })
                        }
                    }
                    scrollpane {
                        isFitToHeight = true
                        vbox {
                            spacing = 5.0
                            alignment = Pos.TOP_CENTER
                            paddingAll = 5.0
                            prefWidth = 770.0
                            text("Buff properties") {
                                font = Font.font(16.0)
                            }
                            hbox {
                                spacing = 20.0
                                alignment = Pos.TOP_CENTER
                                vbox {
                                    spacing = 5.0
                                    alignment = Pos.TOP_CENTER
                                    gridpane {
                                        hgap = 5.0
                                        vgap = 5.0
                                        row("Buff type", buffType)
                                        row("Duration in turns", duarationTurns)
                                        row("Duration in minutes", duarationMinutes)
                                        row {
                                            vbox {
                                                spacing = 15.0
                                                alignment = Pos.CENTER_LEFT
                                                add(permanent)
                                                add(recalculateEveryIteration)
                                            }
                                            vbox {
                                                spacing = 5.0
                                                alignment = Pos.CENTER
                                                text("Acts every")
                                                hbox {
                                                    alignment = Pos.CENTER
                                                    spacing = 5.0
                                                    vbox {
                                                        alignment = Pos.CENTER
                                                        spacing = 5.0
                                                        add(actsEveryTurn.apply { prefWidth = 50.0 })
                                                        add(actsEveryMinute.apply { prefWidth = 50.0 })
                                                    }
                                                    vbox {
                                                        alignment = Pos.CENTER_LEFT
                                                        spacing = 10.0
                                                        text("turns") {
                                                            textAlignment = TextAlignment.CENTER
                                                        }
                                                        text("minutes") {
                                                            textAlignment = TextAlignment.CENTER
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        row {
                                            text("Forced cancel\nafter") {
                                                textAlignment = TextAlignment.CENTER
                                            }
                                            vbox {
                                                hbox {
                                                    add(forcedCancelAfter.apply { prefWidth = 90.0 })
                                                    add(forcedCancelHitsOrDamage.apply { prefWidth = 90.0 })
                                                }
                                                add(forcedCancelReceivedOrDeal.apply { prefWidth = 180.0 })
                                            }
                                        }
                                        row("On duplicating", onDuplicating)
                                    }
                                    text("Transformation")
                                    text("Size")
                                    add(transformationSize.apply { maxWidth = 240.0 })
                                    hbox {
                                        spacing = 5.0
                                        alignment = Pos.CENTER
                                        vbox {
                                            spacing = 5.0
                                            alignment = Pos.CENTER
                                            prefWidth = 135.0
                                            text("Being types")
                                            add(transformationBeingTypes.apply { prefHeight = 80.0 })
                                        }
                                        vbox {
                                            spacing = 5.0
                                            alignment = Pos.CENTER
                                            prefWidth = 135.0
                                            text("Elements")
                                            add(transformationElements.apply { prefHeight = 80.0 })
                                        }
                                    }
                                    hbox {
                                        spacing = 5.0
                                        alignment = Pos.CENTER
                                        add(transformationAdd)
                                        add(transformationOverride)
                                    }
                                }
                                vbox {
                                    spacing = 5.0
                                    alignment = Pos.TOP_CENTER
                                    prefWidth = 400.0
                                    text("Buff stats")
                                    add(stats.apply { prefHeight = 160.0 })
                                    text("Buff coefficients")
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
                }
            }
            tab("Visual and sound") {
                hbox {
                    paddingAll = 10.0
                    vbox {
                        spacing = 5.0
                        alignment = Pos.TOP_CENTER
                        text("Main") {
                            font = Font.font(16.0)
                        }
                        hbox {
                            spacing = 15.0
                            add(skillIcon)
                            add(skillSound)
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
        val testBeing = Player()
        val skillParser = SkillParser(entityData, testBeing, testBeing)
        val messages: MutableList<String> = ArrayList()
        if (nameField.text.removeSpaces().isEmpty() && nameInEditorField.text.removeSpaces().isEmpty()) {
            messages.add("Either one of the fields NAME or NAME IN EDITOR must not be empty")
        }
        if (damage.text.removeSpaces().isNotEmpty() && !skillParser.testParse(damage.text.toUpperCase())) {
            messages.add("Formula in DAMAGE field is incorrect")
        }
        if (forcedCancelAfter.text.removeSpaces()
                .isNotEmpty() && !skillParser.testParse(forcedCancelAfter.text.toUpperCase())
        ) {
            messages.add("Formula in FORCED CANCEL AFTER field is incorrect")
        }
        stats.selectedElementsNodes.forEach {
            if (!skillParser.testParse(it.textField.text.removeSpaces().toUpperCase())) {
                messages.add("Formula in BUFF ${it.value.getName().toUpperCase()} field is incorrect")
            }
        }
        statsRequirements.selectedElementsNodes.forEach {
            if (it.textField.text.removeSpaces().isEmpty()) {
                messages.add("Requirement of stat ${it.value.getName().toUpperCase()} is empty")
            }
        }
        itemsRequirements.selectedElementsNodes.forEach {
            if (it.textField.text.removeSpaces().isEmpty()) {
                messages.add("Requirement amount of item ${it.value.entityData.nameInEditor.toUpperCase()} is empty")
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
        skillsMustCast.selectedElementsNodes.forEach {
            if (it.textField.text.removeSpaces().isEmpty()) {
                messages.add("Chance field of must cast ${it.value.entityData.nameInEditor.toUpperCase()} is empty")
            }
        }
        skillsCanCast.selectedElementsNodes.forEach {
            if (it.textField.text.removeSpaces().isEmpty()) {
                messages.add("Chance field of can cast ${it.value.entityData.nameInEditor.toUpperCase()} is empty")
            }
        }
        skillsOnBeingAction.selectedElementsNodes.forEach {
            val sk = it.skill
            if (sk == null) {
                messages.add("Skill on ${it.value!!.name.toUpperCase()} being action is empty")
            } else if (it.textField.text.removeSpaces().isEmpty()) {
                messages.add("Chance field of cast ${sk.entityData.nameInEditor.toUpperCase()} on ${it.value!!.name.toUpperCase()} is empty")
            }
        }
        return messages
    }

}