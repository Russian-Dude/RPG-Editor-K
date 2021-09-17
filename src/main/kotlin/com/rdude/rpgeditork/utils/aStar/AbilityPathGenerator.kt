package com.rdude.rpgeditork.utils.aStar

import com.rdude.rpgeditork.view.helper.AbilitiesTable
import ru.rdude.rpg.game.logic.playerClass.AbilityPath
import ru.rdude.rpg.game.utils.aStar.AStarGraph
import ru.rdude.rpg.game.utils.aStar.AStarRouteFinder
import ru.rdude.rpg.game.utils.aStar.AStarScorer
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class AbilityPathGenerator(private val cells: Array<Array<AbilitiesTable.CellAStarNode>>) {

    private val aStarGraph = AStarGraph(cells.flatten().toSet(), generateInitialConnections())
    private val aStarScorer = AStarScorer<AbilitiesTable.CellAStarNode> { a, b -> currentConnectionsType.invoke(a, b) }
    private val routeFinder = AStarRouteFinder(aStarGraph, aStarScorer, aStarScorer)

    private val HORIZONTAL_CONNECTIONS: (AbilitiesTable.CellAStarNode, AbilitiesTable.CellAStarNode) -> Int =
        { a, b -> if (a.isPath || b.isPath) 0 else if (a.x != b.x) 1 else 2 }
    private val VERTICAL_CONNECTIONS: (AbilitiesTable.CellAStarNode, AbilitiesTable.CellAStarNode) -> Int =
        { a, b -> if (a.isPath || b.isPath) 0 else if (a.y != b.y) 1 else 2 }
    private var currentConnectionsType: (AbilitiesTable.CellAStarNode, AbilitiesTable.CellAStarNode) -> Int = VERTICAL_CONNECTIONS

    fun preferHorizontalConnections() {
        currentConnectionsType = HORIZONTAL_CONNECTIONS
    }

    fun preferVerticalConnections() {
        currentConnectionsType = VERTICAL_CONNECTIONS
    }

    fun findExistedRoute(from: AbilitiesTable.CellAStarNode, to: AbilitiesTable.CellAStarNode): Optional<MutableList<AbilitiesTable.CellAStarNode>> {
        generateInitialConnections().forEach { aStarGraph.changeConnections(it.key, it.value) }
        return routeFinder.findRoute(from, to)
    }

    fun findRoute(from: AbilitiesTable.CellAStarNode, to: AbilitiesTable.CellAStarNode): Optional<MutableList<AbilitiesTable.CellAStarNode>> {
        generateConnections(from, to).forEach { aStarGraph.changeConnections(it.key, it.value) }
        return routeFinder.findRoute(from, to)
    }

    private fun generateInitialConnections(): Map<AbilitiesTable.CellAStarNode, Set<AbilitiesTable.CellAStarNode>> {
        val map: MutableMap<AbilitiesTable.CellAStarNode, Set<AbilitiesTable.CellAStarNode>> = HashMap()
        for (x in cells.indices) {
            for (y in 0 until cells[0].size) {
                val set: MutableSet<AbilitiesTable.CellAStarNode> = HashSet()
                val currentCell = cells[x][y]
                // from ability
                if (currentCell.isAbility) {
                    if (y < cells[0].size - 1 && cells[x][y + 1].isPath) {
                        set.add(cells[x][y + 1])
                    }
                }
                // from path
                else if (currentCell.isPath) {
                    // left
                    if (x > 0) {
                        val leftCell = cells[x - 1][y]
                        if (leftCell.isPath && (leftCell.cell.content as AbilityPath).name.contains("E")) {
                            set.add(leftCell)
                        }
                    }
                    // right cell
                    if (x < cells.size - 1) {
                        val rightCell = cells[x + 1][y]
                        if (rightCell.isPath && (rightCell.cell.content as AbilityPath).name.contains("W")) {
                            set.add(rightCell)
                        }
                    }
                    // down cell
                    if (y < cells[0].size - 1) {
                        val downCell = cells[x][y + 1]
                        if ((downCell.isAbility && (currentCell.cell.content as AbilityPath).name.contains("S")) || (downCell.isPath && (downCell.cell.content as AbilityPath).name.contains("N"))) {
                            set.add(downCell)
                        }
                    }
                }
                map[currentCell] = set
            }
        }
        return map
    }

    private fun generateConnections(from: AbilitiesTable.CellAStarNode, to: AbilitiesTable.CellAStarNode): Map<AbilitiesTable.CellAStarNode, Set<AbilitiesTable.CellAStarNode>> {
        val map: MutableMap<AbilitiesTable.CellAStarNode, Set<AbilitiesTable.CellAStarNode>> = HashMap()
        for (x in cells.indices) {
            for (y in 0 until cells[0].size) {
                val set: MutableSet<AbilitiesTable.CellAStarNode> = HashSet()
                val isAbility = cells[x][y].isAbility
                // left
                if (x > 0) {
                    val targetCell = cells[x - 1][y]
                    if (!isAbility && !targetCell.isAbility && (targetCell.isEmpty || (to.isAbility && (targetCell.connectedWith.to.contains(to.cell.contentWrapper) || targetCell.connectedWith.from.contains(from.cell.contentWrapper))))) {
                        set.add(targetCell)
                    }
                }
                // right
                if (!isAbility && x < cells.size - 1) {
                    val targetCell = cells[x + 1][y]
                    if (!isAbility && !targetCell.isAbility && (targetCell.isEmpty || (to.isAbility && (targetCell.connectedWith.to.contains(to.cell.contentWrapper) || targetCell.connectedWith.from.contains(from.cell.contentWrapper))))) {
                        set.add(targetCell)
                    }
                }
                // down
                if (y < cells[0].size - 1) {
                    val targetCell = cells[x][y + 1]
                    if ((targetCell.isEmpty || (targetCell.isAbility && targetCell.cell.contentWrapper == to.cell.contentWrapper && !isAbility) || (to.isAbility && targetCell.isPath && (targetCell.connectedWith.to.contains(to.cell.contentWrapper) || targetCell.connectedWith.from.contains(from.cell.contentWrapper))))) {
                        set.add(targetCell)
                    }
                }
                map[cells[x][y]] = set
            }
        }
        return map
    }

}