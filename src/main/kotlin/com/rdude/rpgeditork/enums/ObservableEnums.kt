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
    val SKILL_TYPES: ObservableList<SkillType> = FXCollections.observableArrayList(SkillType.values().asList())
    val MAIN_TARGETS: ObservableList<Target> = Target.values().filter { it.isCanBeMainTarget }.asObservable()
    val SUB_TARGETS: ObservableList<Target> = Target.values().filter { it.isCanBeSubTarget }.asObservable()
    val ELEMENTS: ObservableList<Element> = Element.values().asList().asObservable()
    val BEING_TYPES: ObservableList<BeingType> = BeingType.values().asList().asObservable()
    val SIZES: ObservableList<Size> = Size.values().asList().asObservable()
    val SIZES_WITH_NULL: ObservableList<NullableSize> = NullableSize.values().asList().asObservable()
    val SKILL_EFFECTS: ObservableList<SkillEffect> = SkillEffect.values().asList().asObservable()
    val SKILL_OVERLAY: ObservableList<SkillOverlay> = SkillOverlay.values().asList().asObservable()
    val BUFF_TYPES: ObservableList<BuffType> = BuffType.values().asList().asObservable()
    val STAT_NAMES: ObservableList<StatName> = StatName.values().asList().asObservable()
    val FORMULA_VARIABLES: ObservableList<FormulaVariable> = FormulaVariable.values().asList().asObservable()
    val BEING_ACTIONS: ObservableList<BeingAction.Action> = BeingAction.Action.values().asList().asObservable()
}