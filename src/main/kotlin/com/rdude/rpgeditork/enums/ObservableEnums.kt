package com.rdude.rpgeditork.enums

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import ru.rdude.rpg.game.logic.entities.beings.BeingAction
import ru.rdude.rpg.game.logic.enums.*
import ru.rdude.rpg.game.logic.enums.Target
import tornadofx.asObservable
import tornadofx.observable

object ObservableEnums {
    val ATTACK_TYPES: ObservableList<AttackType> = FXCollections.observableArrayList(AttackType.values().asList())
    val ATTACK_TYPES_NULLABLE: ObservableList<AttackType?> = FXCollections.observableArrayList(AttackType.values().asList()).apply { add(null) }
    val SKILL_TYPES: ObservableList<SkillType> = FXCollections.observableArrayList(SkillType.values().asList())
    val SKILL_TYPES_NULLABLE: ObservableList<SkillType?> = FXCollections.observableArrayList(SkillType.values().asList()).apply { add(null) }
    val MAIN_TARGETS: ObservableList<Target> = Target.values().filter { it.isCanBeMainTarget }.asObservable()
    val SUB_TARGETS: ObservableList<Target> = Target.values().filter { it.isCanBeSubTarget }.asObservable()
    val ELEMENTS: ObservableList<Element> = Element.values().toMutableList().asObservable()
    val BEING_TYPES: ObservableList<BeingType> = BeingType.values().toMutableList().asObservable()
    val SIZES: ObservableList<Size> = Size.values().toMutableList().asObservable()
    val SIZES_WITH_NULL: ObservableList<NullableSize> = NullableSize.values().toMutableList().asObservable()
    val SKILL_EFFECTS: ObservableList<SkillEffect> = SkillEffect.values().toMutableList().asObservable()
    val SKILL_EFFECTS_NULLABLE: ObservableList<SkillEffect?> = SkillEffect.values().toMutableList<SkillEffect?>().asObservable().apply { add(null) }
    val SKILL_OVERLAY: ObservableList<SkillOverlay> = SkillOverlay.values().toMutableList().asObservable()
    val BUFF_TYPES: ObservableList<BuffType> = BuffType.values().toMutableList().asObservable()
    val STAT_NAMES: ObservableList<StatName> = StatName.values().toMutableList().asObservable()
    val FORMULA_VARIABLES: ObservableList<FormulaVariable> = FormulaVariable.values().toMutableList().asObservable()
    val BEING_ACTIONS: ObservableList<BeingAction.Action> = BeingAction.Action.values().toMutableList().asObservable()
}