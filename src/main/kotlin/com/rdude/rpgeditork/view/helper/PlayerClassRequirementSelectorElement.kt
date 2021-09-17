package com.rdude.rpgeditork.view.helper

import com.rdude.rpgeditork.enums.ObservableEnums
import com.rdude.rpgeditork.enums.StatisticType
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.text.Text
import ru.rdude.rpg.game.logic.entities.beings.BeingAction
import ru.rdude.rpg.game.logic.enums.AttackType
import ru.rdude.rpg.game.logic.enums.UsedByStatistics
import tornadofx.*

class PlayerClassRequirementSelectorElement: GridPane() {

    private val beingActionComboBox = ComboBox(ObservableEnums.BEING_ACTIONS).apply {
        value = BeingAction.Action.DAMAGE_DEAL
    }
    var beingAction: BeingAction.Action
        get() = beingActionComboBox.value
        set(value) { beingActionComboBox.value = value }

    private val typeComboBox = ComboBox(ObservableEnums.STATISTIC_TYPE).apply {
        value = StatisticType.ATTACK_TYPE
        valueProperty().onChange {
            concreteComboBox.items = it!!.observableEnum as ObservableList<UsedByStatistics>
            concreteComboBox.value = concreteComboBox.items.first()
        }
    }
    var type: StatisticType
        get() = typeComboBox.value
        set(value) { typeComboBox.value = value }

    private val concreteComboBox = ComboBox(FXCollections.observableArrayList<UsedByStatistics>()).apply {
        value = AttackType.MELEE
        items = ObservableEnums.ATTACK_TYPES as ObservableList<UsedByStatistics>
    }
    var concreteType: UsedByStatistics
        get() = concreteComboBox.value
        set(value) {concreteComboBox.value = value}

    private val pointsForValueTextField = TextField().apply {
        filterInput { it.controlNewText.isInt() && it.controlNewText.toInt() >= 0 }
    }
    var pointsForValue: Int = 0
        get() = with(pointsForValueTextField.text) { if (this.isBlank()) 0 else this.toInt() }
        set(value) { field = value; pointsForValueTextField.text = value.toString() }

    private val pointsForEachUseTextField = TextField().apply {
        filterInput { it.controlNewText.isInt() && it.controlNewText.toInt() >= 0 }
    }
    var pointsForEachUse: Int = 0
        get() = with(pointsForEachUseTextField.text) { if (this.isBlank()) 0 else this.toInt() }
        set(value) { field = value; pointsForEachUseTextField.text = value.toString() }

    init {
        add(Text("  Action: "), 0, 0)
        add(beingActionComboBox.apply { maxWidth = 130.0; minWidth = 130.0 }, 1, 0)
        add(Text("  Type: "), 2, 0)
        add(typeComboBox.apply { maxWidth = 130.0; minWidth = 130.0 }, 3, 0)
        add(Text("  Concrete: "), 4, 0)
        add(concreteComboBox.apply { maxWidth = 130.0; minWidth = 130.0 }, 5, 0)
        add(Text("  Points for each value: "), 6, 0)
        add(pointsForValueTextField.apply { maxWidth = 50.0 }, 7, 0)
        add(Text("  Points for each use: "), 8, 0)
        add(pointsForEachUseTextField.apply { maxWidth = 50.0 }, 9, 0)
    }

}