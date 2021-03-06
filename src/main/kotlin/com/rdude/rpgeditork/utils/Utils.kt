package com.rdude.rpgeditork.utils

import com.rdude.rpgeditork.enums.entityDataTypeOf
import com.rdude.rpgeditork.saveload.GameJsonSerializerController
import com.rdude.rpgeditork.settings.Settings
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.stage.StageStyle
import javafx.util.StringConverter
import ru.rdude.fxlib.dialogs.SearchDialog
import ru.rdude.rpg.game.logic.data.EntityData
import ru.rdude.rpg.game.utils.Functions
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Function

fun <T : EntityData> T.cloneWithNewGuid(): T {
    val serializer = find<GameJsonSerializerController>().gameJsonSerializer
    val cl = this.javaClass
    return serializer.deSerializeEntityData(serializer.serialize(this), cl).apply { guid = Functions.generateGuid() }
}

fun loadDialog(text: String = "", graphic: Image? = null, task: () -> Unit) {
    with(find<LoadDialog>()) {
        this.text = text
        this.graphic = graphic
        openModal(StageStyle.UNDECORATED)
        Platform.runLater {
            task.invoke()
            close()
        }
    }
}

fun String.removeSpaces() = this.replace(" ", "")

fun Int.isNegative() = this < 0

fun Int.isPositive() = this > 0

fun GridPane.row(name: String, content: Node, op: Pane.() -> Unit = {}) {
    (content as? Control)?.maxWidth = Double.MAX_VALUE
    row {
        op.invoke(this)
        text(name)
        add(content)
    }
}

fun GridPane.row(text1: String, text2: String) = row {
    text(text1)
    text(text2)
}

fun clearTempFolders() {
    Files.list(Settings.tempImagesFolder).forEach { Files.delete(it) }
    Files.list(Settings.tempPackedImagesFolder).forEach { Files.delete(it) }
    Files.list(Settings.tempFolder)
        .filter { Files.isDirectory(it) && it.fileName.toString().isLong() }
        .forEach {
            it.toFile().deleteRecursively()
        }
}

inline fun <reified E: EntityData> SearchDialog<EntityDataWrapper<E>>.config() {
    initStyle(StageStyle.UNDECORATED)
    dialogPane.style {
        backgroundColor += c("#F5F6FA")
        borderColor += box(c("#353B48"))
    }
    with(this.searchPane) {
        setNameBy { it.entityNameProperty.get() }
        setTextFieldSearchBy( { it.entityNameProperty.get() }, { it.entityData.name } )
        val dataType = entityDataTypeOf<E>()
        val searchView = dataType.newSearchView()
        this@config.headerText = "Select ${dataType.name}"
        addExtraSearchNode(searchView.root)
        setSearchOptions(searchView.searchOptions)
        searchView.configPopup(this@config)
    }
}

fun ComboBox<*>.setNullToStringConverter(nullToString: String) {
    converter = NullableStringConverter(nullToString)
}


