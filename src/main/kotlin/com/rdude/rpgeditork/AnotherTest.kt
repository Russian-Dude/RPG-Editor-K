package com.rdude.rpgeditork

import tornadofx.View
import tornadofx.label
import tornadofx.vbox

class AnotherTest : View() {

    override val root = vbox {
        label("upper")
        label("bottom")
    }
}