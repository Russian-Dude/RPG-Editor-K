package com.rdude.rpgeditork

import com.rdude.rpgeditork.settings.Settings
import com.rdude.rpgeditork.style.EditorStyles
import com.rdude.rpgeditork.utils.clearTempFolders
import com.rdude.rpgeditork.view.MainView
import tornadofx.App
import tornadofx.importStylesheet
import tornadofx.isLong
import tornadofx.reloadStylesheetsOnFocus
import java.nio.file.Files

class Main : App(MainView::class, EditorStyles::class) {
    init {
        clearTempFolders()
        reloadStylesheetsOnFocus()
    }
}