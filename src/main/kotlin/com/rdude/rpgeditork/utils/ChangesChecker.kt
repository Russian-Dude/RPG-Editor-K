package com.rdude.rpgeditork.utils

class ChangesChecker {

    private val map: MutableMap<Any, Any?> = HashMap()
    private val imagesFields: MutableSet<Any> = HashSet()
    private val getters: MutableMap<Any, (Any) -> Any?> = HashMap()

    val wasChanged: Boolean
        get() = map.filter { getters[it.key]?.invoke(it.key) != it.value }.any()

    val imagesWereChanged: Boolean
        get() = imagesFields.filter { getters[it]?.invoke(it) != map[it] }.any()

    fun <T> add(field: T, isImageField: Boolean = false, getter: (T) -> Any?) {
        if (field == null) {
            return
        }
        if (isImageField) {
            imagesFields.add(field)
        }
        map[field] = getter.invoke(field)
        getters[field] = getter as (Any) -> Any?
    }

    fun update() {
        getters.forEach {
            map[it.key] = it.value.invoke(it.key)
        }
    }

}