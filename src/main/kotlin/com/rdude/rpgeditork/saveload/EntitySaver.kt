package com.rdude.rpgeditork.saveload

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.enums.MODULE
import com.rdude.rpgeditork.enums.hasPackedImages
import com.rdude.rpgeditork.settings.Settings
import com.rdude.rpgeditork.utils.dialogs.SimpleDialog
import com.rdude.rpgeditork.utils.cloneWithNewGuid
import com.rdude.rpgeditork.utils.loadDialog
import com.rdude.rpgeditork.view.entity.ModuleView
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.FileChooser
import ru.rdude.rpg.game.logic.data.EntityData
import ru.rdude.rpg.game.logic.data.Module
import tornadofx.Controller
import tornadofx.FileChooserMode
import tornadofx.chooseFile
import java.nio.file.Files
import java.nio.file.Path

class EntitySaver : Controller() {

    private val imageAtlasPacker = find<ImageAtlasPacker>()
    private val entityPacker = find<EntityPacker>()
    private val modulesDialog = MODULE.defaultSearchDialog

    fun <E : EntityData> save(wrapper: EntityDataWrapper<E>): Boolean {
        val insideFile = wrapper.insideFile
        val insideModule = wrapper.insideModule

        if (insideFile == null && insideModule == null) {
            return SimpleDialog(
                vertical = true,
                defaultReturn = false,
                dialogText = wrapper.mainView?.name?.get() ?: "",
                buttons = arrayOf(
                    "Save to module" to { saveToModule(wrapper) },
                    "Save to file" to { saveToFile(wrapper) },
                    "Cancel" to { false })
            )
                .showAndWait()
        }

        val s1 = insideFile?.let { saveToFile(wrapper, insideFile) }
        val s2 = insideModule?.let { saveToModule(wrapper, insideModule) }
        return s1 == true || s2 == true
    }

    fun saveToModule(wrapper: EntityDataWrapper<*>): Boolean {
        var res = false
        modulesDialog.showAndWait().ifPresent {
            loadDialog("Saving...") { res = saveToModule(wrapper, it) }
        }
        return res
    }

    fun saveToFile(wrapper: EntityDataWrapper<*>): Boolean {
        val files = chooseFile(
            filters = arrayOf(FileChooser.ExtensionFilter(wrapper.dataType.name, "*.${wrapper.dataType.name}")),
            mode = FileChooserMode.Save,
            initialDirectory = wrapper.dataType.saveLoadPath.toFile(),
            title = "Save ${wrapper.dataType.name}",
            op = { this.initialFileName = wrapper.mainView?.name?.get() ?: "Unnamed ${wrapper.dataType.name}" }
        )
        return if (files.isEmpty()) {
            false
        } else {
            var res = false
            loadDialog("Saving") {
                wrapper.dataType.saveLoadPath = files[0].toPath().parent
                res = saveToFile(wrapper, files[0].toPath())
            }
            res
        }
    }

    fun <E : EntityData> saveToModule(wrapper: EntityDataWrapper<E>, module: EntityDataWrapper<Module>): Boolean {
        if (wrapper.dataType == MODULE) {
            throw IllegalArgumentException("Module can not be saved to another module")
        }

        // check if entity is already saved to file or another module and clone it with new guid if true
        val entityToSave: EntityDataWrapper<E> =
            if (wrapper.isInsideFile || (wrapper.isInsideModule && module != wrapper.insideModule)) {
                EntityDataWrapper(wrapper.entityData.cloneWithNewGuid())
                //TODO("cloning not work")
            } else {
                wrapper
            }

        // try to save changes if entity is open in editor
        val isSaved = wrapper.mainView?.saveTo(entityToSave)
        if (isSaved == false) {
            return false
        }

        // if module does not contain images from saving entity
        val moduleImagesChanged = entityToSave.entityData.resources.imageResources
            .any { !module.entityData.resources.imageResources.contains(it) }
        if (moduleImagesChanged) {
            module.imagesWereChanged = true
        }

        // add entity to module
        module.entityData.addEntity(entityToSave.entityData)
        entityToSave.entityData.resources.imageResources.forEach { module.entityData.resources.addImageResource(it) }
        entityToSave.entityData.resources.soundResources.forEach { module.entityData.resources.addSoundResource(it) }

        // check if any entity inside module has dependency to old version of the entity - ask to swap dependencies
        if (wrapper != entityToSave && module.entityData.hasEntityDependency(wrapper.entityData.guid)) {
            val alertText =
                """
            Destination module contains entities with dependencies to the saving ${wrapper.dataType.name.toLowerCase()}.
            This ${wrapper.dataType.name.toLowerCase()} will be saved to the destination module with new id and can be considered as a new unique entity.
            Do you want to swap dependencies of the entities inside module from version inside ${wrapper.insideAsString} to this, new version?
            """
            val alert = Alert(Alert.AlertType.CONFIRMATION, alertText, ButtonType.YES, ButtonType.NO)
            alert.title = "Swap dependencies?"
            alert.showAndWait()
                .filter { it == ButtonType.YES }
                .ifPresent {
                    module.entityData.replaceEntityDependency(
                        wrapper.entityData.guid,
                        entityToSave.entityData.guid
                    )
                }
        }

        // if autoSave module setting is true save module
        if (Settings.autoSaveModulesWhenEntitySaved) {
            save(module)
        }

        // if controller exists make it hold new wrapper
        if (wrapper.mainView != null) {
            entityToSave.mainView = wrapper.mainView
            entityToSave.mainView?.wrapper = entityToSave
        }

        // set inside module to entity
        entityToSave.insideModule = module

        // set was changed to false
        entityToSave.wasChanged = false
        entityToSave.imagesWereChanged = false

        // add to data
        Data.addEntity(entityToSave)

        // update module view images and sounds list (if open)
        if (module.open) {
            (module.mainView as ModuleView).updateImagesAndSoundsList()
        }

        return true
    }


    fun <E : EntityData> saveToFile(wrapper: EntityDataWrapper<E>, file: Path): Boolean {

        val entityToSave: EntityDataWrapper<E> =
            if (wrapper.isInsideModule || (wrapper.isInsideFile && file != wrapper.insideFile)) {
                EntityDataWrapper(wrapper.entityData.cloneWithNewGuid())
            } else {
                wrapper
            }

        // try to save changes if entity is open in editor
        val isSaved = wrapper.mainView?.saveTo(entityToSave)
        if (isSaved == false) {
            return false
        }

        // if entity is of type that can have packed images and images were changed - pack images
        if (entityToSave.hasPackedImages && entityToSave.imagesWereChanged) {
            val entityPackedImagesFolder =
                Path.of(Settings.tempPackedImagesFolder.toString(), entityToSave.entityData.guid.toString())
            // if folder exists - remove old packed images
            if (Files.exists(entityPackedImagesFolder)) {
                Files.list(entityPackedImagesFolder).forEach { Files.deleteIfExists(it) }
            }
            // if folder does not exists - create it
            else {
                Files.createDirectory(entityPackedImagesFolder)
                    .apply { toFile().deleteOnExit() }
            }
            // pack images
            entityToSave.entityData.resources.imageResources
                .map { Path.of(Settings.tempImagesFolder.toString(), it.guid.toString() + ".png") }
                .toCollection(mutableListOf())
                .apply {
                    imageAtlasPacker.pack(
                        this,
                        entityPackedImagesFolder,
                        entityToSave.entityData.guid.toString()
                    )
                }
        }

        // pack entity to file
        entityPacker.pack(entityToSave, file)

        // if controller exists make it hold new wrapper
        if (wrapper.mainView != null) {
            entityToSave.mainView = wrapper.mainView
            entityToSave.mainView?.wrapper = entityToSave
        }

        // set entity inside file
        entityToSave.insideFile = file

        // set was changed to false
        entityToSave.wasChanged = false
        entityToSave.imagesWereChanged = false

        // add to data
        Data.addEntity(entityToSave)

        return true
    }
}