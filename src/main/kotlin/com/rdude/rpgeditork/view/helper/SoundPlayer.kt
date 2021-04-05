package com.rdude.rpgeditork.view.helper

import com.rdude.rpgeditork.wrapper.SoundResourceWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.layout.VBox
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import tornadofx.*

class SoundPlayer(private val isListCell: Boolean = true) : VBox() {

    companion object Instances {
        val instances: MutableSet<MediaPlayer> = HashSet()
    }

    private val mediaProperty = SimpleObjectProperty<Media>()
    private var player: MediaPlayer? = null

    var media: Media?
        get() = mediaProperty.value
        set(value) {
            mediaProperty.value = value
            player?.stop()
            player?.dispose()
            player?.let { instances.remove(it) }
            if (value != null) {
                player = MediaPlayer(value).also { instances.add(it) }
            }
            player?.setOnEndOfMedia { player?.stop() }
            player?.statusProperty()?.onChange { button.text = if (it == MediaPlayer.Status.PLAYING) "⏹" else "▶" }
        }

    var resource: SoundResourceWrapper? = null
    set(value) {
        field = value
        media = value?.fxRepresentation
    }

    private var button = button("▶") {
        prefWidth = 30.0
        action {
            if (player?.status == MediaPlayer.Status.PLAYING) {
                player?.stop()
            }
            else {
                instances.filter { it != null && it != player }.forEach { it.stop() }
                player?.play()
            }
        }
    }


    val root = hbox {
        spacing = 5.0
        if (isListCell) {
            paddingRight = 10.0
        }
        else {
            alignment = Pos.CENTER
        }
        add(button)
    }.apply { this@SoundPlayer.add(this) }

}