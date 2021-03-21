package com.rdude.rpgeditork.view

import com.rdude.rpgeditork.enums.createNewView
import com.rdude.rpgeditork.style.LightTheme
import com.rdude.rpgeditork.utils.clearTempFolders
import com.rdude.rpgeditork.utils.limitHeaderArea
import com.rdude.rpgeditork.view.entity.EntityView
import com.rdude.rpgeditork.view.settings.SettingsView
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.image.ImageView
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.*

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
                limitHeaderArea(screenWidth - 200.0)
                tabClosingPolicy = TabPane.TabClosingPolicy.ALL_TABS
                newTab = tab("  ➕  ") {
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
                    addClass(LightTheme.withoutBorders)
                    text = " 🔧 "
                    action {
                        find<SettingsView>().openModal(StageStyle.UNDECORATED)
                    }
                }
                button {
                    addClass(LightTheme.withoutBorders)
                    text = "➖"
                    action {
                        currentStage?.isIconified = true
                    }
                }
                button {
                    addClass(LightTheme.withoutBorders)
                    text = "❌"
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
            tab.text = view.name.get()
            view.name.onChange { tab.text = it }
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