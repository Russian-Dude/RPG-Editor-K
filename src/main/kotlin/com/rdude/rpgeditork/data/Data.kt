package com.rdude.rpgeditork.data

import com.rdude.rpgeditork.enums.dataMap
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import com.rdude.rpgeditork.wrapper.ImageResourceWrapper
import com.rdude.rpgeditork.wrapper.ResourceWrapper
import com.rdude.rpgeditork.wrapper.SoundResourceWrapper
import javafx.beans.Observable
import javafx.collections.FXCollections
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.scene.image.Image
import ru.rdude.rpg.game.logic.data.*
import java.util.stream.Stream

object Data {

    val modulesMap: ObservableMap<Long, EntityDataWrapper<Module>> = FXCollections.observableHashMap()
    val modulesList: ObservableList<EntityDataWrapper<Module>> =
        FXCollections.observableArrayList { w -> arrayOf(w.entityNameProperty) }

    val skillsMap: ObservableMap<Long, EntityDataWrapper<SkillData>> = FXCollections.observableHashMap()
    val skillsList: ObservableList<EntityDataWrapper<SkillData>> = FXCollections.observableArrayList()
    { w -> arrayOf(w.entityNameProperty, w.insideModuleProperty) }

    val itemsMap: ObservableMap<Long, EntityDataWrapper<ItemData>> = FXCollections.observableHashMap()
    val itemsList: ObservableList<EntityDataWrapper<ItemData>> = FXCollections.observableArrayList()
    { w -> arrayOf(w.entityNameProperty, w.insideModuleProperty) }

    val monstersMap: ObservableMap<Long, EntityDataWrapper<MonsterData>> = FXCollections.observableHashMap()
    val monstersList: ObservableList<EntityDataWrapper<MonsterData>> = FXCollections.observableArrayList()
    { w -> arrayOf(w.entityNameProperty, w.insideModuleProperty) }

    val eventsMap: ObservableMap<Long, EntityDataWrapper<EventData>> = FXCollections.observableHashMap()
    val eventsList: ObservableList<EntityDataWrapper<EventData>> = FXCollections.observableArrayList()
    { w -> arrayOf(w.entityNameProperty, w.insideModuleProperty) }

    val questsMap: ObservableMap<Long, EntityDataWrapper<QuestData>> = FXCollections.observableHashMap()
    val questsList: ObservableList<EntityDataWrapper<QuestData>> = FXCollections.observableArrayList()
    { w -> arrayOf(w.entityNameProperty, w.insideModuleProperty) }

    val allEntities: List<EntityDataWrapper<*>>
        get() = listOf(skillsList, itemsList, monstersList, eventsList, questsList)
            .flatten()

    val images: ObservableMap<Long, ImageResourceWrapper> = FXCollections.observableHashMap()
    val imagesList: ObservableList<ImageResourceWrapper> = FXCollections.observableArrayList()
    { w -> arrayOf(w.nameProperty) }

    val sounds: ObservableMap<Long, SoundResourceWrapper> = FXCollections.observableHashMap()
    val soundsList: ObservableList<SoundResourceWrapper> = FXCollections.observableArrayList()
    { w -> arrayOf(w.nameProperty) }


    init {
        // modules to entities
        modulesMap.addListener(MapChangeListener { change ->
            change.valueAdded.entityData.allEntities
                .map { EntityDataWrapper(it) }
                .forEach { t ->
                    t.dataMap.putIfAbsent(t.entityData.guid, t)
                    t.insideModule = change.valueAdded
                }
        })

        // maps to lists
        modulesMap.addListener(MapChangeListener { change ->
            modulesList.add(change.valueAdded)
            change.valueAdded.entityData.resources.imageResources
                .forEach { if (it != null) images.putIfAbsent(it.guid, ImageResourceWrapper(it)) }
            change.valueAdded.entityData.resources.soundResources
                .forEach { if (it != null) sounds.putIfAbsent(it.guid, SoundResourceWrapper(it)) }
        })
        skillsMap.addListener(MapChangeListener { change ->
            skillsList.add(change.valueAdded)
            change.valueAdded.entityData.resources.imageResources
                .forEach { if (it != null) images.putIfAbsent(it.guid, ImageResourceWrapper(it)) }
            change.valueAdded.entityData.resources.soundResources
                .forEach { if (it != null) sounds.putIfAbsent(it.guid, SoundResourceWrapper(it)) }
        })
        itemsMap.addListener(MapChangeListener { change ->
            itemsList.add(change.valueAdded)
            change.valueAdded.entityData.resources.imageResources
                .forEach { if (it != null) images.putIfAbsent(it.guid, ImageResourceWrapper(it)) }
            change.valueAdded.entityData.resources.soundResources
                .forEach { if (it != null) sounds.putIfAbsent(it.guid, SoundResourceWrapper(it)) }
        })
        monstersMap.addListener(MapChangeListener { change ->
            monstersList.add(change.valueAdded)
            change.valueAdded.entityData.resources.imageResources
                .forEach { if (it != null) images.putIfAbsent(it.guid, ImageResourceWrapper(it)) }
            change.valueAdded.entityData.resources.soundResources
                .forEach { if (it != null) sounds.putIfAbsent(it.guid, SoundResourceWrapper(it)) }
        })
        eventsMap.addListener(MapChangeListener { change ->
            eventsList.add(change.valueAdded)
            change.valueAdded.entityData.resources.imageResources
                .forEach { if (it != null) images.putIfAbsent(it.guid, ImageResourceWrapper(it)) }
            change.valueAdded.entityData.resources.soundResources
                .forEach { if (it != null) sounds.putIfAbsent(it.guid, SoundResourceWrapper(it)) }
        })
        questsMap.addListener(MapChangeListener { change ->
            questsList.add(change.valueAdded)
            change.valueAdded.entityData.resources.imageResources
                .forEach { if (it != null) images.putIfAbsent(it.guid, ImageResourceWrapper(it)) }
            change.valueAdded.entityData.resources.soundResources
                .forEach { if (it != null) sounds.putIfAbsent(it.guid, SoundResourceWrapper(it)) }
        })
        images.addListener(MapChangeListener { change ->
            if (change.wasAdded()) {
                imagesList.add(change.valueAdded)
            }
            else if (change.wasRemoved()) {
                imagesList.remove(change.valueRemoved)
            }
        })
        sounds.addListener(MapChangeListener { change ->
            if (change.wasAdded()) {
                soundsList.add(change.valueAdded)
            }
            else if (change.wasRemoved()) {
                soundsList.remove(change.valueRemoved)
            }
        })
    }

    fun <T : EntityData> addEntity(wrapper: EntityDataWrapper<T>) {
        wrapper.dataMap.putIfAbsent(wrapper.entityData.guid, wrapper)
    }

    fun getEntity(guid: Long): EntityDataWrapper<*>? =
        Stream.of(modulesMap, skillsMap, itemsMap, monstersMap, eventsMap, questsMap)
            .flatMap { map -> map.entries.stream() }
            .filter { entry -> entry.key == guid }
            .map { entry -> entry.value }
            .findFirst()
            .orElse(null)
}