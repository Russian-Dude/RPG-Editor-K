package com.rdude.rpgeditork

import com.rdude.rpgeditork.settings.Settings
import com.rdude.rpgeditork.style.LightTheme
import com.rdude.rpgeditork.utils.clearTempFolders
import com.rdude.rpgeditork.view.MainView
import javafx.beans.value.ChangeListener
import tornadofx.App
import tornadofx.importStylesheet
import tornadofx.reloadStylesheetsOnFocus
import tornadofx.removeStylesheet

class Main : App(MainView::class) {
    init {
        clearTempFolders()
        reloadStylesheetsOnFocus()

        importStylesheet(Settings.styleTheme.clazz)
        Settings.styleThemeProperty.addListener { _, old, new ->
            removeStylesheet(old.clazz)
            importStylesheet(new.clazz)
        }
    }
}