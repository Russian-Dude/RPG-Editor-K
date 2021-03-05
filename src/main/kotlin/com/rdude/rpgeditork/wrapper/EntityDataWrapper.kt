package com.rdude.rpgeditork.wrapper

import com.rdude.rpgeditork.enums.EntityDataType
import com.rdude.rpgeditork.enums.entityDataTypeOf
import com.rdude.rpgeditork.view.MainView
import com.rdude.rpgeditork.view.entity.EntityView
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import ru.rdude.rpg.game.logic.data.*
import tornadofx.find
import tornadofx.getValue
import tornadofx.onChange
import tornadofx.setValue
import java.nio.file.Path

data class EntityDataWrapper<E : EntityData>(val entityData: E) {

    val dataType: EntityDataType<E> = entityDataTypeOf(entityData)

    val insideModuleProperty: SimpleObjectProperty<EntityDataWrapper<Module>> = SimpleObjectProperty()
    var insideModule: EntityDataWrapper<Module>? by insideModuleProperty
    val isInsideModule: Boolean get() = insideModule != null

    val insideFileProperty: SimpleObjectProperty<Path> = SimpleObjectProperty()
    var insideFile: Path? by insideFileProperty
    val isInsideFile: Boolean get() = insideFile != null

    val insideAsString: String
        get() = if (isInsideFile) insideFile.toString()
        else if (isInsideModule) insideModule!!.entityData.nameInEditor
        else ""

    val wasChangedProperty: SimpleBooleanProperty = SimpleBooleanProperty(false)
    var wasChanged: Boolean by wasChangedProperty

    val imagesWereChangedProperty: SimpleBooleanProperty = SimpleBooleanProperty(false)
    var imagesWereChanged: Boolean by imagesWereChangedProperty

    val mainViewProperty: SimpleObjectProperty<EntityView<E>> = SimpleObjectProperty()
    var mainView: EntityView<E>? by mainViewProperty
    val open: Boolean get() = mainView != null

    init {
        // entity can be inside file or module and not inside both
        insideModuleProperty.onChange { if (it != null) insideFile = null }
        insideFileProperty.onChange { if (it != null) insideModule = null }

        // if entity is inside module and changed, module should be considered as changed too
        imagesWereChangedProperty.onChange {
            if (it) {
                wasChanged = true
                if (insideModule != null) insideModule!!.imagesWereChanged = true
            }
        }
        wasChangedProperty.onChange { if (it && insideModule != null) insideModule!!.wasChanged = true }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EntityDataWrapper<*>) return false

        if (entityData != other.entityData) return false

        return true
    }

    override fun hashCode(): Int {
        return entityData.hashCode()
    }


}