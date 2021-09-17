package com.rdude.rpgeditork.data

import com.rdude.rpgeditork.enums.dataMap
import com.rdude.rpgeditork.wrapper.*
import javafx.beans.Observable
import javafx.collections.FXCollections
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.scene.image.Image
import ru.rdude.rpg.game.logic.data.*
import java.util.stream.Stream
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.ClassData

object Data {

    val modulesMap: ObservableMap<Long, EntityDataWrapper<Module>> = FXCollections.observableHashMap()
    val modulesList: ObservableList<EntityDataWrapper<Module>> =
        FXCollections.observableArrayList { w -> arrayOf(w.entityNameProperty) }

    val skills: EntityDataHolder<SkillData> = EntityDataHolder()
    val items: EntityDataHolder<ItemData> = EntityDataHolder()
    val monsters: EntityDataHolder<MonsterData> = EntityDataHolder()
    val events: EntityDataHolder<EventData> = EntityDataHolder()
    val quests: EntityDataHolder<QuestData> = EntityDataHolder()
    val classes: EntityDataHolder<PlayerClassData> = EntityDataHolder()
    val abilities: EntityDataHolder<AbilityData> = EntityDataHolder()

    val allEntities
    get() = EntityDataHolder.allEntities

    val images: ResourceDataHolder<ImageResourceWrapper> = ResourceDataHolder()
    val sounds: ResourceDataHolder<SoundResourceWrapper> = ResourceDataHolder()
    val particles: ResourceDataHolder<ParticleResourceWrapper> = ResourceDataHolder()


    init {
        // modules map to list
        EntityDataHolder.allLists.add(modulesList as ObservableList<EntityDataWrapper<*>>)
        modulesMap.addListener(MapChangeListener { change ->
            modulesList.add(change.valueAdded)
            change.valueAdded.entityData.resources.imageResources
                .forEach { if (it != null) images.putIfAbsent(it.guid, ImageResourceWrapper(it)) }
            change.valueAdded.entityData.resources.soundResources
                .forEach { if (it != null) sounds.putIfAbsent(it.guid, SoundResourceWrapper(it)) }
            change.valueAdded.entityData.resources.particleResources
                .forEach { if (it != null) particles.putIfAbsent(it.guid, ParticleResourceWrapper(it)) }
        })
        // modules to entities
        modulesMap.addListener(MapChangeListener { change ->
            change.valueAdded.entityData.allEntities
                .map { EntityDataWrapper(it) }
                .forEach { t ->
                    t.dataMap.putIfAbsent(t.entityData.guid, t)
                    t.insideModule = change.valueAdded
                }
        })
    }

    fun <T : EntityData> addEntity(wrapper: EntityDataWrapper<T>) {
        wrapper.dataMap.putIfAbsent(wrapper.entityData.guid, wrapper)
    }

    fun getEntity(guid: Long): EntityDataWrapper<*>? =
        allEntities.stream()
            .filter { wrapper -> wrapper.entityData.guid == guid }
            .findFirst()
            .orElse(null)



    class EntityDataHolder<T : EntityData> {

        val list: ObservableList<EntityDataWrapper<T>> = FXCollections.observableArrayList { w -> arrayOf(w.entityNameProperty) }
        val map: ObservableMap<Long, EntityDataWrapper<T>> = FXCollections.observableHashMap()

        init {
            allLists.add(list as ObservableList<EntityDataWrapper<*>>)
            map.addListener(MapChangeListener { change ->
                list.add(change.valueAdded)
                change.valueAdded.entityData.resources.imageResources
                    .forEach { if (it != null) images.putIfAbsent(it.guid, ImageResourceWrapper(it)) }
                change.valueAdded.entityData.resources.soundResources
                    .forEach { if (it != null) sounds.putIfAbsent(it.guid, SoundResourceWrapper(it)) }
                change.valueAdded.entityData.resources.particleResources
                    .forEach { if (it != null) particles.putIfAbsent(it.guid, ParticleResourceWrapper(it)) }
            })
        }

        fun put(guid: Long, wrapper: EntityDataWrapper<T>) = map.put(guid, wrapper)
        fun putIfAbsent(guid: Long, wrapper: EntityDataWrapper<T>) = map.putIfAbsent(guid, wrapper)
        fun add(wrapper: EntityDataWrapper<T>) = map.putIfAbsent(wrapper.entityData.guid, wrapper)
        fun remove(guid: Long) = map.remove(guid)
        fun remove(wrapper: EntityDataWrapper<T>) = map.remove(wrapper.entityData.guid)
        operator fun get(guid: Long) = map[guid]
        operator fun set(guid: Long, wrapper: EntityDataWrapper<T>) { map[guid] = wrapper }

        companion object AllEntities {

            val allLists: MutableList<ObservableList<EntityDataWrapper<*>>> = ArrayList()
            val allEntities: List<EntityDataWrapper<*>>
                get() = allLists.flatten()

        }
    }



    class ResourceDataHolder<T : ResourceWrapper<*>> {

        val list: ObservableList<T> = FXCollections.observableArrayList{ w -> arrayOf(w.nameProperty) }
        val map: ObservableMap<Long, T> = FXCollections.observableHashMap()

        init {
            map.addListener(MapChangeListener { change ->
                if (change.wasAdded()) {
                    list.add(change.valueAdded)
                }
                else if (change.wasRemoved()) {
                    list.remove(change.valueRemoved)
                }
            })
        }

        fun put(guid: Long, wrapper: T) = map.put(guid, wrapper)
        fun putIfAbsent(guid: Long, wrapper: T) = map.putIfAbsent(guid, wrapper)
        fun add(wrapper: T) = map.putIfAbsent(wrapper.resource.guid, wrapper)
        fun remove(guid: Long) = map.remove(guid)
        fun remove(wrapper: T) = map.remove(wrapper.resource.guid)
        operator fun get(guid: Long?) = map[guid]
        operator fun set(guid: Long, wrapper: T) { map[guid] = wrapper }
    }
}