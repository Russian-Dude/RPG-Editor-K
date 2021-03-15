package com.rdude.rpgeditork.view.search

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.enums.ObservableEnums
import com.rdude.rpgeditork.utils.setNullToStringConverter
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.ColumnConstraints
import javafx.scene.text.Text
import ru.rdude.fxlib.containers.selector.SelectorContainer
import ru.rdude.fxlib.dialogs.SearchDialog
import ru.rdude.rpg.game.logic.data.SkillData
import ru.rdude.rpg.game.logic.enums.GameState
import tornadofx.*

class SkillSearchView : EntitySearchView<SkillData>() {

    override fun configPopup(searchDialog: SearchDialog<EntityDataWrapper<SkillData>>) {
        searchDialog.searchPane.popupBuilder()
            .addText { it.entityData.description }
            .addText { "In game name: ${it.entityData.name}" }
            .addText { "Type: ${it.entityData.type}" }
            .addText { "Damage: ${it.entityData.damage}" }
            .addText { "Attack type: ${it.entityData.attackType}" }
            .addText { "Stamina required: ${it.entityData.staminaReq}" }
            .addText { "Concentration required: ${it.entityData.concentrationReq}" }

            .addText { "Elements: ${it.entityData.elements
                        .map { el -> el.toString() }
                        .ifEmpty { listOf("-") }
                        .reduce { a, b -> "$a, $b" }}" }

            .addText { "Stats: ${it.entityData.stats
                        .filter { st -> st.value.isNotBlank() }
                        .map { st -> "${st.key.getName()}: ${st.value}" }
                        .ifEmpty { listOf("-") }
                        .reduce { a, b -> "$a\r\n       $b" }}" }

            .addText { "Summon: ${it.entityData.summon
                        .mapNotNull { entry -> Data.monstersMap[entry.key]?.entityData?.nameInEditor }
                        .ifEmpty { listOf("-") }
                        .reduce { a, b -> "$a\r\n        $b" }}" }

            .addText { "Duration: ${
                if (!it.entityData.durationInTurns.isNullOrBlank()) "${it.entityData.durationInTurns} turns " else ""
            }${
                if (!it.entityData.durationInMinutes.isNullOrBlank()) "${it.entityData.durationInMinutes} minutes " else ""
            }" }

            .addText { "Skill chaining: ${
                setOf(it.entityData.skillsCouldCast.keys, it.entityData.skillsMustCast.keys,
                it.entityData.skillsOnBeingAction.values.flatMap { m -> m.keys })
                    .flatten()
                    .mapNotNull { guid -> Data.skillsMap[guid]?.entityData?.nameInEditor }
                    .ifEmpty { listOf("-") }
                    .reduce { a, b ->  "$a\r\n                $b"}
            }" }
            .apply()
    }

    override val root = hbox {
        paddingLeft = 15.0
        spacing = 25.0
        vbox {
            spacing = 15.0
            alignment = Pos.TOP_CENTER

            add(resetSearchButton)

            combobox(values = listOf(null, "Describer", "Regular")) {
                maxWidth = Double.MAX_VALUE
                setNullToStringConverter("Any")
                searchOptions.put(this) { if (it.entityData.isDescriber) "Describer" else "Regular" }
                resetSearchFunctions.add { value = null }
            }

            vbox {
                text("Type")
                combobox(values = ObservableEnums.SKILL_TYPES_NULLABLE) {
                    maxWidth = Double.MAX_VALUE
                    setNullToStringConverter("Any")
                    searchOptions.put(this) { it.entityData.type }
                    resetSearchFunctions.add { value = null }
                }
            }

            vbox {
                text("Attack type")
                combobox(values = ObservableEnums.ATTACK_TYPES_NULLABLE) {
                    maxWidth = Double.MAX_VALUE
                    setNullToStringConverter("Any")
                    searchOptions.put(this) { it.entityData.attackType }
                    resetSearchFunctions.add { value = null }
                }
            }

            vbox {
                text("Effect")
                combobox(values = ObservableEnums.SKILL_EFFECTS_NULLABLE) {
                    maxWidth = Double.MAX_VALUE
                    setNullToStringConverter("Any")
                    searchOptions.put(this) { it.entityData.effect }
                    resetSearchFunctions.add { value = null }
                }
            }

            vbox {
                text("Elements")
                val selectorContainer = SelectorContainer.simple(ObservableEnums.ELEMENTS).apply {
                    setHasSearchButton(false)
                    prefHeight = 80.0
                    searchOptions.put(this) { it.entityData.elements }
                    resetSearchFunctions.add { clear() }
                }
                add(selectorContainer)
            }

            vbox {
                text("Can be used in")
                gridpane {
                    columnConstraints.addAll(
                        ColumnConstraints().apply {
                            prefWidth = 50.0
                            halignment = HPos.CENTER
                            hgap = 5.0
                        },
                        ColumnConstraints().apply {
                            halignment = HPos.CENTER
                            hgap = 5.0
                        },
                        ColumnConstraints().apply {
                            halignment = HPos.CENTER
                            hgap = 5.0
                        },
                        ColumnConstraints().apply {
                            halignment = HPos.CENTER
                            hgap = 5.0
                        }
                    )
                    row {
                        text("")
                        text("Yes")
                        text("No")
                        text("Any")
                    }
                    row {
                        val toggle = ToggleGroup()
                        text("Battle")
                        radiobutton {
                            isSelected = false
                            toggleGroup = toggle
                            searchOptions.put(this) { it.entityData.usableInGameStates[GameState.BATTLE] }
                            resetSearchFunctions.add { isSelected = false }
                        }
                        radiobutton {
                            isSelected = false
                            toggleGroup = toggle
                            searchOptions.put(this) { !it.entityData.usableInGameStates[GameState.BATTLE]!! }
                            resetSearchFunctions.add { isSelected = false }
                        }
                        radiobutton {
                            isSelected = true
                            toggleGroup = toggle
                            searchOptions.put(this) { true }
                            resetSearchFunctions.add { isSelected = true }
                        }
                    }
                    row {
                        val toggle = ToggleGroup()
                        text("Map")
                        radiobutton {
                            isSelected = false
                            toggleGroup = toggle
                            searchOptions.put(this) { it.entityData.usableInGameStates[GameState.MAP] }
                            resetSearchFunctions.add { isSelected = false }
                        }
                        radiobutton {
                            isSelected = false
                            toggleGroup = toggle
                            searchOptions.put(this) { !it.entityData.usableInGameStates[GameState.MAP]!! }
                            resetSearchFunctions.add { isSelected = false }
                        }
                        radiobutton {
                            isSelected = true
                            toggleGroup = toggle
                            searchOptions.put(this) { true }
                            resetSearchFunctions.add { isSelected = true }
                        }
                    }
                    row {
                        val toggle = ToggleGroup()
                        text("Camp")
                        radiobutton {
                            isSelected = false
                            toggleGroup = toggle
                            searchOptions.put(this) { it.entityData.usableInGameStates[GameState.CAMP] }
                            resetSearchFunctions.add { isSelected = false }
                        }
                        radiobutton {
                            isSelected = false
                            toggleGroup = toggle
                            searchOptions.put(this) { !it.entityData.usableInGameStates[GameState.CAMP]!! }
                            resetSearchFunctions.add { isSelected = false }
                        }
                        radiobutton {
                            isSelected = true
                            toggleGroup = toggle
                            searchOptions.put(this) { true }
                            resetSearchFunctions.add { isSelected = true }
                        }
                    }
                }
            }

            vbox {
                text("Can be")
                gridpane {
                    columnConstraints.addAll(
                        ColumnConstraints().apply {
                            prefWidth = 50.0
                            halignment = HPos.CENTER
                            hgap = 5.0
                        },
                        ColumnConstraints().apply {
                            halignment = HPos.CENTER
                            hgap = 5.0
                        },
                        ColumnConstraints().apply {
                            halignment = HPos.CENTER
                            hgap = 5.0
                        },
                        ColumnConstraints().apply {
                            halignment = HPos.CENTER
                            hgap = 5.0
                        }
                    )
                    row {
                        text("")
                        text("Yes")
                        text("No")
                        text("Any")
                    }
                    row {
                        val toggle = ToggleGroup()
                        text("Blocked")
                        radiobutton {
                            isSelected = false
                            toggleGroup = toggle
                            searchOptions.put(this) { it.entityData.isCanBeBlocked }
                            resetSearchFunctions.add { isSelected = false }
                        }
                        radiobutton {
                            isSelected = false
                            toggleGroup = toggle
                            searchOptions.put(this) { !it.entityData.isCanBeBlocked }
                            resetSearchFunctions.add { isSelected = false }
                        }
                        radiobutton {
                            isSelected = true
                            toggleGroup = toggle
                            searchOptions.put(this) { true }
                            resetSearchFunctions.add { isSelected = true }
                        }
                    }
                    row {
                        val toggle = ToggleGroup()
                        text("Resisted")
                        radiobutton {
                            isSelected = false
                            toggleGroup = toggle
                            searchOptions.put(this) { it.entityData.isCanBeResisted }
                            resetSearchFunctions.add { isSelected = false }
                        }
                        radiobutton {
                            isSelected = false
                            toggleGroup = toggle
                            searchOptions.put(this) { !it.entityData.isCanBeResisted }
                            resetSearchFunctions.add { isSelected = false }
                        }
                        radiobutton {
                            isSelected = true
                            toggleGroup = toggle
                            searchOptions.put(this) { true }
                            resetSearchFunctions.add { isSelected = true }
                        }
                    }
                    row {
                        val toggle = ToggleGroup()
                        text("Dodged")
                        radiobutton {
                            isSelected = false
                            toggleGroup = toggle
                            searchOptions.put(this) { it.entityData.isCanBeDodged }
                            resetSearchFunctions.add { isSelected = false }
                        }
                        radiobutton {
                            isSelected = false
                            toggleGroup = toggle
                            searchOptions.put(this) { !it.entityData.isCanBeDodged }
                            resetSearchFunctions.add { isSelected = false }
                        }
                        radiobutton {
                            isSelected = true
                            toggleGroup = toggle
                            searchOptions.put(this) { true }
                            resetSearchFunctions.add { isSelected = true }
                        }
                    }
                }
            }
        }

        vbox {
            spacing = 10.0
            alignment = Pos.TOP_CENTER

            vbox {
                text("Deal damage")
                val toggle = ToggleGroup()
                radiobutton {
                    text = "Not important"
                    isSelected = true
                    toggleGroup = toggle
                    searchOptions.put(this) { true }
                    resetSearchFunctions.add { isSelected = true }
                }
                radiobutton {
                    text = "Yes"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.damage != null && it.entityData.damage.isNotBlank() }
                    resetSearchFunctions.add { isSelected = false }
                }
                radiobutton {
                    text = "No"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.damage.isNullOrBlank() }
                    resetSearchFunctions.add { isSelected = false }
                }
            }

            vbox {
                text("Change stats")
                val toggle = ToggleGroup()
                radiobutton {
                    text = "Not important"
                    isSelected = true
                    toggleGroup = toggle
                    searchOptions.put(this) { true }
                    resetSearchFunctions.add { isSelected = true }
                }
                radiobutton {
                    text = "Yes"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) {
                        it.entityData.stats.filter { it.value != null && it.value.isNotBlank() }.any()
                    }
                    resetSearchFunctions.add { isSelected = false }
                }
                radiobutton {
                    text = "No"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.stats.filter { it.value.isNullOrBlank() }.any() }
                    resetSearchFunctions.add { isSelected = false }
                }
            }

            vbox {
                text("Receive items")
                val toggle = ToggleGroup()
                radiobutton {
                    text = "Not important"
                    isSelected = true
                    toggleGroup = toggle
                    searchOptions.put(this) { true }
                    resetSearchFunctions.add { isSelected = true }
                }
                radiobutton {
                    text = "Yes"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.receiveItems.isNotEmpty() }
                    resetSearchFunctions.add { isSelected = false }
                }
                radiobutton {
                    text = "No"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.receiveItems.isEmpty() }
                    resetSearchFunctions.add { isSelected = false }
                }
            }

            vbox {
                text("Require items")
                val toggle = ToggleGroup()
                radiobutton {
                    text = "Not important"
                    isSelected = true
                    toggleGroup = toggle
                    searchOptions.put(this) { true }
                    resetSearchFunctions.add { isSelected = true }
                }
                radiobutton {
                    text = "Yes"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.requirements.items.isNotEmpty() }
                    resetSearchFunctions.add { isSelected = false }
                }
                radiobutton {
                    text = "No"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.requirements.items.isEmpty() }
                    resetSearchFunctions.add { isSelected = false }
                }
            }

            vbox {
                text("Has duration")
                val toggle = ToggleGroup()
                radiobutton {
                    text = "Not important"
                    isSelected = true
                    toggleGroup = toggle
                    searchOptions.put(this) { true }
                    resetSearchFunctions.add { isSelected = true }
                }
                radiobutton {
                    text = "Yes"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) {
                        (it.entityData.durationInMinutes != null && it.entityData.durationInMinutes.isNotEmpty())
                                || (it.entityData.durationInTurns != null && it.entityData.durationInTurns.isNotEmpty())
                    }
                    resetSearchFunctions.add { isSelected = false }
                }
                radiobutton {
                    text = "No"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.durationInMinutes.isNullOrBlank() && it.entityData.durationInTurns.isNullOrBlank() }
                    resetSearchFunctions.add { isSelected = false }
                }
            }

            vbox {
                text("Apply transformation")
                val toggle = ToggleGroup()
                radiobutton {
                    text = "Not important"
                    isSelected = true
                    toggleGroup = toggle
                    searchOptions.put(this) { true }
                    resetSearchFunctions.add { isSelected = true }
                }
                radiobutton {
                    text = "Yes"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) {
                        it.entityData.transformation.beingTypes.isNotEmpty()
                                || it.entityData.transformation.elements.isNotEmpty()
                                || it.entityData.transformation.size != null
                    }
                    resetSearchFunctions.add { isSelected = false }
                }
                radiobutton {
                    text = "No"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) {
                        it.entityData.transformation.beingTypes.isEmpty()
                                && it.entityData.transformation.elements.isEmpty()
                                && it.entityData.transformation.size == null
                    }
                    resetSearchFunctions.add { isSelected = false }
                }
            }

            vbox {
                text("Summon")
                val toggle = ToggleGroup()
                radiobutton {
                    text = "Not important"
                    isSelected = true
                    toggleGroup = toggle
                    searchOptions.put(this) { true }
                    resetSearchFunctions.add { isSelected = true }
                }
                radiobutton {
                    text = "Yes"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.summon.isNotEmpty() }
                    resetSearchFunctions.add { isSelected = false }
                }
                radiobutton {
                    text = "No"
                    isSelected = false
                    toggleGroup = toggle
                    searchOptions.put(this) { it.entityData.summon.isEmpty() }
                    resetSearchFunctions.add { isSelected = false }
                }
            }
        }
    }
}