package com.rdude.rpgeditork.data

import com.rdude.rpgeditork.enums.dataMap
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import com.rdude.rpgeditork.wrapper.ImageResourceWrapper
import com.rdude.rpgeditork.wrapper.ResourceWrapper
import javafx.collections.FXCollections
import javafx.collections.MapChangeListener
import javafx.scene.image.Image
import ru.rdude.rpg.game.logic.data.*
import java.util.stream.Stream

object Data {

    val modulesMap = FXCollections.observableHashMap<Long, EntityDataWrapper<Module>>()
    val modulesList = FXCollections.observableArrayList<EntityDataWrapper<Module>>()

    val skillsMap = FXCollections.observableHashMap<Long, EntityDataWrapper<SkillData>>()
    val skillsList = FXCollections.observableArrayList<EntityDataWrapper<SkillData>>()

    val itemsMap = FXCollections.observableHashMap<Long, EntityDataWrapper<ItemData>>()
    val itemsList = FXCollections.observableArrayList<EntityDataWrapper<ItemData>>()

    val monstersMap = FXCollections.observableHashMap<Long, EntityDataWrapper<MonsterData>>()
    val monstersList = FXCollections.observableArrayList<EntityDataWrapper<MonsterData>>()

    val eventsMap = FXCollections.observableHashMap<Long, EntityDataWrapper<EventData>>()
    val eventsList = FXCollections.observableArrayList<EntityDataWrapper<EventData>>()

    val questsMap = FXCollections.observableHashMap<Long, EntityDataWrapper<QuestData>>()
    val questsList = FXCollections.observableArrayList<EntityDataWrapper<QuestData>>()

    val images = FXCollections.observableHashMap<Long, ImageResourceWrapper>()

    // TODO: 25.02.2021 sounds should not be images :)
    val sounds = FXCollections.observableHashMap<Long, ResourceWrapper<Image>>()


    init {
        // modules to entities
        modulesMap.addListener(MapChangeListener { change ->
            change.valueAdded.entityData.allEntities
                .map { EntityDataWrapper(it) }
                .forEach { t -> t.dataMap.putIfAbsent(t.entityData.guid, t) }
        })

        // maps to lists
        modulesMap.addListener(MapChangeListener { change ->
            modulesList.add(change.valueAdded)
            change.valueAdded.entityData.resources.imageResources
                .forEach { if (it != null) images.putIfAbsent(it.guid, ImageResourceWrapper(it)) }
        })
        skillsMap.addListener(MapChangeListener { change ->
            skillsList.add(change.valueAdded)
            change.valueAdded.entityData.resources.imageResources
                .forEach { if (it != null) images.putIfAbsent(it.guid, ImageResourceWrapper(it)) }
        })
        itemsMap.addListener(MapChangeListener { change ->
            itemsList.add(change.valueAdded)
            change.valueAdded.entityData.resources.imageResources
                .forEach { if (it != null) images.putIfAbsent(it.guid, ImageResourceWrapper(it)) }
        })
        monstersMap.addListener(MapChangeListener { change ->
            monstersList.add(change.valueAdded)
            change.valueAdded.entityData.resources.imageResources
                .forEach { if (it != null) images.putIfAbsent(it.guid, ImageResourceWrapper(it)) }
        })
        eventsMap.addListener(MapChangeListener { change ->
            eventsList.add(change.valueAdded)
            change.valueAdded.entityData.resources.imageResources
                .forEach { if (it != null) images.putIfAbsent(it.guid, ImageResourceWrapper(it)) }
        })
        questsMap.addListener(MapChangeListener { change ->
            questsList.add(change.valueAdded)
            change.valueAdded.entityData.resources.imageResources
                .forEach { if (it != null) images.putIfAbsent(it.guid, ImageResourceWrapper(it)) }
        })

        // TODO: link sounds
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