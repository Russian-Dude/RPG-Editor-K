package com.rdude.rpgeditork.settings

import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Path
import java.util.*

object Settings {

    private val properties = Properties().apply { load(FileReader("properties.properties")) }

    var autoSaveModulesWhenEntitySaved = properties.getOrDefault("autosave_modules_on_save", true).toString().toBoolean()
        set(value) {
            storeProperty("autosave_modules_on_save", value.toString())
            field = value
        }

    var askAutoLoadModules = properties.getOrDefault("ask_auto_load_modules", true).toString().toBoolean()
        set(value) {
            storeProperty("ask_auto_load_modules", value.toString())
            field = value
        }

    var autoLoadModules = properties.getOrDefault("auto_load_modules", true).toString().toBoolean()
        set(value) {
            storeProperty("auto_load_modules", value.toString())
            field = value
        }

    var modulesFolder = Path.of(properties.getOrDefault("modules_folder", "modules\\") as String)
        set(value) {
            storeProperty("modules_folder", value.toString())
            field = value
        }

    var skillsFolder = Path.of(properties.getOrDefault("skills_folder", "entities\\skills\\") as String)
        set(value) {
            storeProperty("skills_folder", value.toString())
            field = value
        }

    var itemsFolder = Path.of(properties.getOrDefault("items_folder", "entities\\items\\") as String)
        set(value) {
            storeProperty("items_folder", value.toString())
            field = value
        }

    var monstersFolder = Path.of(properties.getOrDefault("monsters_folder", "entities\\monsters\\") as String)
        set(value) {
            storeProperty("monsters_folder", value.toString())
            field = value
        }

    var eventsFolder = Path.of(properties.getOrDefault("events_folder", "entities\\events\\") as String)
        set(value) {
            storeProperty("events_folder", value.toString())
            field = value
        }

    var questsFolder = Path.of(properties.getOrDefault("quests_folder", "entities\\quests\\") as String)
        set(value) {
            storeProperty("quests_folder", value.toString())
            field = value
        }

    var tempFolder = Path.of(properties.getOrDefault("temp_folder", "temp\\") as String)
        set(value) {
            storeProperty("temp_folder", value.toString())
            field = value
        }

    var tempImagesFolder = Path.of(properties.getOrDefault("temp_images_folder", "temp\\images\\") as String)
        set(value) {
            storeProperty("temp_images_folder", value.toString())
            field = value
        }

    var tempSoundsFolder = Path.of(properties.getOrDefault("temp_sounds_folder", "temp\\sounds\\") as String)
        set(value) {
            storeProperty("temp_sounds_folder", value.toString())
            field = value
        }

    var tempPackedImagesFolder =
        Path.of(properties.getOrDefault("temp_packed_images_folder", "temp\\packed_images\\") as String)
        set(value) {
            storeProperty("temp_packed_images_folder", value.toString())
            field = value
        }

    var loadImageFolder = Path.of(properties.getOrDefault("load_image_folder", "\\") as String)
        set(value) {
            storeProperty("load_image_folder", value.toString())
            field = value
        }


    private fun storeProperty(property: String, value: String) {
        properties.setProperty(property, value)
        FileWriter(File("properties.properties")).use {
            properties.store(it, "")
        }
    }
}