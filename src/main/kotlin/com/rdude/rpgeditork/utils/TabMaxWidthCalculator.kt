package com.rdude.rpgeditork.utils

import javafx.collections.ListChangeListener
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import tornadofx.add
import tornadofx.hbox
import tornadofx.onChange

private val isLimitReached: MutableMap<TabPane, Boolean> = HashMap()
private val alreadyDelegatedProperties: MutableSet<Tab> = HashSet()

fun TabPane.limitHeaderArea(widthLimit: Double, tabExtraSpace: Double = 25.0) {

    isLimitReached[this] = false
    tabs.forEach { delegateProperties(it) }

    tabs.addListener(ListChangeListener {
        while (it.next()) {

            // delegate properties
            if (it.wasAdded()) { it.addedSubList.forEach { tab -> delegateProperties(tab) } }

            // width
            val widths: MutableMap<HBox, Double> = HashMap()
            for (t in it.list) {
                if (t.graphic is HBox) {
                    widths[t.graphic as HBox] = (t.graphic as HBox).width + tabExtraSpace
                }
            }
            if (widths.isEmpty()) {
                widths.keys.forEach { hBox -> hBox.maxWidth = Double.MAX_VALUE }
                return@ListChangeListener
            }

            var max = widths.values.maxOrNull()!!
            widths.replaceAll { _, value -> if (value == tabExtraSpace) max else value }

            if (it.wasAdded()) {
                if (widths.values.sum() <= widthLimit) {
                    widths.keys.forEach { hBox -> hBox.maxWidth = Double.MAX_VALUE }
                } else {
                    while (widths.values.sum() > widthLimit) {
                        max--
                        widths.replaceAll { _, value -> if (value > max) max else value }
                    }
                    widths.keys.forEach { hBox -> hBox.maxWidth = max - tabExtraSpace }
                }
            }

            else if (it.wasRemoved()) {
                while (widths.values.sum() < widthLimit) {
                    max++
                    widths.replaceAll { _, value -> if (value < max) max else value }
                }
                widths.keys.forEach { hBox -> hBox.maxWidth = max - tabExtraSpace }
            }
        }
    })
}


private fun delegateProperties(tab: Tab) {
    if (alreadyDelegatedProperties.contains(tab)) {
        return
    }
    val text = tab.text
    val graphic = tab.graphic
    tab.text = ""
    tab.graphic = null
    var userChangeProperties = true
    val newLabel = Label(text)
    val newGraphic = if (graphic == null) AnchorPane() else AnchorPane(graphic)
    val hBox = HBox(newGraphic, newLabel).apply { alignment = Pos.CENTER_LEFT }
    tab.graphic = hBox
    tab.textProperty().onChange {
        if (userChangeProperties) {
            newLabel.text = it
            userChangeProperties = false
            tab.text = ""
            userChangeProperties = true
        }
    }
    tab.graphicProperty().onChange {
        if (userChangeProperties) {
            userChangeProperties = false
            tab.graphic = hBox
            userChangeProperties = true
            newGraphic.children.clear()
            if (it != null) {
                newGraphic.add(it)
            }
        }
    }
    alreadyDelegatedProperties.add(tab)
}


fun Collection<HBox>.calculateTabMaxWidth(allTabsMaxWidth: Double) = calculateTabsMaxWidth(this, allTabsMaxWidth)

fun calculateTabsMaxWidth(tabs: Collection<HBox>, allTabsMaxWidth: Double, tabExtraSpace: Double = 25.0): Double {
    if (tabs.isEmpty()) return Double.MAX_VALUE

    val map: MutableMap<HBox, Double> = HashMap()
    tabs.map { it to it.width + tabExtraSpace }.toMap(map)

    var max = map.values.maxOrNull()!!
    map.replaceAll { _, value -> if (value == tabExtraSpace) max else value }

    if (map.values.sum() <= allTabsMaxWidth) return Double.MAX_VALUE

    while (map.values.sum() > allTabsMaxWidth) {
        max--
        map.replaceAll { _, value -> if (value > max) max else value }
    }

    return max - tabExtraSpace
}


/*fun calculateTabsMaxWidth(tabs: Collection<HBox>, allTabsMaxWidth: Double, tabExtraSpace: Double = 25.0): Double {
    if (tabs.isEmpty()) return Double.MAX_VALUE

    val map: MutableMap<HBox, Double> = HashMap()
    tabs.map { it to it.width }.toMap(map)

    var max = map.values.map { it }.maxOrNull()!!
    map.replaceAll { _, value -> if (value == 0.0) max else value }

    if (map.values.sum() + tabs.size * tabExtraSpace < allTabsMaxWidth) return Double.MAX_VALUE

    while ((map.values.sum()) > allTabsMaxWidth) {
        max--
        map.replaceAll { _, value -> if (value > max) max else value }
    }

    return max - tabExtraSpace
}*/

/*
    fun calculateTabsMaxWidth(tabs: Collection<HBox>, allTabsMaxWidth: Double) : Double {
        if (tabs.isEmpty()) return Double.MAX_VALUE
        if (tabs.map { hBox -> hBox.width + 1000.0 }.sum() < allTabsMaxWidth) return Double.MAX_VALUE

        println("All tabs width: $allTabsMaxWidth")

        val map: MutableMap<HBox, Double> = HashMap()
        tabs.map { it to (it.width + 1000.0) }.toMap(map)
        var max = map.values.map { it }.maxOrNull()!!

        println("Max: $max")

        while (map.values.sum() > allTabsMaxWidth) {
            max--
            map.replaceAll { _, value -> if (value > max) max else value }
        }
        println("After while max: $max")
        println("=".repeat(25))
        return max
    }*/
