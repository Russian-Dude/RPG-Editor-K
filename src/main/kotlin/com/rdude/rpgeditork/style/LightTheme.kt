package com.rdude.rpgeditork.style

import javafx.scene.effect.InnerShadow
import javafx.scene.paint.Color
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import tornadofx.*

class LightTheme : Stylesheet() {

    companion object {
        val withBorders by cssclass()
        val withRoundBorders by cssclass()
        val withoutBorders by cssclass()

        val fxexSelectorContainer by cssclass()
        val fxexSelectorContainerAddButton by cssclass()
        val fxexSelectorContainerRemoveButton by cssclass()
        val fxexSelectorContainerSearchButton by cssclass()
        val entityTopMenu by cssclass()
        val editorDialog by cssclass()

        val lightBlue = c("#00A8FF")
        val blue = c("#0097E6")
        val red = c("#E84118")
        val darkRed = c("#C23616")
        val lightPurple = c("#9C88FF")
        val purple = c("#8C7AE6")
        val yellow = c("#FBC531")
        val darkYellow = c("#E1B12C")
        val green = c("#4CD137")
        val darkGreen = c("#44BD32")
        val greyBlue = c("#487EB0")
        val darkGreyBlue = c("#40739E")
        val superWhite = c("#FFFFFF")
        val white = c("#F5F6FA")
        val whiteGrey = c("#DCDDE1")
        val lightGrey = c("#7F8FA6")
        val darkGrey = c("#718093")
        val deepBlue = c("#273C75")
        val darkDeepBlue = c("#192A56")
        val deepGrey = c("#353B48")
        val darkDeepGrey = c("#2F3640")
        val transparent = c("#000000", 0.0)
    }

    init {

        withBorders {
            borderRadius += box(0.0.px)
            backgroundRadius += box(0.0.px)
            borderWidth += box(0.5.px)
            borderColor += box(darkDeepBlue)
            and(button, scrollPane, tab, textField, textArea) {
                borderRadius += box(0.0.px)
                backgroundRadius += box(0.0.px)
                borderWidth += box(1.0.px)
                borderColor += box(darkDeepBlue)
            }
        }

        withRoundBorders {
            borderRadius += box(35.0.px)
            backgroundRadius += box(35.0.px)
            borderWidth += box(0.5.px)
            borderColor += box(darkDeepBlue)
            and(button, scrollPane, tab, textField, textArea) {
                borderRadius += box(35.0.px)
                backgroundRadius += box(35.0.px)
                borderWidth += box(1.0.px)
                borderColor += box(darkDeepBlue)
            }
        }

        withoutBorders {
            borderWidth += box(0.0.px)
            borderColor += box(transparent)
            and(button, scrollPane, tab, textField, textArea) {
                borderWidth += box(0.0.px)
                borderColor += box(transparent)
            }
        }

        root {
            focusColor = transparent
            faintFocusColor = transparent
            backgroundRadius += box(0.0.px)
            borderRadius += box(0.0.px)
            backgroundColor += transparent
            and(disabled) {
                textFill = darkGrey
            }
        }

        tabPane {
            tabHeaderArea {
                padding = box(0.0.px, 0.0.px, 0.0.px, 0.0.px)
                backgroundColor += whiteGrey
            }
            backgroundColor += white
        }

        tab {
            backgroundColor += whiteGrey
            borderColor += box(transparent, transparent, transparent, transparent)
            borderWidth += box(0.0.px, 0.0.px, 5.0.px, 0.0.px)
            and(selected) {
                borderColor += box(whiteGrey, whiteGrey, blue, whiteGrey)
                and(hover) {
                    backgroundColor += superWhite
                    borderColor += box(whiteGrey, whiteGrey, lightBlue, whiteGrey)
                }
            }
            and(hover) {
                backgroundColor += superWhite
                borderColor += box(transparent, transparent, superWhite, transparent)
            }
        }

        controlBox {
            backgroundColor += red
        }

        button {
            backgroundColor += transparent
            borderColor += box(darkDeepBlue)
            borderWidth += box(0.5.px)
            and(hover) {
                backgroundColor += superWhite
            }
            and(disabled) {
                borderColor += box(lightGrey)
            }
        }

        scrollPane {
            s(viewport) {
                backgroundColor += transparent
            }
            backgroundColor += transparent
        }

        fxexSelectorContainer {
            borderColor += box(Color.BLACK)
            borderWidth += box(0.5.px)
            textField {
                borderWidth += box(0.0.px, 0.0.px, 0.5.px, 0.5.px)
            }
            comboBoxBase {
                borderWidth += box(0.0.px, 0.0.px, 0.5.px, 0.0.px)
                textField {
                    borderWidth += box(0.0.px, 0.0.px, 0.0.px, 0.0.px)
                }
            }
            scrollPane {
                backgroundColor += superWhite
            }
        }

        fxexSelectorContainerAddButton {
            borderWidth += box(0.0.px, 0.0.px, 0.5.px, 0.0.px)
            //borderRadius += box(30.0.px, 30.0.px, 30.0.px, 30.0.px)
            //borderColor += box(purple)
            backgroundColor += transparent
            padding = box(1.0.px, 0.0.px)
            textFill = blue
            fontWeight = FontWeight.BLACK
            fontScale = 1.5
            and(hover) {
                backgroundColor += blue
                textFill = superWhite
            }
        }

        fxexSelectorContainerSearchButton {
            borderWidth += box(0.0.px)
            textFill = blue
            backgroundColor += transparent
            and(hover) {
                backgroundColor += blue
                textFill = superWhite
            }
        }


        fxexSelectorContainerRemoveButton {
            borderWidth += box(0.0.px, 0.0.px, 0.5.px, 0.5.px)
            backgroundColor += transparent
            textFill = red
            and(hover) {
                backgroundColor += red
                textFill = superWhite
            }
        }

        comboBoxBase {
            backgroundColor += transparent
            borderColor += box(darkDeepBlue)
            borderWidth += box(0.5.px)
            borderRadius += box(0.0.px)
        }

        textField {
            backgroundColor += superWhite
            borderColor += box(darkDeepBlue)
            borderWidth += box(0.5.px)
            borderRadius += box(0.0.px)
            and(disabled) {
                borderColor += box(lightGrey)
            }
        }

        label {
            and(disabled) {
                textFill = lightGrey
            }
        }

        text {
            and(disabled) {
                textFill = lightGrey
            }
        }

        entityTopMenu {
            borderWidth += box(0.0.px)
            borderColor += box(transparent)
            button {
                borderWidth += box(0.0.px)
                borderColor += box(transparent)
            }
        }

        editorDialog {
            backgroundColor += c("#F5F6FA")
            borderColor += box(c("#353B48"))
        }


    }

}