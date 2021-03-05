package com.rdude.rpgeditork

import com.rdude.rpgeditork.view.MainView
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class TestView : View() {

    val bottomView: BottomView by inject()
    val counter = SimpleStringProperty("count!")

    override val root = vbox {
        button {
            text(counter)
            action {
                runAsync {
                    for (i in 0.. 10) {
                        println("counting $i")
                        counter.set(i.toString())
                        Thread.sleep(1000)
                    }
                }
            }
        }
        borderpane {
            top = find(UpView::class).root
            center<CenterView>()
            bottom = bottomView.root

            left<LeftView>()
            right<LeftView>()
        }
        button {
            text = "go to main view"
            action {
                replaceWith<MainView>()
            }
        }
    }
}

class UpView : View() {
    override val root = label {
        text = "upper"
    }
}

class BottomView : View() {
    override val root = label {
        text = "bottom"
    }
}

class CenterView : View() {
    override val root = label {
        text = "center"
    }
}

class LeftView : Fragment() {
    override val root = label(text = "side label")
}