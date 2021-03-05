package com.rdude.rpgeditork.utils

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.saveload.GameJsonSerializerController
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.layout.VBox
import ru.rdude.fxlib.dialogs.SearchDialog
import ru.rdude.rpg.game.logic.data.EntityData
import ru.rdude.rpg.game.utils.Functions
import tornadofx.find
import tornadofx.hide

fun <T : EntityData> T.cloneWithNewGuid(): T {
    val serializer = find<GameJsonSerializerController>().gameJsonSerializer
    val cl = this.javaClass
    return serializer.deSerializeEntityData(serializer.serialize(this), cl).apply { guid = Functions.generateGuid() }
}
