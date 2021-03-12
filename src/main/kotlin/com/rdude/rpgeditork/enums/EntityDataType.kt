package com.rdude.rpgeditork.enums

import com.rdude.rpgeditork.data.Data
import com.rdude.rpgeditork.settings.Settings
import com.rdude.rpgeditork.view.entity.*
import com.rdude.rpgeditork.wrapper.EntityDataWrapper
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.scene.image.Image
import ru.rdude.rpg.game.logic.data.*
import ru.rdude.rpg.game.utils.Functions
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

data class EntityDataType<E : EntityData>(
    val clazz: Class<E>,
    val dataMap: ObservableMap<Long, EntityDataWrapper<E>>,
    val dataList: ObservableList<EntityDataWrapper<E>>,
    val imageFiles: (EntityDataWrapper<E>) -> List<Path>,
    val soundFiles: (EntityDataWrapper<E>) -> List<Path> = { wrapper ->
        wrapper.entityData.resources.soundResources
            .mapNotNull { Data.sounds[it.guid]?.file }
    },
    val name: String,
    val hasPackedImages: Boolean,
    val canBeDescriber: Boolean,
    private val newViewFunc: (EntityDataWrapper<E>) -> EntityView<E>,
    val icon: Image = Image("icons/$name.png"),
    private val newEntityDataFunc: () -> EntityDataWrapper<E>,
    private val saveLoadPathSet: (Path) -> Unit,
    private val saveLoadPathGet: () -> Path
) {

    var saveLoadPath
        set(value) = saveLoadPathSet.invoke(value)
        get() = saveLoadPathGet.invoke()

    fun newView(wrapper: EntityDataWrapper<E>): EntityView<E> {
        val view = newViewFunc.invoke(wrapper)
        view.wrapper = wrapper
        wrapper.mainView = view
        return view
    }

    fun newEntity() = newEntityDataFunc.invoke()
}


val <E : EntityData> EntityDataWrapper<E>.dataMap: ObservableMap<Long, EntityDataWrapper<E>>
    get() = this.dataType.dataMap

val <E : EntityData> EntityDataWrapper<E>.dataList: ObservableList<EntityDataWrapper<E>>
    get() = this.dataType.dataList

val <E : EntityData> EntityDataWrapper<E>.imageFiles: List<Path>
    get() = this.dataType.imageFiles.invoke(this)

val <E : EntityData> EntityDataWrapper<E>.soundFiles: List<Path>
    get() = this.dataType.soundFiles.invoke(this)

val <E : EntityData> EntityDataWrapper<E>.entityTypeName: String
    get() = this.dataType.name

val <E : EntityData> EntityDataWrapper<E>.hasPackedImages: Boolean
    get() = this.dataType.hasPackedImages

fun <E : EntityData> EntityDataWrapper<E>.createNewView() = dataType.newView(this)

fun entityDataTypeOf(name: String) = when (name.toLowerCase()) {
    "module" -> MODULE
    "skill" -> SKILL
    "item" -> ITEM
    "monster" -> MONSTER
    "event" -> EVENT
    "quest" -> QUEST
    else -> null
}

inline fun <reified E : EntityData> entityDataTypeOf(): EntityDataType<E> = when (E::class.java) {
    is Module -> MODULE as EntityDataType<E>
    is SkillData -> SKILL as EntityDataType<E>
    is ItemData -> ITEM as EntityDataType<E>
    is MonsterData -> MONSTER as EntityDataType<E>
    is EventData -> EVENT as EntityDataType<E>
    is QuestData -> QUEST as EntityDataType<E>
    else -> throw IllegalArgumentException("Wrapper for  ${E::class.java} not implemented")
}

fun <E : EntityData> entityDataTypeOf(entityData: E) = when (entityData) {
    is Module -> MODULE as EntityDataType<E>
    is SkillData -> SKILL as EntityDataType<E>
    is ItemData -> ITEM as EntityDataType<E>
    is MonsterData -> MONSTER as EntityDataType<E>
    is EventData -> EVENT as EntityDataType<E>
    is QuestData -> QUEST as EntityDataType<E>
    else -> throw IllegalArgumentException("Wrapper for  ${entityData::class.java} not implemented")
}

val MODULE = EntityDataType(
    clazz = Module::class.java,
    dataMap = Data.modulesMap,
    dataList = Data.modulesList,
    imageFiles = { wrapper ->
        val path = Path.of(Settings.tempPackedImagesFolder.toString(), wrapper.entityData.guid.toString())
        if (Files.notExists(path)) {
            emptyList()
        } else {
            Files.list(path).collect(Collectors.toList())
        }
    },
    name = "module",
    hasPackedImages = true,
    canBeDescriber = false,
    newViewFunc = { wrapper -> ModuleView(wrapper) },
    newEntityDataFunc = { EntityDataWrapper(Module(Functions.generateGuid())) },
    saveLoadPathGet = { Settings.modulesFolder },
    saveLoadPathSet = { Settings.modulesFolder = it }
)

val SKILL = EntityDataType(
    clazz = SkillData::class.java,
    dataMap = Data.skillsMap,
    dataList = Data.skillsList,
    imageFiles = { wrapper ->
        wrapper.entityData.resources.imageResources
            .filterNotNull()
            .mapNotNull { Data.images[it.guid]?.file }
    },
    name = "skill",
    hasPackedImages = false,
    canBeDescriber = true,
    newViewFunc = { wrapper ->
        if (wrapper.entityData.isDescriber) SkillDescriberView(wrapper)
        else SkillView(wrapper)
    },
    newEntityDataFunc = { EntityDataWrapper(SkillData(Functions.generateGuid())) },
    saveLoadPathGet = { Settings.skillsFolder },
    saveLoadPathSet = { Settings.skillsFolder = it }
)

val ITEM = EntityDataType(
    clazz = ItemData::class.java,
    dataMap = Data.itemsMap,
    dataList = Data.itemsList,
    imageFiles = { wrapper ->
        wrapper.entityData.resources.imageResources
            .filterNotNull()
            .mapNotNull { Data.images[it.guid]?.file }
    },
    name = "item",
    hasPackedImages = false,
    canBeDescriber = true,
    newViewFunc = { wrapper ->
        if (wrapper.entityData.isDescriber) ItemDescriberView(wrapper)
        else ItemView(wrapper)
    },
    newEntityDataFunc = { EntityDataWrapper(ItemData(Functions.generateGuid())) },
    saveLoadPathGet = { Settings.itemsFolder },
    saveLoadPathSet = { Settings.itemsFolder = it }
)

val MONSTER = EntityDataType(
    clazz = MonsterData::class.java,
    dataMap = Data.monstersMap,
    dataList = Data.monstersList,
    imageFiles = { wrapper ->
        wrapper.entityData.resources.imageResources
            .filterNotNull()
            .mapNotNull { Data.images[it.guid]?.file }
    },
    name = "monster",
    hasPackedImages = false,
    canBeDescriber = true,
    newViewFunc = { wrapper ->
        if (wrapper.entityData.isDescriber) MonsterDescriberView(wrapper)
        else MonsterView(wrapper)
    },
    newEntityDataFunc = { EntityDataWrapper(MonsterData(Functions.generateGuid())) },
    saveLoadPathGet = { Settings.monstersFolder },
    saveLoadPathSet = { Settings.monstersFolder = it }
)

val EVENT = EntityDataType(
    clazz = EventData::class.java,
    dataMap = Data.eventsMap,
    dataList = Data.eventsList,
    imageFiles = { wrapper ->
        wrapper.entityData.resources.imageResources
            .filterNotNull()
            .mapNotNull { Data.images[it.guid]?.file }
    },
    name = "event",
    hasPackedImages = false,
    canBeDescriber = false,
    newViewFunc = { wrapper -> EventView(wrapper) },
    newEntityDataFunc = { EntityDataWrapper(EventData()) },
    saveLoadPathGet = { Settings.eventsFolder },
    saveLoadPathSet = { Settings.eventsFolder = it }
)

val QUEST = EntityDataType(
    clazz = QuestData::class.java,
    dataMap = Data.questsMap,
    dataList = Data.questsList,
    imageFiles = { wrapper ->
        wrapper.entityData.resources.imageResources
            .filterNotNull()
            .mapNotNull { Data.images[it.guid]?.file }
    },
    name = "quest",
    hasPackedImages = false,
    canBeDescriber = false,
    newViewFunc = { wrapper -> QuestView(wrapper) },
    newEntityDataFunc = { EntityDataWrapper(QuestData()) },
    saveLoadPathGet = { Settings.questsFolder },
    saveLoadPathSet = { Settings.questsFolder = it }
)