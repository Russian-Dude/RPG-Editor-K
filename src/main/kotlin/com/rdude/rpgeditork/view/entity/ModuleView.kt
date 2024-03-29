package com.rdude.rpgeditork.view.entity

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.enums.*
import com.rdude.rpgeditork.saveload.EntityLoader
import com.rdude.rpgeditork.saveload.EntitySaver
import com.rdude.rpgeditork.saveload.ParticleFileLoader
import com.rdude.rpgeditork.settings.Settings
import com.rdude.rpgeditork.utils.ParticleImageChanger
import com.rdude.rpgeditork.utils.dialogs.Dialogs
import com.rdude.rpgeditork.utils.dialogs.InfoDialog
import com.rdude.rpgeditork.utils.loadDialog
import com.rdude.rpgeditork.utils.update
import com.rdude.rpgeditork.view.MainView
import com.rdude.rpgeditork.view.helper.EntityTopMenu
import com.rdude.rpgeditork.view.helper.SoundPlayer
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import com.rdude.rpgeditork.wrapper.ImageResourceWrapper
import com.rdude.rpgeditork.wrapper.ParticleResourceWrapper
import com.rdude.rpgeditork.wrapper.SoundResourceWrapper
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import ru.rdude.fxlib.panes.SearchPane
import ru.rdude.rpg.game.logic.data.EntityData
import ru.rdude.rpg.game.logic.data.Module
import ru.rdude.rpg.game.logic.data.resources.Resource
import ru.rdude.rpg.game.utils.Functions
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Predicate

class ModuleView(wrapper: EntityDataWrapper<Module>) : EntityView<Module>(wrapper) {

    override val nameField: TextField = textfield {
        text = entityData.name ?: ""
        changesChecker.add(this) { text }
        fieldsSaver.add {
            it.name = text
            it.nameInEditor = text
            wrapper.entityNameProperty.set(text)
        }
    }

    override val nameInEditorField = TextField()

    val description = textarea {
        text = entityData.description
        isWrapText = true
        changesChecker.add(this) { text }
        fieldsSaver.add { it.description = text }
    }

    val imagesList = ImagesList(this)

    val soundsList = SoundsList(this)

    val particleList = ParticleList(this)

    override val root = anchorpane {
        tabpane {
            fitToParentSize()
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("Main") {
                vbox {
                    paddingAll = 10.0
                    spacing = 10.0
                    hbox {
                        spacing = 5.0
                        alignment = Pos.CENTER_LEFT
                        text("Name")
                        add(nameField)
                    }
                    vbox {
                        spacing = 5.0
                        alignment = Pos.CENTER_LEFT
                        text("Description")
                        add(description.apply { maxWidth = 250.0 })
                    }
                }
            }
            tab("Entities") {
                fitToParentSize()
                scrollpane {
                    fitToParentSize()
                    hbox {
                        fitToParentSize()
                        spacing = 10.0
                        alignment = Pos.CENTER_LEFT
                        add(EntitiesList(SKILL, this@ModuleView))
                        add(EntitiesList(ITEM, this@ModuleView))
                        add(EntitiesList(MONSTER, this@ModuleView))
                        add(EntitiesList(EVENT, this@ModuleView))
                        add(EntitiesList(QUEST, this@ModuleView))
                        add(EntitiesList(CLASS, this@ModuleView))
                        add(EntitiesList(ABILITY, this@ModuleView))
                    }
                }
            }
            tab("Resources") {
                fitToParentSize()
                hbox {
                    fitToParentSize()
                    spacing = 10.0
                    alignment = Pos.CENTER_LEFT
                    add(imagesList)
                    add(soundsList)
                    add(particleList)
                }
            }
        }
        add(EntityTopMenu(wrapperProperty))
    }

    fun updateLists() {
        imagesList.update()
        soundsList.update()
        particleList.update()
    }

    override fun reasonsNotToSave(): List<String> {
        return listOf()
    }

    class EntitiesList<E : EntityData>(type: EntityDataType<E>, moduleView: ModuleView) : Fragment() {

        override val root = vbox {
            paddingAll = 10.0
            spacing = 5.0
            alignment = Pos.TOP_CENTER
            text("${type.name}s".capitalize())
            hbox {
                button(" Load from file ") {
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.MAX_VALUE
                    action {
                        val files = chooseFile(
                            title = "Open ${type.name}",
                            filters = arrayOf(FileChooser.ExtensionFilter(type.name, "*.${type.name}")),
                            initialDirectory = type.saveLoadPath.toFile(),
                            mode = FileChooserMode.Single
                        )
                        if (files.isEmpty()) {
                            return@action
                        }
                        val file = files[0].toPath()
                        type.saveLoadPath = file.parent
                        loadDialog("Loading ${type.name}...") {
                            val wrapper = find<EntityLoader>().loadFromFile(file)
                            if (wrapper == null) {
                                InfoDialog("Failed to load ${type.name}", image = Image("icons\\warning.png")).show()
                            } else {
                                find<EntitySaver>().saveToModule(wrapper, moduleView.wrapper)
                            }
                        }
                    }
                }
                button("Copy from module") {
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.MAX_VALUE
                    action {
                        val wrapper = type.defaultSearchDialog.showAndWait()
                        if (!wrapper.isEmpty) {
                            find<EntitySaver>().saveToModule(wrapper.get(), moduleView.wrapper)
                        }
                    }
                }
            }
            val insideList = FilteredList(type.dataList) { w -> w.insideModule == moduleView.wrapper }
            add(SearchPane(insideList).apply {
                setNameByProperty { w -> w.entityNameProperty }
                setTextFieldSearchBy({ w -> w.entityNameProperty.get() }, { w -> w.entityData.name })
                addContextMenuItem("Open") {
                    loadDialog("Loading ${type.name}...") {
                        find<MainView>().openEntity(it)
                    }
                }
                addContextMenuItem("Remove") {
                    moduleView.entityData.removeEntity(it.entityData)
                    it.insideModule = null
                    it.wasChanged = true
                    insideList.update()
                }
                moduleView.changesChecker.add(this) { this.listView.items.sorted() }
            })
        }
    }

    class ImagesList(private val moduleView: ModuleView) : Fragment() {

        private val predicate =
            Predicate<ImageResourceWrapper> { w -> moduleView.entityData.resources.imageResources.contains(w.resource) }
        private val filteredList = FilteredList(Data.images.list, predicate)

        override val root = vbox {
            paddingAll = 10.0
            spacing = 5.0
            alignment = Pos.TOP_CENTER
            text("Images")
            hbox {
                button(" Load from file ") {
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.MAX_VALUE
                    action {
                        loadImageFromFile()
                    }
                }
                button("Copy from module") {
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.MAX_VALUE
                    action {
                        val wrapper = Dialogs.imageSearchDialog {
                            !moduleView.wrapper.entityData.resources.imageResources.contains(it.resource)
                        }
                            .orElse(null)?.copy() ?: return@action
                        moduleView.wrapper.entityData.resources.addImageResource(wrapper.resource)
                        Data.images[wrapper.guid] = wrapper
                    }
                }
            }
            add(SearchPane(filteredList).apply {
                setNameBy { w -> w.nameProperty.get() }
                setTextFieldSearchBy({ w -> w.nameProperty.get() })
                setIcon { w -> w.fxRepresentation }
                addContextMenuItem("Show") {
                    Dialogs.showImageDialog(it)
                }

                addContextMenuItem("Rename") {
                    Dialogs.renameDialog(it.nameProperty)
                }
                addContextMenu("Replace") { replace ->
                    replace.menuItem("With image from file") {
                        val wrapper = loadImageFromFile() ?: return@menuItem
                        loadDialog(text = "Replacing...") {
                            moduleView.entityData.resources.remove(it.resource)
                            Data.allEntities.forEach { w ->
                                w.mainView?.imagePickers?.forEach { picker -> picker.imageResourceWrapper = wrapper }
                                val swaped = w.entityData.resources.swapImage(it.resource, wrapper.resource)
                                if (swaped) {
                                    moduleView.wrapper.imagesWereChanged = true
                                    find<EntitySaver>().save(w)
                                }
                            }
                            Data.particles.list.forEach { particle ->
                                ParticleImageChanger.change(particle, it.guid, wrapper.guid)
                                moduleView.wrapper.imagesWereChanged = true
                            }
                            Data.images.remove(it.guid)
                        }
                    }
                    replace.menuItem("With image from data") {
                        val wrapper = Dialogs.imageSearchDialog().orElse(null) ?: return@menuItem
                        if (wrapper == it) return@menuItem
                        loadDialog(text = "Replacing...") {
                            moduleView.entityData.resources.remove(it.resource)
                            Data.allEntities.forEach { w ->
                                w.mainView?.imagePickers?.forEach { picker -> picker.imageResourceWrapper = wrapper }
                                val swaped = w.entityData.resources.swapImage(it.resource, wrapper.resource)
                                if (swaped) find<EntitySaver>().save(w)
                            }
                            Data.particles.list.forEach { particle ->
                                ParticleImageChanger.change(particle, it.guid, wrapper.guid)
                                moduleView.wrapper.imagesWereChanged = true
                            }
                            Data.images.remove(it.guid)
                        }
                    }
                }

                addContextMenuItem("Remove") {
                    val used = Data.allEntities
                        .filter { w -> w != moduleView.wrapper && w.entityData.resources.imageResources.contains(it.resource) }
                    if (used.isNotEmpty()) {
                        val usedAsString = used
                            .take(3)
                            .map { w -> w.entityNameProperty.get() }
                            .reduce { a, b -> "$a, $b" }
                        val andAmount = if (used.size <= 3) "" else " and ${used.size - 3} more entities"
                        val question = "This image is used by $usedAsString$andAmount.\r\nRemove it anyway?"
                        if (!Dialogs.confirmationDialog(question)) {
                            return@addContextMenuItem
                        }
                    }
                    moduleView.wrapper.imagesWereChanged = true
                    moduleView.wrapper.entityData.resources.remove(it.resource)
                    used.forEach { wrapper -> wrapper.entityData.resources.remove(it.resource) }
                    Data.images.remove(it.resource.guid)
                }
                moduleView.changesChecker.add(this, true) {
                    listView.items.sorted() to listView.items.map { it.name }.sorted()
                }
            })
        }

        private fun loadImageFromFile(): ImageResourceWrapper? {
            val file = chooseFile(
                filters = arrayOf(FileChooser.ExtensionFilter("image", "*.png")),
                mode = FileChooserMode.Single,
                title = "Load image",
                initialDirectory = Settings.loadImageFolder.toFile()
            )
            if (file.isEmpty()) {
                return null
            }
            Settings.loadImageFolder = file[0].parentFile.toPath()
            val guid = Functions.generateGuid()
            Files.copy(file[0].toPath(), Path.of(Settings.tempImagesFolder.toString(), "$guid.png"))
            val imageResourceWrapper = ImageResourceWrapper(Resource(file[0].name.replace(".png", ""), guid))
            imageResourceWrapper.file.toFile().deleteOnExit()
            moduleView.wrapper.imagesWereChanged = true
            moduleView.entityData.resources.addImageResource(imageResourceWrapper.resource)
            Data.images[imageResourceWrapper.guid] = imageResourceWrapper
            return imageResourceWrapper
        }

        fun update() = filteredList.update()
    }

    class SoundsList(private val moduleView: ModuleView) : Fragment() {

        private val predicate =
            Predicate<SoundResourceWrapper> { w -> moduleView.entityData.resources.soundResources.contains(w.resource) }
        private val filteredList = FilteredList(Data.sounds.list, predicate)

        override val root = vbox {
            paddingAll = 10.0
            spacing = 5.0
            alignment = Pos.TOP_CENTER
            text("Sounds")
            hbox {
                button(" Load from file ") {
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.MAX_VALUE
                    action {
                        loadSoundsFromFile()
                    }
                }
                button("Copy from module") {
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.MAX_VALUE
                    action {
                        val wrapper = Dialogs.soundsSearchDialog.showAndWait()
                            .orElse(null)?.copy() ?: return@action
                        moduleView.wrapper.entityData.resources.addSoundResource(wrapper.resource)
                        Data.sounds[wrapper.guid] = wrapper
                    }
                }
            }
            add(SearchPane(filteredList).apply {
                setNameBy { w -> w.nameProperty.get() }
                setTextFieldSearchBy({ w -> w.nameProperty.get() })
                setCellGraphic( {SoundPlayer()}, { res, player -> player.resource = res } )

                addContextMenuItem("Rename") {
                    Dialogs.renameDialog(it.nameProperty)
                }
                addContextMenu("Replace") { replace ->
                    replace.menuItem("With sound from file") {
                        val wrapper = loadSoundsFromFile() ?: return@menuItem
                        loadDialog("Replacing...") {
                            moduleView.entityData.resources.remove(it.resource)
                            Data.allEntities.forEach { w ->
                                w.mainView?.soundPickers?.forEach { picker -> picker.soundResourceWrapper = wrapper }
                                val swaped = w.entityData.resources.swapSound(it.resource, wrapper.resource)
                                if (swaped) find<EntitySaver>().save(w)
                            }
                            Data.sounds.remove(it.guid)
                        }
                    }
                    replace.menuItem("With sound from data") {
                        val wrapper = Dialogs.soundsSearchDialog.showAndWait().orElse(null) ?: return@menuItem
                        if (wrapper == it) return@menuItem
                        loadDialog("Replacing...") {
                            moduleView.entityData.resources.remove(it.resource)
                            Data.allEntities.forEach { w ->
                                w.mainView?.soundPickers?.forEach { picker -> picker.soundResourceWrapper = wrapper }
                                val swaped = w.entityData.resources.swapSound(it.resource, wrapper.resource)
                                if (swaped) find<EntitySaver>().save(w)
                            }
                            Data.sounds.remove(it.guid)
                        }
                    }
                }

                addContextMenuItem("Remove") {
                    val used = Data.allEntities
                        .filter { w -> w != moduleView.wrapper && w.entityData.resources.soundResources.contains(it.resource) }
                    if (used.isNotEmpty()) {
                        val usedAsString = used
                            .take(3)
                            .map { w -> w.entityNameProperty.get() }
                            .reduce { a, b -> "$a, $b" }
                        val andAmount = if (used.size <= 3) "" else " and ${used.size - 3} more entities"
                        val question = "This sound is used by $usedAsString$andAmount.\r\nRemove it anyway?"
                        if (!Dialogs.confirmationDialog(question)) {
                            return@addContextMenuItem
                        }
                    }
                    moduleView.wrapper.entityData.resources.remove(it.resource)
                    used.forEach { wrapper -> wrapper.entityData.resources.remove(it.resource) }
                    Data.sounds.remove(it.resource.guid)
                }
                moduleView.changesChecker.add(this) {
                    listView.items.sorted() to listView.items.map { it.name }.sorted()
                }
            })
        }

        private fun loadSoundsFromFile(): SoundResourceWrapper? {
            val file = chooseFile(
                filters = arrayOf(FileChooser.ExtensionFilter("sound", "*.mp3")),
                mode = FileChooserMode.Single,
                title = "Load sound",
                initialDirectory = Settings.loadSoundFolder.toFile()
            )
            if (file.isEmpty()) {
                return null
            }
            Settings.loadSoundFolder = file[0].parentFile.toPath()
            val guid = Functions.generateGuid()
            Files.copy(file[0].toPath(), Path.of(Settings.tempSoundsFolder.toString(), "$guid.mp3"))
            val soundResourceWrapper = SoundResourceWrapper(Resource(file[0].name.replace(".mp3", ""), guid))
            soundResourceWrapper.file.toFile().deleteOnExit()
            moduleView.entityData.resources.addSoundResource(soundResourceWrapper.resource)
            Data.sounds[soundResourceWrapper.guid] = soundResourceWrapper
            return soundResourceWrapper
        }

        fun update() = filteredList.update()
    }

    class ParticleList(private val moduleView: ModuleView) : Fragment() {

        private val predicate =
            Predicate<ParticleResourceWrapper> { w -> moduleView.entityData.resources.particleResources.contains(w.resource) }
        private val filteredList = FilteredList(Data.particles.list, predicate)

        override val root = vbox {
            paddingAll = 10.0
            spacing = 5.0
            alignment = Pos.TOP_CENTER
            text("Particles")
            hbox {
                button(" Load from file ") {
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.MAX_VALUE
                    action {
                        loadParticlesFromFile()
                    }
                }
                button("Copy from module") {
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.MAX_VALUE
                    action {
                        val wrapper = Dialogs.particlesSearchDialog.showAndWait()
                            .orElse(null)?.copy() ?: return@action
                        moduleView.wrapper.entityData.resources.addParticleResource(wrapper.resource)
                        Data.particles[wrapper.guid] = wrapper
                    }
                }
            }
            add(SearchPane(filteredList).apply {
                setNameBy { w -> w.nameProperty.get() }
                setTextFieldSearchBy({ w -> w.nameProperty.get() })

                addContextMenuItem("Rename") {
                    Dialogs.renameDialog(it.nameProperty)
                }
                addContextMenu("Replace") { replace ->
                    replace.menuItem("With particle from file") {
                        val wrapper = loadParticlesFromFile() ?: return@menuItem
                        loadDialog("Replacing...") {
                            moduleView.entityData.resources.remove(it.resource)
                            Data.allEntities.forEach { w ->
                                w.mainView?.particleHolders?.forEach { holder -> holder.particle = wrapper }
                                val swaped = w.entityData.resources.swapParticle(it.resource, wrapper.resource)
                                if (swaped) find<EntitySaver>().save(w)
                            }
                            Data.particles.remove(it.guid)
                        }
                    }
                    replace.menuItem("With particle from data") {
                        val wrapper = Dialogs.particlesSearchDialog.showAndWait().orElse(null) ?: return@menuItem
                        if (wrapper == it) return@menuItem
                        loadDialog("Replacing...") {
                            moduleView.entityData.resources.remove(it.resource)
                            Data.allEntities.forEach { w ->
                                w.mainView?.particleHolders?.forEach { holder -> holder.particle = wrapper }
                                val swaped = w.entityData.resources.swapParticle(it.resource, wrapper.resource)
                                if (swaped) find<EntitySaver>().save(w)
                            }
                            Data.particles.remove(it.guid)
                        }
                    }
                }

                addContextMenuItem("Remove") {
                    val used = Data.allEntities
                        .filter { w -> w != moduleView.wrapper && w.entityData.resources.particleResources.contains(it.resource) }
                    if (used.isNotEmpty()) {
                        val usedAsString = used
                            .take(3)
                            .map { w -> w.entityNameProperty.get() }
                            .reduce { a, b -> "$a, $b" }
                        val andAmount = if (used.size <= 3) "" else " and ${used.size - 3} more entities"
                        val question = "This particle is used by $usedAsString$andAmount.\r\nRemove it anyway?"
                        if (!Dialogs.confirmationDialog(question)) {
                            return@addContextMenuItem
                        }
                    }
                    moduleView.wrapper.entityData.resources.remove(it.resource)
                    used.forEach { wrapper -> wrapper.entityData.resources.particleResources.remove(it.resource) }
                    moduleView.wrapper.imagesWereChanged = true
                    Data.particles.remove(it.resource.guid)
                }

                addContextMenuItem("Export") {

                }

                moduleView.changesChecker.add(this) {
                    listView.items.sorted() to listView.items.map { it.name }.sorted()
                }
            })
        }

        private fun loadParticlesFromFile(): ParticleResourceWrapper? {
            val particleAndImages = ParticleFileLoader.loadParticlesFromFile()
            if (particleAndImages != null) {
                moduleView.wrapper.imagesWereChanged = true
                particleAndImages.images.forEach {
                    moduleView.entityData.resources.addImageResource(it.resource)
                    Data.images[it.guid] = it
                }
                moduleView.entityData.resources.addParticleResource(particleAndImages.particle.resource)
                Data.particles[particleAndImages.particle.guid] = particleAndImages.particle
            }
            return particleAndImages?.particle
        }

        fun update() = filteredList.update()
    }
}