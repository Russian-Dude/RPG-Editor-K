package com.rdude.rpgeditork.view.settings

import com.rdude.rpgeditork.settings.Settings
import com.rdude.rpgeditork.style.LightTheme
import com.rdude.rpgeditork.style.StyleTheme
import tornadofx.*

class SettingsView : View() {

    override val root = form {
        style {
            borderWidth += box(1.0.px)
            borderColor += box(LightTheme.darkDeepBlue)
        }
        fieldset("Settings") {
            field("Load modules on start") {
                checkbox {
                    isSelected = Settings.autoLoadModules
                    selectedProperty().onChange { Settings.autoLoadModules = it }
                }
            }
            field("Ask autoload modules on start") {
                checkbox {
                    isSelected = Settings.askAutoLoadModules
                    selectedProperty().onChange { Settings.askAutoLoadModules = it }
                }
            }
            field("Auto save modules when entity saved") {
                checkbox {
                    isSelected = Settings.autoSaveModulesWhenEntitySaved
                    selectedProperty().onChange { Settings.autoSaveModulesWhenEntitySaved = it }
                }
            }
            field("UI theme") {
                combobox(values = StyleTheme.values().asList()) {
                    value = Settings.styleTheme
                    valueProperty().onChange { Settings.styleTheme = it ?: StyleTheme.values().first() }
                }
            }
            button("Ok") {
                action { close() }
            }
        }
    }
}