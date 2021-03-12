package com.rdude.rpgeditork.view

import com.rdude.rpgeditork.AnotherTest
import com.rdude.rpgeditork.enums.ObservableEnums
import com.rdude.rpgeditork.enums.createNewView
import com.rdude.rpgeditork.settings.Settings
import com.rdude.rpgeditork.utils.clearTempFolders
import com.rdude.rpgeditork.utils.loadDialog
import com.rdude.rpgeditork.view.entity.EntityView
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.image.ImageView
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import ru.rdude.fxlib.containers.selector.SelectorContainer
import ru.rdude.rpg.game.logic.data.EntityData
import ru.rdude.rpg.game.logic.data.SkillData
import tornadofx.*
import java.nio.file.Files

class MainView : View() {

    val screenWidth = Screen.getPrimary().visualBounds.width
    val screenHeight = Screen.getPrimary().visualBounds.height

    private var tabPane: TabPane by singleAssign()
    private var newTab: Tab by singleAssign()

    val entityViewTabs = FXCollections.observableHashMap<EntityView<*>, Tab>()

    override val root =
        anchorpane {
            primaryStage.initStyle(StageStyle.UNDECORATED)
            primaryStage.maxWidth = screenWidth
            primaryStage.width = screenWidth
            primaryStage.maxHeight = screenHeight
            primaryStage.height = screenHeight
            tabPane = tabpane {
                tabClosingPolicy = TabPane.TabClosingPolicy.ALL_TABS
                newTab = tab(" + ") {
                    prefWidth = screenWidth
                    prefHeight = screenHeight
                    isClosable = false
                    add(find<CreateNewView>())
                }
            }
            hbox {
                anchorpaneConstraints {
                    rightAnchor = 10
                }
                alignment = Pos.CENTER
                button {
                    text = "Settings"
                }
                button {
                    text = "_"
                    action {
                        currentStage?.isIconified = true
                    }
                }
                button {
                    text = "X"
                    action {
                        val tabsToRemove = mutableListOf<EntityView<*>>()
                        for (entry in entityViewTabs) {
                            if (!entry.key.wrapper.wasChanged || entry.key.saveOnCloseDialog.showAndWait()) {
                                entry.value.close()
                                tabsToRemove.add(entry.key)
                            } else {
                                break
                            }
                        }
                        tabsToRemove.forEach { entityViewTabs.remove(it) }
                        if (entityViewTabs.isEmpty()) {
                            clearTempFolders()
                            (scene.window as Stage).close()
                        }
                    }
                }
            }
        }

    fun openEntity(wrapper: EntityDataWrapper<*>) {
        if (entityViewTabs.containsKey(wrapper.mainView)) {
            tabPane.selectionModel.select(entityViewTabs[wrapper.mainView])
        } else {
            // create
            val tab = Tab()
            val view = wrapper.createNewView()
            // bind tab title to view name
            tab.textProperty().bind(view.name)
            // add icon
            tab.graphic = ImageView(wrapper.dataType.icon)
            // add view to tab's content
            tab.content = view.root
            // manage open entities
            entityViewTabs[view] = tab
            tab.setOnClosed { entityViewTabs.remove(view) }
            // on close ask to save
            tab.setOnCloseRequest {
                if (wrapper.wasChanged && !view.saveOnCloseDialog.showAndWait()) {
                    it.consume()
                }
            }
            // add tab to tab pane
            tabPane.tabs.add(tabPane.tabs.size - 1, tab)
            tabPane.selectionModel.select(tab)
        }
    }
}