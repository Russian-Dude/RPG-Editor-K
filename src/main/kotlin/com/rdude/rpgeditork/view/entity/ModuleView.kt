package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.enums.*
import com.rdude.rpgeditork.utils.loadDialog
import com.rdude.rpgeditork.view.MainView
import com.rdude.rpgeditork.view.helper.EntityTopMenu
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import com.rdude.rpgeditork.wrapper.ImageResourceWrapper
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import ru.rdude.fxlib.panes.SearchPane
import ru.rdude.rpg.game.logic.data.EntityData
import ru.rdude.rpg.game.logic.data.Module
import tornadofx.*
import tornadofx.Stylesheet.Companion.contextMenu

class ModuleView(wrapper: EntityDataWrapper<Module>) : EntityView<Module>(wrapper) {

    val nameField: TextField = textfield {
        text = entityData.name ?: ""
        changesChecker.add(this) { text }
        fieldsSaver.add {
            it.name = text
            it.nameInEditor = text
            wrapper.entityNameProperty.set(text)
        }
    }

    val description = textarea {
        text = entityData.description
        changesChecker.add(this) { text }
        fieldsSaver.add { it.description = text }
    }

    override val root = anchorpane {
        tabpane {
            fitToParentSize()
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("Main") {
                vbox {
                    paddingAll = 10.0
                    spacing = 10.0
                    hbox {
                        spacing = 5.0
                        alignment = Pos.CENTER_LEFT
                        text("Name")
                        add(nameField)
                    }
                    vbox {
                        spacing = 5.0
                        alignment = Pos.CENTER_LEFT
                        text("Description")
                        add(description.apply { maxWidth = 250.0 })
                    }
                }
            }
            tab("Entities") {
                fitToParentSize()
                hbox {
                    fitToParentSize()
                    spacing = 10.0
                    alignment = Pos.CENTER_LEFT
                    add(EntitiesList(SKILL, this@ModuleView))
                    add(EntitiesList(ITEM, this@ModuleView))
                    add(EntitiesList(MONSTER, this@ModuleView))
                    add(EntitiesList(EVENT, this@ModuleView))
                    add(EntitiesList(QUEST, this@ModuleView))
                }
            }
            tab("Resources") {
                fitToParentSize()
                hbox {
                    fitToParentSize()
                    spacing = 10.0
                    alignment = Pos.CENTER_LEFT
                    add(ImagesList(this@ModuleView))
                }
            }
        }
        add(EntityTopMenu(wrapperProperty))
    }

    override fun reasonsNotToSave(): List<String> {
        return listOf()
    }

    class EntitiesList<E : EntityData>(type: EntityDataType<E>, moduleView: ModuleView) : Fragment() {

        override val root = vbox {
            paddingAll = 10.0
            spacing = 5.0
            alignment = Pos.TOP_CENTER
            text("${type.name}s".capitalize())
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
            add(SearchPane(FilteredList(type.dataList) { w -> w.insideModule == moduleView.wrapper }).apply {
                setNameByProperty { w -> w.entityNameProperty }
                setTextFieldSearchBy({ w -> w.entityNameProperty.get() }, { w -> w.entityData.name })
                addContextMenuItem("Open") {
                    loadDialog("Loading ${type.name}...") {
                        find<MainView>().openEntity(it)
                    }
                }
                addContextMenuItem("Remove") {
                    moduleView.entityData.removeEntity(it.entityData)
                    it.insideModule = null
                    it.wasChanged = true
                }
            })
        }
    }

    class ImagesList(private val moduleView: ModuleView) : Fragment() {

        override val root = vbox {
            paddingAll = 10.0
            spacing = 5.0
            alignment = Pos.TOP_CENTER
            text("Images")
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
            add(SearchPane(FilteredList(Data.imagesList)
            { w -> moduleView.entityData.resources.imageResources.contains(w.resource) }).apply {
                setNameBy { w -> w.nameProperty.get() }
                setTextFieldSearchBy({ w -> w.nameProperty.get() })
                setIcon { w -> w.fxRepresentation }
            })
        }
    }
}