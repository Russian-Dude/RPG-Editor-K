package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.enums.SKILL
import com.rdude.rpgeditork.utils.isNegative
import com.rdude.rpgeditork.utils.removeSpaces
import com.rdude.rpgeditork.utils.row
import com.rdude.rpgeditork.view.helper.EntityTopMenu
import com.rdude.rpgeditork.view.helper.ImagePicker
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import ru.rdude.fxlib.containers.selector.SelectorContainer
import ru.rdude.rpg.game.logic.data.AbilityData
import ru.rdude.rpg.game.logic.enums.EntityReferenceInfo
import tornadofx.*
import kotlin.math.max

class AbilityView(wrapper: EntityDataWrapper<AbilityData>) : EntityView<AbilityData>(wrapper) {

    override val nameField: TextField = textfield {
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

    override val nameInEditorField: TextField = textfield {
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

    val description = textarea {
        isWrapText = true
        text = entityData.description
        changesChecker.add(this) { text }
        fieldsSaver.add { it.description = text }
    }

    val addLvlButton = Button("Add level").apply {
        action {
            levels.tabs.add(LevelTab(levels.tabs.size + 1, entityData))
        }
    }

    val levels: TabPane = TabPane().apply {
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        for (lvl in 1..max(1, entityData.levels.size)) {
            tabs.add(LevelTab(lvl, entityData))
        }
        changesChecker.add(this) {
            tabs.asSequence()
                .map { it as LevelTab }
                .map {
                    val requirements = mutableListOf(it.lvlRequirement.toLong(), it.classLvlRequirement.toLong())
                    val skillsGuids = it.skills.selected.map { el -> el.entityData.guid }
                    val buffsGuids = it.buffs.selected.map { el -> el.entityData.guid }
                    skillsGuids + buffsGuids + requirements
                }
                .reduce { a, b -> a + b }
                .sorted() to tabs
                .map { it as LevelTab }
                .map { it.description.text }
                .reduce{a, b -> a + b}
        }
        fieldsSaver.add {
            entityData.entityReferenceInfo = EntityReferenceInfo.ALL
            entityData.entityInfo = EntityReferenceInfo.ALL
            entityData.clearLevels()
            tabs.asSequence()
                .map { it as LevelTab }
                .forEach {
                    val abilityLevel = entityData.createLvl(it.lvl)
                    abilityLevel.lvlRequirement = it.lvlRequirement
                    abilityLevel.classLvlRequirement = it.classLvlRequirement
                    abilityLevel.skills = it.skills.selected.map { el -> el.entityData.guid }.toSet()
                    abilityLevel.buffs = it.buffs.selected.map { el -> el.entityData.guid }.toSet()
                    abilityLevel.description = it.description.text
                }
        }
    }

    val abilityIcon = ImagePicker(
        header = "Icon (64x64)",
        imageWidthRestriction = 64.0,
        imageHeightRestriction = 64.0
    ).apply {
        imageResourceWrapper = Data.images[entityData.resources.abilityIcon?.guid]
        changesChecker.add(this, true) { imageResourceWrapper?.guid }
        fieldsSaver.add { it.resources.abilityIcon = imageResourceWrapper?.resource }
        imagePickers.add(this)
    }


    override val root = anchorpane {
        tabpane {
            fitToParentSize()
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("Logic") {
                vbox {
                    paddingAll = 10.0
                    spacing = 25.0
                    gridpane {
                        hgap = 5.0
                        vgap = 5.0
                        constraintsForColumn(1).maxWidth = 145.0
                        row("Name", nameField)
                        row("Name in editor", nameInEditorField)
                    }
                    add(addLvlButton)
                    stackpane {
                        alignment = Pos.TOP_RIGHT
                        add(levels)
                        add(label("levels    ").apply { paddingAll = 5.0 })
                    }
                }
            }
            tab("Visual and sound") {
                hbox {
                    paddingAll = 10.0
                    vbox {
                        spacing = 20.0
                        alignment = Pos.TOP_CENTER
                        text("Main") {
                            font = Font.font(16.0)
                        }
                        hbox {
                            spacing = 5.0
                            add(abilityIcon)
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
        val messages: MutableList<String> = ArrayList()
        if (nameField.text.removeSpaces().isEmpty() && nameInEditorField.text.removeSpaces().isEmpty()) {
            messages.add("Either one of the fields NAME or NAME IN EDITOR must not be empty")
        }
        return messages
    }


    private class LevelTab(var lvl: Int, abilityData: AbilityData): Tab("   $lvl   ") {

        private val lvlRequirementTextField = TextField().apply {
            filterInput { it.controlNewText.isInt() && !it.controlNewText.toInt().isNegative() }
            abilityData.getLvl(lvl).ifPresent { text = it.lvlRequirement.toString() }
        }

        var lvlRequirement: Int
            get() = if (lvlRequirementTextField.text.isBlank()) 0 else lvlRequirementTextField.text.toInt()
            set(value) { lvlRequirementTextField.text = value.toString() }

        private val classLvlRequirementTextField = TextField().apply {
            filterInput { it.controlNewText.isInt() && !it.controlNewText.toInt().isNegative() }
            abilityData.getLvl(lvl).ifPresent { text = it.classLvlRequirement.toString() }
        }

        var classLvlRequirement: Int
            get() = if (classLvlRequirementTextField.text.isBlank()) 0 else classLvlRequirementTextField.text.toInt()
            set(value) { classLvlRequirementTextField.text = value.toString() }

        val skills = SelectorContainer.simple(Data.skills.list)
            .nameByProperty { w -> w.entityNameProperty }
            .searchBy({ w -> w.entityNameProperty.get() }, { w -> w.entityData.name })
            .setUnique(true)
            .get()
            .apply {
                SKILL.configSearchDialog(searchDialog)
                abilityData.getLvl(lvl).ifPresent { abilityLvl -> abilityLvl.skills.forEach { add(Data.skills[it]) } }
            }

        val buffs = SelectorContainer.simple(Data.skills.list)
            .nameByProperty { w -> w.entityNameProperty }
            .searchBy({ w -> w.entityNameProperty.get() }, { w -> w.entityData.name })
            .setUnique(true)
            .get()
            .apply {
                SKILL.configSearchDialog(searchDialog)
                abilityData.getLvl(lvl).ifPresent { abilityLvl -> abilityLvl.buffs.forEach { add(Data.skills[it]) } }
            }

        val removeButton = Button("Remove this level").apply {
            action {
                if (tabPane.tabs.size > 1) {
                    val tabPane = tabPane
                    tabPane.tabs.remove(this@LevelTab)
                    tabPane.tabs.forEachIndexed { index, tab -> tab.text = "   ${index + 1}   "; (tab as LevelTab).lvl = index + 1 }
                }
            }
        }

        val description = textarea {
            isWrapText = true
            abilityData.getLvl(lvl).ifPresent { text = it.description }
        }

        private val root = HBox().apply {
            spacing = 10.0
            paddingAll = 10.0
        }

        init {
            abilityData.getLvl(lvl).ifPresent {
                lvlRequirement = it.lvlRequirement
                classLvlRequirement = it.classLvlRequirement
                skills.addAll(it.skills.map { guid -> Data.skills[guid] })
                buffs.addAll(it.buffs.map { guid -> Data.skills[guid] })
            }

            val requirementsGridPane = GridPane()
            requirementsGridPane.vgap = 10.0
            requirementsGridPane.hgap = 10.0
            requirementsGridPane.add(Text(""), 0, 0)
            requirementsGridPane.add(Text("Level requirement").apply { textAlignment = TextAlignment.CENTER }, 0, 1)
            requirementsGridPane.add(lvlRequirementTextField.apply { prefWidth = 50.0 }, 1, 1)
            requirementsGridPane.add(Text("Class level requirement").apply { textAlignment = TextAlignment.CENTER; }, 0, 2)
            requirementsGridPane.add(classLvlRequirementTextField.apply { prefWidth = 50.0 }, 1, 2)
            requirementsGridPane.add(removeButton, 0, 3, 1, 2)

            val skillsGridPane = GridPane()
            skillsGridPane.columnConstraints.add(ColumnConstraints().apply { halignment = HPos.CENTER })
            skillsGridPane.columnConstraints.add(ColumnConstraints().apply { halignment = HPos.CENTER })
            skillsGridPane.vgap = 10.0
            skillsGridPane.hgap = 10.0
            skillsGridPane.add(Text("Allow to use skills").apply { textAlignment = TextAlignment.CENTER }, 0, 0)
            skillsGridPane.add(skills.apply { prefHeight = 200.0; prefWidth = 200.0}, 0, 1)
            skillsGridPane.add(Text("Apply buffs").apply { textAlignment = TextAlignment.CENTER }, 1, 0)
            skillsGridPane.add(buffs.apply { prefHeight = 200.0; prefWidth = 200.0 }, 1, 1)

            root.add(requirementsGridPane)
            root.add(skillsGridPane)
            root.add(vbox {
                alignment = Pos.TOP_CENTER
                add(text("Description"))
                add(description)
            })
            content = root
        }
    }

}