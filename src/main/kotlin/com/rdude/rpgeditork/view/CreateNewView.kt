package com.rdude.rpgeditork.view

import com.rdude.rpgeditork.enums.*
import com.rdude.rpgeditork.saveload.EntityLoader
import com.rdude.rpgeditork.style.LightTheme
import com.rdude.rpgeditork.utils.InfoDialog
import com.rdude.rpgeditork.utils.dialogs.Dialogs
import com.rdude.rpgeditork.utils.loadDialog
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import javafx.stage.FileChooser
import javafx.stage.Screen
import tornadofx.*

class CreateNewView : Fragment() {

    private val buttonPrefWidth = 400.0
    private val buttonPrefHeight = Screen.getPrimary().visualBounds.height / 10

    override val root = vbox {
        alignment = Pos.CENTER
        isFillWidth = true
        add(CreationButton(MODULE, buttonPrefWidth, buttonPrefHeight))
        add(CreationButton(SKILL, buttonPrefWidth, buttonPrefHeight))
        add(CreationButton(ITEM, buttonPrefWidth, buttonPrefHeight))
        add(CreationButton(MONSTER, buttonPrefWidth, buttonPrefHeight))
        add(CreationButton(EVENT, buttonPrefWidth, buttonPrefHeight))
        add(CreationButton(QUEST, buttonPrefWidth, buttonPrefHeight))
    }

    class CreationButton(
        type: EntityDataType<*>,
        private val buttonWidth: Double,
        private val buttonHeight: Double
    ) : Fragment() {

        private val unHovered = UnHovered(type, buttonWidth, buttonHeight)
        private val hovered = Hovered(type, buttonWidth, buttonHeight)

        override val root = stackpane {
            alignment = Pos.CENTER
            prefWidth = buttonWidth
            prefHeight = buttonHeight
            add(unHovered)
            hoverProperty().addListener(ChangeListener<Boolean> { _, _, value ->
                if (value) {
                    unHovered.replaceWith(hovered, ViewTransition.Flip(0.2.seconds, true))
/*                    runAsync {
                        Thread.sleep(250)
                    } ui {
                        if (!isHover) {
                            hovered.replaceWith(unHovered, ViewTransition.Flip(0.2.seconds, true))
                        }
                    }*/
                } else {
                    hovered.replaceWith(unHovered, ViewTransition.Flip(0.2.seconds, true))
/*                    runAsync {
                        Thread.sleep(250)
                    } ui {
                        if (isHover) {
                            unHovered.replaceWith(hovered, ViewTransition.Flip(0.2.seconds, true))
                        }
                    }*/
                }
            })
        }

    }

    class UnHovered(
        private val type: EntityDataType<*>,
        private val regionWidth: Double,
        private val regionHeight: Double,
    ) : Fragment() {

        override val root =
            button {
                style(true) { backgroundColor += LightTheme.whiteGrey }
                text = type.name.capitalize()
                prefWidth = regionWidth
                prefHeight = regionHeight
            }

    }

    class Hovered(
        private val type: EntityDataType<*>,
        private val regionWidth: Double,
        private val regionHeight: Double,
    ) : Fragment() {

        private val maxButtonWidth =
            if (type.canBeDescriber) regionWidth / 4
            else regionWidth / 3

        override val root = vbox {
            alignment = Pos.CENTER
            prefWidth = regionWidth
            prefHeight = regionHeight
            label(type.name.capitalize())
            hbox {
                alignment = Pos.CENTER
                maxWidth = regionWidth
                button {
                    style(true) { backgroundColor += LightTheme.whiteGrey }
                    text = "new"
                    maxWidth = maxButtonWidth
                    isFillWidth = true
                    prefHeight = regionHeight * 0.5
                    hgrow = Priority.ALWAYS
                    action {
                        loadDialog("Creating new ${type.name}...") {
                            find<MainView>().openEntity(type.newEntity())
                        }
                    }
                }
                if (type.canBeDescriber) {
                    button {
                        style(true) { backgroundColor += LightTheme.whiteGrey }
                        textAlignment = TextAlignment.CENTER
                        text = "new\r\ndescriber"
                        maxWidth = maxButtonWidth
                        isFillWidth = true
                        prefHeight = regionHeight * 0.5
                        hgrow = Priority.ALWAYS
                        action {
                            loadDialog("Creating new ${type.name} describer...") {
                                val wrapper = type.newEntity()
                                wrapper.entityData.isDescriber = true
                                find<MainView>().openEntity(wrapper)
                            }
                        }
                    }
                }
                button {
                    style(true) { backgroundColor += LightTheme.whiteGrey }
                    text = "open"
                    maxWidth = maxButtonWidth
                    isFillWidth = true
                    prefHeight = regionHeight * 0.5
                    hgrow = Priority.ALWAYS
                    action {
                        val wrapper = Dialogs.skillsSearchDialog.showAndWait()
                        if (wrapper.isPresent) {
                            loadDialog("Loading ${type.name}...") {
                                find<MainView>().openEntity(wrapper.get())
                            }
                        }
                    }
                }
                button {
                    style(true) { backgroundColor += LightTheme.whiteGrey }
                    text = "load file"
                    maxWidth = maxButtonWidth
                    isFillWidth = true
                    prefHeight = regionHeight * 0.5
                    hgrow = Priority.ALWAYS
                    val loader = find<EntityLoader>()
                    action {
                        val files = chooseFile(
                            title = "Open ${type.name}",
                            filters = arrayOf(FileChooser.ExtensionFilter(type.name, "*.${type.name}")),
                            initialDirectory = type.saveLoadPath.toFile(),
                            mode = FileChooserMode.Single
                        )
                        if (files.isEmpty()) {
                            return@action
                        }
                        val file = files[0].toPath()
                        type.saveLoadPath = file.parent
                        loadDialog("Loading ${type.name}...") {
                            val wrapper = loader.loadFromFile(file)
                            if (wrapper == null) {
                                InfoDialog("Failed to load ${type.name}", image = Image("icons\\warning.png")).show()
                            } else {
                                find<MainView>().openEntity(wrapper)
                            }
                        }
                    }
                }
            }
        }
    }
}