package com.rdude.rpgeditork.utils

import com.rdude.rpgeditork.settings.Settings
import tornadofx.DefaultErrorHandler
import java.nio.file.Files
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*

object ExceptionLogger : (DefaultErrorHandler.ErrorEvent) -> Unit {

    override fun invoke(errorEvent: DefaultErrorHandler.ErrorEvent) {
        val name = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(Date())
        Files.writeString(Path.of(Settings.logsDirectory.toString(), "$name.txt"), errorEvent.error.stackTraceToString())
    }

}