package com.rdude.rpgeditork.view.helper

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.enums.ObservableEnums
import com.rdude.rpgeditork.enums.SKILL
import com.rdude.rpgeditork.utils.dialogs.Dialogs
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.application.Platform
import javafx.beans.property.Property
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import ru.rdude.fxlib.boxes.SearchComboBox
import ru.rdude.fxlib.containers.selector.SelectorElementNode
import ru.rdude.rpg.game.logic.data.SkillData
import ru.rdude.rpg.game.logic.entities.beings.BeingAction
import tornadofx.*

class SkillsOnBeingActionSelectorElement : GridPane(), SelectorElementNode<BeingAction.Action> {

    val actionComboBox = SearchComboBox<BeingAction.Action>().apply {
        isSearchEnabled = false
        maxWidth = Double.MAX_VALUE
        minWidth = 0.0
        columnConstraints.add(ColumnConstraints().apply {
            percentWidth = 40.0
            isFillWidth = true
        })
        addColumn(0, this)
    }

    val skillsComboBox = SearchComboBox(Data.skillsList).apply {
        setNameByProperty { w -> w.entityNameProperty }
        setSearchBy( { w -> w.entityNameProperty.get() }, { w -> w.entityData.nameInEditor } )
        value = if (Data.skillsList.size > 0) Data.skillsList[0] else null
        maxWidth = Double.MAX_VALUE
        minWidth = 0.0
        columnConstraints.add(ColumnConstraints().apply {
            percentWidth = 35.0
            isFillWidth = true
        })
        addColumn(1, this)
    }

    val skillSearchButton = Button("\uD83D\uDD0E").apply {
        action {
            SKILL.defaultSearchDialog.showAndWait().ifPresent { skillsComboBox.value = it }
        }
        maxWidth = Double.MAX_VALUE
        columnConstraints.add(ColumnConstraints().apply {
            percentWidth = 10.0
            isFillWidth = true
        })
        addColumn(2, this)
    }

    val textField = TextField("100 %").apply {
        filterInput { it.controlNewText.replace(" %", "").isDouble() }
        columnConstraints.add(ColumnConstraints().apply {
            percentWidth = 15.0
            isFillWidth = true
        })
        caretPositionProperty().addListener { _, _, newV ->
            val max = this.text.length - 2;
            if (newV as Int > max && this.selectedTextProperty().get().isEmpty()) {
                Platform.runLater { this.positionCaret(max) }
            }
        }
        addColumn(3, this)
    }

    var skill: EntityDataWrapper<SkillData>?
        get() = skillsComboBox.value
        set(value) {
            skillsComboBox.value = value
        }

    var coefficient: Double
        get() {
            val str = textField.text.replace(" %", "")
            return if (str.isEmpty()) 1.0 else str.toDouble() / 100
        }
        set(value) {
            textField.text = (value * 100).toString().replace(Regex("\\.0+\\b"), "") + " %"
        }

    var percents: Double
        get() {
            val str = textField.text.replace(" %", "")
            return if (str.isEmpty()) 100.0 else str.toDouble()
        }
        set(value) {
            textField.text = value.toString().replace(Regex("\\.0+\\b"), "") + " %"
        }

    var action: BeingAction.Action
        get() = actionComboBox.value
        set(value) {
            actionComboBox.value = value
        }

    override fun valueProperty(): Property<BeingAction.Action> = actionComboBox.valueProperty()

    override fun getValue(): BeingAction.Action? = actionComboBox.value

    override fun setValue(value: BeingAction.Action?) {
        actionComboBox.value = value
    }

    override fun setCollection(collection: MutableCollection<BeingAction.Action>?) {
        actionComboBox.setCollection(collection)
    }
}

