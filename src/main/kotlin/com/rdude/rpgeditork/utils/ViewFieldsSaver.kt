package com.rdude.rpgeditork.utils

import com.rdude.rpgeditork.view.entity.EntityView
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.beans.property.SimpleObjectProperty
import ru.rdude.rpg.game.logic.data.EntityData

class ViewFieldsSaver<E : EntityData>(view: EntityView<E>) {

    private val funSet: MutableSet<(E) -> Unit> = HashSet()

    var entityDataProperty = SimpleObjectProperty<EntityDataWrapper<E>>().apply { bind(view.wrapperProperty) }

    fun add(saveFun: (E) -> Unit) = funSet.add(saveFun)

    fun save() = funSet.forEach { it.invoke(entityDataProperty.get().entityData) }

}