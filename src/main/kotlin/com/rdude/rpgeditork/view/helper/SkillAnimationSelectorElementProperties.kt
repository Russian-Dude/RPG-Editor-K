package com.rdude.rpgeditork.view.helper

import com.rdude.rpgeditork.enums.ObservableEnums
import javafx.scene.control.ComboBox
import javafx.scene.layout.GridPane
import javafx.scene.text.Text
import ru.rdude.rpg.game.visual.SkillAnimation
import tornadofx.constraintsForColumn
import tornadofx.onChange
import tornadofx.paddingAll

class SkillAnimationSelectorElementProperties : GridPane() {

    init {
        paddingAll = 10.0
        hgap = 10.0
        vgap = 10.0
    }

    enum class SkillAnimationType(private val fieldSetter: (SkillAnimation.Entry, Long?) -> Unit) {

        DIRECTED({ entry, guid -> entry.directed = guid }),
        TARGET_BACK({ entry, guid -> entry.targetBack = guid }),
        TARGET_FRONT({ entry, guid -> entry.targetFront = guid }),
        CASTER_BACK({ entry, guid -> entry.casterBack = guid }),
        CASTER_FRONT({ entry, guid -> entry.casterFront = guid }),
        FULLSCREEN({ entry, guid -> entry.fullscreen = guid });

        fun setToField(entry: SkillAnimation.Entry, guid: Long) {
            fieldSetter.invoke(entry, guid)
        }

        companion object Find {

            fun findGuid(entry: SkillAnimation.Entry): Long {
                if (entry.directed != null) {
                    return entry.directed
                } else if (entry.targetBack != null) {
                    return entry.targetBack
                } else if (entry.targetFront != null) {
                    return entry.targetFront
                } else if (entry.casterBack != null) {
                    return entry.casterBack
                } else if (entry.casterFront != null) {
                    return entry.casterFront
                } else {
                    return entry.fullscreen
                }
            }

            fun findType(entry: SkillAnimation.Entry): SkillAnimationType {
                if (entry.directed != null) {
                    return DIRECTED
                } else if (entry.targetBack != null) {
                    return TARGET_BACK
                } else if (entry.targetFront != null) {
                    return TARGET_FRONT
                } else if (entry.casterBack != null) {
                    return CASTER_BACK
                } else if (entry.casterFront != null) {
                    return CASTER_FRONT
                } else {
                    return FULLSCREEN
                }
            }
        }
    }

    val entryOrderComboBox = ComboBox(ObservableEnums.SKILL_ANIMATION_ENTRY_ORDER).apply {
        value = SkillAnimation.EntryOrder.ORDERED
        add(Text("Order relative to previous entry"), 0, 0)
        add(this, 1, 0)
        maxWidth = Double.MAX_VALUE
        setFillWidth(this, true)
    }

    val animationTypeComboBox = ComboBox(ObservableEnums.SKILL_ANIMATION_TYPE).apply {
        value = SkillAnimationType.DIRECTED
        valueProperty().onChange {
            directionComboBox.isDisable = it != SkillAnimationType.DIRECTED
        }
        add(Text("Animation type"), 0, 1)
        add(this, 1, 1)
        maxWidth = Double.MAX_VALUE
        setFillWidth(this, true)
    }

    val directionComboBox = ComboBox(ObservableEnums.SKILL_ANIMATION_DIRECTION).apply {
        value = SkillAnimation.Direction.FORWARD
        add(Text("Direction"), 0, 2)
        add(this, 1, 2)
        maxWidth = Double.MAX_VALUE
        setFillWidth(this, true)
    }

    var entryOrder: SkillAnimation.EntryOrder
        get() = entryOrderComboBox.value
        set(value) {
            entryOrderComboBox.value = value
        }

    var direction: SkillAnimation.Direction
        get() = directionComboBox.value
        set(value) {
            directionComboBox.value = value
        }

    var animationType: SkillAnimationType
        get() = animationTypeComboBox.value
        set(value) {
            animationTypeComboBox.value = value
        }

}