package com.rdude.rpgeditork.view.helper

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.enums.ObservableEnums
import com.rdude.rpgeditork.wrapper.ParticleResourceWrapper
import javafx.scene.control.ComboBox
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import ru.rdude.fxlib.boxes.SearchComboBox
import ru.rdude.rpg.game.visual.SkillAnimation
import tornadofx.*

class SkillAnimationSelectorElement() : HBox(), ParticleHolder {

    enum class SkillAnimationType(private val fieldSetter: (SkillAnimation.Entry, Long?) -> Unit) {

        DIRECTED({ entry, guid -> entry.directed = guid }),
        TARGET_BACK({ entry, guid -> entry.targetBack = guid }),
        TARGET_FRONT({ entry, guid -> entry.targetFront = guid }),
        CASTER_BACK({ entry, guid -> entry.casterBack = guid }),
        CASTER_FRONT({ entry, guid -> entry.casterFront = guid }),
        FULLSCREEN({ entry, guid -> entry.fullscreen = guid });

        fun setParticleTo(entry: SkillAnimation.Entry, particleGuid: Long?) {
            fieldSetter.invoke(entry, particleGuid)
        }

        companion object FindType {

            fun findType(entry: SkillAnimation.Entry): SkillAnimationType {
                if (entry.directed != null) {
                    return DIRECTED
                }
                else if (entry.targetBack != null) {
                    return TARGET_BACK
                }
                else if (entry.targetFront != null) {
                    return TARGET_FRONT
                }
                else if (entry.casterBack != null) {
                    return CASTER_BACK
                }
                else if (entry.casterFront != null) {
                    return CASTER_FRONT
                }
                else {
                    return FULLSCREEN
                }
            }
        }
    }

    constructor(entry: SkillAnimation.Entry) : this() {
        val particleGuid: Long
        if (entry.directed != null) {
            particleGuid = entry.directed
            animationType.value = SkillAnimationType.DIRECTED
        }
        else if (entry.targetBack != null) {
            particleGuid = entry.targetBack
            animationType.value = SkillAnimationType.TARGET_BACK
        }
        else if (entry.targetFront != null) {
            particleGuid = entry.targetFront
            animationType.value = SkillAnimationType.TARGET_FRONT
        }
        else if (entry.casterBack != null) {
            particleGuid = entry.casterBack
            animationType.value = SkillAnimationType.CASTER_BACK
        }
        else if (entry.casterFront != null) {
            particleGuid = entry.casterFront
            animationType.value = SkillAnimationType.CASTER_FRONT
        }
        else {
            particleGuid = entry.fullscreen
            animationType.value = SkillAnimationType.FULLSCREEN
        }
        // set particle
        particleComboBox.value = Data.particles[particleGuid]
        // entry order
        entryOrder.value = entry.entryOrder
        // direction
        direction.value = entry.direction
    }

    val entryOrder: ComboBox<SkillAnimation.EntryOrder> = combobox(values = ObservableEnums.SKILL_ANIMATION_ENTRY_ORDER)
        .apply {
            value = SkillAnimation.EntryOrder.ORDERED
            prefWidth = -1.0
            maxWidth = Double.MAX_VALUE
            hgrow = Priority.SOMETIMES
        }

    val direction: ComboBox<SkillAnimation.Direction> = combobox(values = ObservableEnums.SKILL_ANIMATION_DIRECTION)
        .apply {
            value = SkillAnimation.Direction.FORWARD
            prefWidth = -1.0
            maxWidth = Double.MAX_VALUE
            hgrow = Priority.SOMETIMES
        }

    val animationType: ComboBox<SkillAnimationType> = combobox(values = ObservableEnums.SKILL_ANIMATION_TYPE)
        .apply {
            prefWidth = -1.0
            maxWidth = Double.MAX_VALUE
            hgrow = Priority.SOMETIMES
            value = SkillAnimationType.DIRECTED
            valueProperty().onChange {
                // show / hide direction combo box
                if (it == SkillAnimationType.DIRECTED) {
                    this@SkillAnimationSelectorElement.add(direction)
                } else {
                    this@SkillAnimationSelectorElement.children.remove(direction)
                }
            }
        }

    val particleComboBox: SearchComboBox<ParticleResourceWrapper> = SearchComboBox(Data.particlesList)
        .apply {
            prefWidth = -1.0
            maxWidth = Double.MAX_VALUE
            hgrow = Priority.SOMETIMES
            setNameAndSearchBy { it?.name ?: "Select particle" }
        }

    override var particle: ParticleResourceWrapper?
        set(value) {
            particleComboBox.value = value
        }
        get() = particleComboBox.value

    init {
        fitToParentWidth()
        add(particleComboBox)
        add(animationType)
        add(entryOrder)
        add(direction)
    }

    fun generateEntry(): SkillAnimation.Entry {
        val entry = SkillAnimation.Entry()
        animationType.value.setParticleTo(entry, particle?.guid)
        entry.direction = direction.value
        entry.entryOrder = entryOrder.value
        return entry
    }

}