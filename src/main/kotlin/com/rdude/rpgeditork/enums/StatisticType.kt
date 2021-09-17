package com.rdude.rpgeditork.enums

import javafx.collections.ObservableList
import ru.rdude.rpg.game.logic.enums.UsedByStatistics

enum class StatisticType(val observableEnum: ObservableList<out UsedByStatistics>) {

    ATTACK_TYPE(ObservableEnums.ATTACK_TYPES),
    BEING_TYPE(ObservableEnums.BEING_TYPES),
    BUFF_TYPE(ObservableEnums.BUFF_TYPES),
    ELEMENT(ObservableEnums.ELEMENTS),
    ITEM_MAIN_TYPE(ObservableEnums.ITEM_MAIN_TYPES),
    ITEM_TYPE(ObservableEnums.ITEM_TYPES),
    SIZE(ObservableEnums.SIZES),
    SKILL_TYPE(ObservableEnums.SKILL_TYPES);

    companion object TypeOf {
        fun typeOf(value: UsedByStatistics): StatisticType? {
            return values().find { it.observableEnum.contains(value) }
        }
    }

}