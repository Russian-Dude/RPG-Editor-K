package com.rdude.rpgeditork.style

import javafx.scene.effect.InnerShadow
import tornadofx.*

class EditorStyles : Stylesheet() {

    companion object {
        val withBorders by cssclass()
        val withRoundBorders by cssclass()
        val withoutBorders by cssclass()

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
            borderWidth += box(1.0.px)
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
            borderWidth += box(1.0.px)
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

        button {
            backgroundColor += transparent
            borderColor += box(darkDeepBlue)
            borderWidth += box(0.5.px)
            and(hover) {
                backgroundColor += superWhite
            }
        }

        scrollPane {
            backgroundColor += transparent
        }
    }

}