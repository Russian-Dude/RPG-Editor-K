package com.rdude.rpgeditork.view.cells

import com.rdude.rpgeditork.wrapper.ImageResourceWrapper
import tornadofx.*

class ImageResourceCell(imageResourceWrapper: ImageResourceWrapper) : Fragment() {

    override val root = borderpane {
        center {
            text(imageResourceWrapper.nameProperty)
        }
        right {
            imageview(imageResourceWrapper.fxRepresentation) {
                fitWidth = 25.0
                fitHeight = 25.0
            }
        }
    }
}