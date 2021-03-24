package com.rdude.rpgeditork.utils

import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.swing.filechooser.FileSystemView

fun Properties.getFileOrDefault(property: String, defaultPath: Path = FileSystemView.getFileSystemView().homeDirectory.toPath()) : Path {
    val p = Path.of(getProperty(property))
    return if (Files.notExists(p)) defaultPath else p
}