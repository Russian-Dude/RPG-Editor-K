package com.rdude.rpgeditork.view.search

import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.scene.control.Button
import javafx.scene.control.Control
import ru.rdude.fxlib.dialogs.SearchDialog
import ru.rdude.rpg.game.logic.data.EntityData
import tornadofx.Fragment
import tornadofx.action
import tornadofx.button
import java.util.function.Function

abstract class EntitySearchView<E: EntityData>: Fragment() {

    val searchOptions: MutableMap<Control, Function<EntityDataWrapper<E>, *>> = HashMap()

    protected val resetSearchFunctions : MutableSet<() -> Unit> = HashSet()

    abstract fun configPopup(searchDialog: SearchDialog<EntityDataWrapper<E>>)

    val resetSearchButton = button("Reset search") {
        maxWidth = Double.MAX_VALUE
        action {
            resetSearchFunctions.forEach { it.invoke() }
        }
    }
}