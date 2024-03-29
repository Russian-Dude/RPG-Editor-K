package com.rdude.rpgeditork.enums

import com.rdude.rpgeditork.view.helper.SkillAnimationSelectorElementProperties
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import ru.rdude.rpg.game.logic.data.QuestData
import ru.rdude.rpg.game.logic.entities.beings.BeingAction
import ru.rdude.rpg.game.logic.enums.*
import ru.rdude.rpg.game.logic.enums.Target
import ru.rdude.rpg.game.visual.SkillAnimation
import tornadofx.asObservable
import tornadofx.toObservable

object ObservableEnums {
    val ATTACK_TYPES: ObservableList<AttackType> = FXCollections.observableArrayList(AttackType.values().asList())
    val ATTACK_WITHOUT_WEAPON_TYPE: ObservableList<AttackType> = FXCollections.observableArrayList(AttackType.values().asList().filter { at -> at != AttackType.WEAPON_TYPE })
    val ATTACK_WITHOUT_WEAPON_TYPE_NULLABLE: ObservableList<AttackType?> = FXCollections.observableArrayList(AttackType.values().asList().filter { at -> at != AttackType.WEAPON_TYPE }).apply { add(0, null) }
    val ATTACK_TYPES_NULLABLE: ObservableList<AttackType?> = FXCollections.observableArrayList(AttackType.values().asList()).apply { add(0, null) }
    val SKILL_TYPES: ObservableList<SkillType> = FXCollections.observableArrayList(SkillType.values().asList())
    val SKILL_TYPES_NULLABLE: ObservableList<SkillType?> = FXCollections.observableArrayList(SkillType.values().asList()).apply { add(0, null) }
    val MAIN_TARGETS: ObservableList<Target> = Target.values().filter { it.isCanBeMainTarget }.asObservable()
    val SUB_TARGETS: ObservableList<Target> = Target.values().filter { it.isCanBeSubTarget }.asObservable()
    val ELEMENTS: ObservableList<Element> = Element.values().toMutableList().asObservable()
    val BEING_TYPES: ObservableList<BeingType> = BeingType.values().toMutableList().asObservable()
    val SIZES: ObservableList<Size> = Size.values().toMutableList().asObservable()
    val SIZES_NULLABLE: ObservableList<Size?> = Size.values().toMutableList<Size?>().asObservable().apply { add(0, null) }
    val SIZES_WITH_NULL: ObservableList<NullableSize> = NullableSize.values().toMutableList().asObservable()
    val SKILL_EFFECTS: ObservableList<SkillEffect> = SkillEffect.values().toMutableList().asObservable()
    val SKILL_EFFECTS_NULLABLE: ObservableList<SkillEffect?> = SkillEffect.values().toMutableList<SkillEffect?>().asObservable().apply { add(0, null) }
    val SKILL_OVERLAY: ObservableList<SkillOverlay> = SkillOverlay.values().toMutableList().asObservable()
    val BUFF_TYPES: ObservableList<BuffType> = BuffType.values().toMutableList().asObservable()
    val BUFF_TYPES_NULLABLE: ObservableList<BuffType?> = BuffType.values().toMutableList<BuffType?>().asObservable().apply { add(0, null) }
    val STAT_NAMES: ObservableList<StatName> = StatName.values().toMutableList().asObservable()
    val FORMULA_VARIABLES: ObservableList<FormulaVariable> = FormulaVariable.values().toMutableList().asObservable()
    val BEING_ACTIONS: ObservableList<BeingAction.Action> = BeingAction.Action.values().toMutableList().asObservable()
    val ITEM_MAIN_TYPES: ObservableList<ItemMainType> = ItemMainType.values().toMutableList().asObservable()
    val ITEM_MAIN_TYPES_NULLABLE: ObservableList<ItemMainType?> = ItemMainType.values().toMutableList<ItemMainType?>().asObservable().apply { add(0, null) }
    val ITEM_TYPES: ObservableList<ItemType> = ItemType.values().toMutableList().asObservable()
    val ITEM_TYPES_NULLABLE: ObservableList<ItemType?> = ItemType.values().toMutableList<ItemType?>().asObservable().apply { add(0, null) }
    val ITEM_RARITY: ObservableList<ItemRarity> = ItemRarity.values().toMutableList().asObservable()
    val ITEM_RARITY_NULLABLE: ObservableList<ItemRarity?> = ItemRarity.values().toMutableList<ItemRarity?>().asObservable().apply { add(0, null) }
    val BIOMS: ObservableList<Biom> = Biom.values().toMutableList().asObservable()
    val BIOMS_NULLABLE: ObservableList<Biom?> = Biom.values().toMutableList<Biom?>().asObservable().apply { add(0, null) }
    val RELIEFS: ObservableList<Relief> = Relief.values().toMutableList().asObservable()
    val RELIEFS_NULLABLE: ObservableList<Relief?> = Relief.values().toMutableList<Relief?>().asObservable().apply { add(0, null) }
    val ENTITY_REFERENCE_INFO: ObservableList<EntityReferenceInfo> = EntityReferenceInfo.values().toMutableList().asObservable()
    val ENTITY_INFO: ObservableList<EntityReferenceInfo> = EntityReferenceInfo.values().toMutableList().asObservable().apply { removeAll(EntityReferenceInfo.NO, EntityReferenceInfo.INTEGRATED) }
    val SKILL_ANIMATION_ENTRY_ORDER: ObservableList<SkillAnimation.EntryOrder> = SkillAnimation.EntryOrder.values().toMutableList().asObservable()
    val SKILL_ANIMATION_SUB_TARGETS_ORDER: ObservableList<SkillAnimation.SubTargetsOrder> = SkillAnimation.SubTargetsOrder.values().toMutableList().asObservable()
    val SKILL_ANIMATION_DIRECTION: ObservableList<SkillAnimation.Direction> = SkillAnimation.Direction.values().toMutableList().asObservable()
    val SKILL_ANIMATION_TYPE: ObservableList<SkillAnimationSelectorElementProperties.SkillAnimationType> = SkillAnimationSelectorElementProperties.SkillAnimationType.values().toMutableList().asObservable()
    val STATISTIC_TYPE: ObservableList<StatisticType> = StatisticType.values().toMutableList().asObservable()
    val QUEST_END_PLACE: ObservableList<QuestData.EndQuestPlace> = QuestData.EndQuestPlace.values().toMutableList().asObservable()
    val QUEST_REWARD_TARGET: ObservableList<QuestData.RewardTarget> = QuestData.RewardTarget.values().toMutableList().asObservable()
    val QUEST_UNIQUE: ObservableList<QuestData.Unique> = QuestData.Unique.values().toMutableList().asObservable()
    val PRIMARY_STATS: ObservableList<StatName> = StatName.values().filter { it.isPrimary }.toMutableList().toObservable()
}