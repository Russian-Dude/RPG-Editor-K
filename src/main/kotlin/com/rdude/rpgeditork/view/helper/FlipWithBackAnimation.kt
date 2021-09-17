package com.rdude.rpgeditork.view.helper

import javafx.animation.Animation
import javafx.animation.Interpolator
import javafx.geometry.Point3D
import javafx.scene.Node
import javafx.scene.layout.StackPane
import javafx.scene.transform.Rotate
import javafx.util.Duration
import tornadofx.ViewTransition
import tornadofx.rotate
import tornadofx.runLater
import tornadofx.then

class FlipWithBackAnimation(private val duration: Duration, private val vertical: Boolean = false, val backAnimation: () -> Unit) : ViewTransition()  {

    val halfTime: Duration = duration.divide(2.0)
    val targetAxis: Point3D = (if (vertical) Rotate.X_AXIS else Rotate.Y_AXIS)

    override fun create(current: Node, replacement: Node, stack: StackPane): Animation {
        val animation = current.rotate(halfTime, 90, easing = Interpolator.EASE_IN, play = false) { axis = targetAxis }.then(
            replacement.rotate(halfTime, 90, easing = Interpolator.EASE_OUT, reversed = true, play = false) {
                axis = targetAxis
            }
        )
        animation.setOnFinished { runLater { backAnimation.invoke() } }
        return animation
    }

    override fun onComplete(removed: Node, replacement: Node) {
        removed.rotate = 0.0
        removed.rotationAxis = Rotate.Z_AXIS
        replacement.rotationAxis = Rotate.Z_AXIS
    }

}