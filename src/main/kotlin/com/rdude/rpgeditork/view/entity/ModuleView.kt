package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.view.helper.EntityTopMenu
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.geometry.Pos
import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import ru.rdude.fxlib.panes.SearchPane
import ru.rdude.rpg.game.logic.data.Module
import tornadofx.*

class ModuleView(wrapper: EntityDataWrapper<Module>) : EntityView<Module>(wrapper) {

    val skills = vbox {
        paddingAll = 10.0
        spacing = 5.0
        alignment = Pos.TOP_CENTER
        text("Skills")
        hbox {
            button(" Load from file ") {
                hgrow = Priority.ALWAYS
                maxWidth = Double.MAX_VALUE
            }
            button("Copy from module") {
                hgrow = Priority.ALWAYS
                maxWidth = Double.MAX_VALUE
            }
        }
        add(SearchPane(Data.skillsList).apply {
            setNameByProperty { w -> w.entityNameProperty }
            setTextFieldSearchBy( { w -> w.entityNameProperty.get() }, { w -> w.entityData.name } )
        })
    }


    override val root = anchorpane {
        tabpane {
            fitToParentSize()
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("Entities") {
                fitToParentSize()
                hbox {
                    fitToParentSize()
                    spacing = 10.0
                    alignment = Pos.CENTER_LEFT
                    add(skills)
                }
            }
            tab("Resources") {

            }
        }
        add(EntityTopMenu(wrapperProperty))
    }

    override fun reasonsNotToSave(): List<String> {
        return listOf()
    }
}