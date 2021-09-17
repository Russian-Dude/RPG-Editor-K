package com.rdude.rpgeditork.utils

import com.rdude.rpgeditork.saveload.GameJsonSerializerController
import com.rdude.rpgeditork.settings.Settings
import com.rdude.rpgeditork.utils.dialogs.LoadDialog
import com.rdude.rpgeditork.view.helper.AbilitiesTable
import com.rdude.rpgeditork.view.helper.SoundPlayer
import javafx.application.Platform
import javafx.collections.transformation.FilteredList
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.stage.StageStyle
import ru.rdude.rpg.game.logic.data.EntityData
import ru.rdude.rpg.game.logic.playerClass.AbilityPath
import ru.rdude.rpg.game.utils.Functions
import tornadofx.*
import java.nio.file.Files

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

fun String.startsWithAnyOf(vararg value: String) = value.any { this.startsWith(it) }

fun String.trimZeroes() = this.replace(Regex("\\.0+\\b"), "")

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

fun GridPane.row(
    text1: String, content1: Node,
    text2: String, content2: Node,
    text3: String, content3: Node,
    op: Pane.() -> Unit = {}) {

    (content1 as? Control)?.maxWidth = Double.MAX_VALUE
    (content2 as? Control)?.maxWidth = Double.MAX_VALUE
    (content3 as? Control)?.maxWidth = Double.MAX_VALUE
    row {
        op.invoke(this)
        text(text1)
        add(content1)
        text(text2)
        add(content2)
        text(text3)
        add(content3)
    }
}

fun GridPane.row(text1: String, text2: String) = row {
    text(text1)
    text(text2)
}

fun clearTempFolders() {
    // images
    Files.list(Settings.tempImagesFolder).forEach { Files.delete(it) }
    Files.list(Settings.tempPackedImagesFolder).forEach { it.toFile().deleteRecursively() }
    // sounds
    SoundPlayer.instances.forEach { it.stop(); it.dispose() }
    Files.list(Settings.tempSoundsFolder).forEach { Files.delete(it) }
    // other files
    Files.list(Settings.tempFolder)
        .filter { Files.isDirectory(it) && it.fileName.toString().isLong() }
        .forEach {
            it.toFile().deleteRecursively()
        }
}

fun ComboBox<*>.setNullToStringConverter(nullToString: String) {
    converter = NullableStringConverter(nullToString)
}

fun FilteredList<*>.update() {
    val predicate = this.predicate;
    this.setPredicate { false }
    this.predicate = predicate
}

operator fun AbilityPath.plus(another: AbilityPath?): AbilityPath {
    if (another == null) return this
    val chars = (name + another.name).toCharArray().distinct()
    return AbilityPath.values().find { it.name.length == chars.size && chars.all { char -> it.name.contains(char) } }!!
}

operator fun AbilityPath.minus(another: AbilityPath?): AbilityPath? {
    if (another == null) return this
    var resultName = name
    another.name.toCharArray().forEach { resultName = resultName.replace(it.toString(), "") }
    val chars = resultName.toCharArray()
    return AbilityPath.values().find { it.name.length == chars.size && chars.all { char -> it.name.contains(char) } }
}

fun abilityPathBetweenCells(from: AbilitiesTable.CellAStarNode, between: AbilitiesTable.CellAStarNode, to: AbilitiesTable.CellAStarNode): AbilityPath? {
    // only y relation
    if (from.x - to.x == 0 && from.y - to.y != 0) {
        return AbilityPath.SN
    }
    // only x relation
    else if (from.y - to.y == 0 && from.x - to.x != 0) {
        return AbilityPath.WE
    }
    // both relations
    // NW
    else if ((from.x < between.x && from.y == between.y && to.x == between.x && to.y < between.y)
        || (from.x == between.x && from.y < between.y && to.x < between.x && to.y == between.y)) {
        return AbilityPath.NW
    }
    // NE
    else if ((from.x == between.x && from.y < between.y && to.x > between.x && to.y == between.y)
        || (from.x > between.x && from.y == between.y && to.x == between.x && to.y < between.y)) {
        return AbilityPath.NE
    }
    // SE
    else if ((from.x > between.x && from.y == between.y && to.x == between.x && to.y > between.y)
        ||(from.x == between.x && from.y > between.y && to.x > between.x && to.y == between.y)) {
        return AbilityPath.SE
    }
    // SW
    else if ((from.x < between.x && from.y == between.y && to.x == between.x && to.y > between.y)
        ||(from.x == between.x && from.y > between.y && to.x < between.x && to.y == between.y)) {
        return AbilityPath.SW
    }
    else {
        return null
    }
}

fun <T> Collection<T>.containsAny(collection: Collection<T>): Boolean {
    for (t in collection) {
        if (this.contains(t)) {
            return true
        }
    }
    return false
}


