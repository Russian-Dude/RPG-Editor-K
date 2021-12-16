package com.rdude.rpgeditork.view.helper

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.enums.ABILITY
import com.rdude.rpgeditork.utils.aStar.AbilityPathGenerator
import com.rdude.rpgeditork.utils.abilityPathBetweenCells
import com.rdude.rpgeditork.utils.containsAny
import com.rdude.rpgeditork.utils.dialogs.Dialogs
import com.rdude.rpgeditork.utils.dialogs.InfoDialog
import com.rdude.rpgeditork.utils.plus
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.beans.value.ChangeListener
import javafx.geometry.Side
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import ru.rdude.rpg.game.logic.data.AbilityData
import ru.rdude.rpg.game.logic.data.AbilityDataCell
import ru.rdude.rpg.game.logic.data.PlayerClassData
import ru.rdude.rpg.game.logic.playerClass.AbilityPath
import ru.rdude.rpg.game.utils.aStar.AStarNode
import tornadofx.action
import tornadofx.item

class AbilitiesTable(private val classData: PlayerClassData) : GridPane() {

    private val cells: Array<Array<CellAStarNode>> =
        Array(9) { x -> Array(9) { y -> CellAStarNode(Cell(), x, y) } }
    private val pathGenerator = AbilityPathGenerator(cells)
    private var currentRouteStart: Cell? = null
    private var lastClickedCell: CellAStarNode? = null

    private val abilityCellContextMenu = ContextMenu().apply {
        item("Connect").action { currentRouteStart = lastClickedCell?.cell }
        item("Remove connection").action { lastClickedCell?.let { removeConnections(it) } }
        item("Remove ability").action { lastClickedCell?.let { removeAbility(it) } }
    }

    init {
        val initialCells = classData.resources.cells.cells
        for (x in initialCells.indices) {
            for (y in 0 until initialCells[0].size) {
                val cell = cells[x][y].cell
                cell.setContent(initialCells[x][y])
                add(cell, x, y)
                createButtonAction(cells[x][y])
            }
        }
        createInitialConnections()
    }

    fun getCell(x: Int, y: Int) = cells[x][y].cell

    fun getCells() = cells.flatten().map { it.cell }

    fun preferHorizontalConnections() = pathGenerator.preferHorizontalConnections()

    fun preferVerticalConnections() = pathGenerator.preferVerticalConnections()

    private fun removeConnections(cell: CellAStarNode) {
        if (cell.isAbility) {
            cells.flatten().forEach {
                    // from
                    if (it.connectedWith.from.contains(cell.cell.contentWrapper)) {
                        if (it.isAbility) {
                            it.cell.requirements.remove(cell.cell)
                        }
                        it.connectedWith.from.remove(cell.cell.contentWrapper)
                        if (it.isPath && it.connectedWith.from.isEmpty()) {
                            it.connectedWith.to.clear()
                            it.cell.clear()
                        }
                    }
                    // to
                    if (it.connectedWith.to.contains(cell.cell.contentWrapper)) {
                        it.connectedWith.to.remove(cell.cell.contentWrapper)
                        if (it.isPath && it.connectedWith.to.isEmpty()) {
                            it.connectedWith.from.clear()
                            it.cell.clear()
                        }
                    }
                }
        }
        fixConnectionsImages()
    }

    private fun removeAbility(cell: CellAStarNode) {
        removeConnections(cell)
        cell.cell.clear()
    }

    private fun fixConnectionsImages() {
        cells.flatten().filter { it.isPath }.forEach {
            var res = (it.cell.content as AbilityPath).name
            // N
            if (it.y > 0 && !cells[it.x][it.y - 1].connectedWith.from.containsAny(it.connectedWith.from)) {
                res = res.replace("N", "")
            }
            // S
            if (it.y < cells[0].size - 1 && !cells[it.x][it.y + 1].connectedWith.from.containsAny(it.connectedWith.from)) {
                res = res.replace("S", "")
            }
            // W
            if (it.x > 0 && !cells[it.x - 1][it.y].connectedWith.from.containsAny(it.connectedWith.from)) {
                res = res.replace("W", "")
            }
            // E
            if (it.x < cells.size - 1 && !cells[it.x + 1][it.y].connectedWith.from.containsAny(it.connectedWith.from)) {
                res = res.replace("N", "")
            }
            val newPath = AbilityPath.values().find { path -> path.name.length == res.length && res.toCharArray().all { char -> path.name.contains(char) } }
            if (newPath != null) {
                it.cell.setContent(newPath)
            }
        }
    }

    private fun createInitialConnections() {
        classData.abilityEntries.forEach { abilityEntry ->
            val entryCell = cells.flatten().find { it.cell.contentWrapper?.entityData?.guid == Data.abilities[abilityEntry.abilityData]?.entityData?.guid }
            abilityEntry.requirements.forEach { (requirementGuid, requirementLvl) ->
                val requirementCell = cells.flatten().find { it.cell.contentWrapper?.entityData?.guid == requirementGuid }
                if (entryCell != null && requirementCell != null) {
                    pathGenerator.findExistedRoute(requirementCell, entryCell).ifPresent { route ->
                        route.forEach { routeCell ->
                            entryCell.cell.contentWrapper?.let { routeCell.connectedWith.to.add(it) }
                            requirementCell.cell.contentWrapper?.let { routeCell.connectedWith.from.add(it) }
                        }
                    }
                }
            }
        }
    }

    private fun createButtonAction(cellNode: CellAStarNode) {
        cellNode.cell.action {
            lastClickedCell = cellNode
            // if looking for end point of a route
            if (currentRouteStart != null) {
                val flatten = cells.flatten()
                val from = flatten.find { it.cell == currentRouteStart }
                val to = flatten.find { it.cell == cellNode.cell }
                if (from != null && to != null && to.isAbility) {
                    pathGenerator.findRoute(from, to).ifPresent { path ->
                        val requiredLvl = Dialogs.inputNumberDialog("${from.cell.contentWrapper?.entityData?.nameInEditor?.toLowerCase()?.capitalize() ?: "Ability"} level requirement", true)
                        path.forEach { routeNode ->
                            if (from.isAbility) {
                                routeNode.connectedWith.from.add(from.cell.contentWrapper!!)
                            }
                            else if (from.isPath) {
                                routeNode.connectedWith.from.addAll(from.connectedWith.from)
                            }
                            if (to.isAbility) {
                                routeNode.connectedWith.to.add(to.cell.contentWrapper!!)
                            }
                            else if (to.isPath) {
                                routeNode.connectedWith.to.addAll(to.connectedWith.to)
                            }
                         }
                        updatePathImages(path)
                        to.cell.requirements[from.cell] = requiredLvl
                    }
                }
                currentRouteStart = null
            }
            // if no route start
            else {
                // if cell is empty - open dialog to add ability to this cell
                if (cellNode.isEmpty && !isAbilityCellAround(cellNode)) {
                    ABILITY.defaultSearchDialog.showAndWait().ifPresent { selected ->
                        if (cells.flatten().any { it.cell.contentWrapper == selected }) {
                            AbilityAlreadyPresentsDialog.show(selected)
                        }
                        else {
                            cellNode.cell.setContent(selected)
                        }
                    }
                }
                // if cell contains ability
                else if (cellNode.isAbility) {
                    abilityCellContextMenu.show(cellNode.cell, Side.BOTTOM, 20.0, 20.0)
                }
            }
        }
    }

    private fun updatePathImages(route: List<CellAStarNode>) {
        for (i in route.indices) {
            if (i > 0 && i < route.size - 1) {
                val between = abilityPathBetweenCells(route[i - 1], route[i], route[i + 1])
                if (between != null) {
                    val cell = route[i].cell
                    if (cell.content is AbilityPath) {
                        cell.setContent((cell.content as AbilityPath) + between)
                    }
                    else if (route[i].isEmpty) {
                        cell.setContent(between)
                    }
                }
            }
        }
    }

    private fun isAbilityCellAround(node: CellAStarNode): Boolean {
        val x = node.x
        val y = node.y
        return (x > 1 && cells[x - 1][y].isAbility)
                || (x < cells.size - 1 && cells[x + 1][y].isAbility)
                || (y > 1 && cells[x][y - 1].isAbility)
                || (y < cells[0].size - 1 && cells[x][y + 1].isAbility)
    }

    class CellAStarNode(val cell: Cell, val x: Int, val y: Int) : AStarNode {
        private val cellId = "$x$y".toLong()
        val connectedWith = CellConnection()
        val isPath get() = cell.content is AbilityPath
        val isAbility get() = cell.content is AbilityData
        val isEmpty get() = cell.content == null
        override fun getId() = cellId
    }

    class CellConnection {
        val from: MutableSet<EntityDataWrapper<AbilityData>> = HashSet()
        val to: MutableSet<EntityDataWrapper<AbilityData>> = HashSet()
    }

    class Cell : Button() {

        private val imageView: ImageView = ImageView()
        var content: AbilityDataCell<*>? = null
        var contentWrapper: EntityDataWrapper<AbilityData>? = null
        val requirements: MutableMap<Cell, Int> = HashMap()

        var imageChangeListener: ChangeListener<Boolean>? = null
        var textListener: ChangeListener<String>? = null

        init {
            setMinSize(40.0, 40.0)
            setMaxSize(40.0, 40.0)
            imageView.fitWidth = 40.0
            imageView.fitHeight = 40.0
            imageView.resize(40.0, 40.0)
            this.graphic = imageView
        }

        fun setContent(path: AbilityPath) {
            contentWrapper?.let { unbindImagesAndText(it) }
            imageView.image = pathToImage(path)
            content = path
            contentWrapper = null
            text = ""
        }

        fun setContent(ability: AbilityData) {
            setContent(Data.abilities[ability.guid])
        }

        fun setContent(ability: EntityDataWrapper<AbilityData>?) {
            if (ability == null) {
                clear()
                return
            }
            contentWrapper?.let { unbindImagesAndText(it) }
            bindImagesAndText(ability)
            imageView.image = Data.images[ability.entityData.resources.abilityIcon?.guid]?.fxRepresentation
            content = ability.entityData
            contentWrapper = ability
            text = if (imageView.image != null) "" else ability.entityNameProperty.get()
        }

        fun clear() {
            contentWrapper?.let { unbindImagesAndText(it) }
            imageView.image = null
            content = null
            contentWrapper = null
            text = ""
        }

        fun setContent(guid: Long) {
            if (guid < 0) {
                imageView.image = null
                content = null
            } else if (guid <= 10) {
                setContent(AbilityPath.values()[guid.toInt()])
            } else {
                val ability = Data.abilities[guid]?.entityData
                if (ability != null) {
                    setContent(ability)
                }
            }
        }

        private fun bindImagesAndText(ability: EntityDataWrapper<AbilityData>) {
            val textChange: ChangeListener<String> =
                ChangeListener { _, _, newValue -> text = if (imageView.image != null) "" else newValue }
            textListener = textChange
            ability.entityNameProperty.addListener(textChange)

            val imageChange: ChangeListener<Boolean> =
                ChangeListener { _, _, _ ->
                    imageView.image = Data.images[ability.entityData.resources.abilityIcon.guid]?.fxRepresentation
                    text = if (imageView.image != null) "" else ability.entityNameProperty.get()
                }
            imageChangeListener = imageChange
            ability.imagesWereChangedProperty.addListener(imageChange)
        }

        private fun unbindImagesAndText(ability: EntityDataWrapper<AbilityData>) {
            textListener?.let { ability.entityNameProperty.removeListener(it) }
            imageChangeListener?.let { ability.imagesWereChangedProperty.removeListener(it) }
        }

        private companion object PathToImage {

            private val NSWE = Image("icons/abilities_tree/Ability_Path_ALL.png")
            private val NE = Image("icons/abilities_tree/Ability_Path_N_E.png")
            private val NW = Image("icons/abilities_tree/Ability_Path_N_W.png")
            private val NWE = Image("icons/abilities_tree/Ability_Path_N_W_E.png")
            private val SE = Image("icons/abilities_tree/Ability_Path_S_E.png")
            private val SN = Image("icons/abilities_tree/Ability_Path_S_N.png")
            private val SW = Image("icons/abilities_tree/Ability_Path_S_W.png")
            private val SWE = Image("icons/abilities_tree/Ability_Path_S_W_E.png")
            private val WE = Image("icons/abilities_tree/Ability_Path_W_E.png")
            private val NSE = Image("icons/abilities_tree/Ability_Path_N_S_E.png")
            private val NSW = Image("icons/abilities_tree/Ability_Path_N_S_W.png")

            fun pathToImage(abilityPath: AbilityPath): Image = when (abilityPath) {
                AbilityPath.NSWE -> NSWE
                AbilityPath.NE -> NE
                AbilityPath.NW -> NW
                AbilityPath.NWE -> NWE
                AbilityPath.SE -> SE
                AbilityPath.SN -> SN
                AbilityPath.SW -> SW
                AbilityPath.SWE -> SWE
                AbilityPath.WE -> WE
                AbilityPath.NSE -> NSE
                AbilityPath.NSW -> NSW
            }
        }

    }

    private companion object AbilityAlreadyPresentsDialog : InfoDialog(
        headerText = "Ability Name already presents in the table",
        image = Image(AbilityAlreadyPresentsDialog::class.java.getResourceAsStream("/icons/warning.png"))) {

        fun show(wrapper: EntityDataWrapper<AbilityData>) {
            headerText = "${wrapper.entityData.nameInEditor} already presents in the table"
            show()
        }

    }

}