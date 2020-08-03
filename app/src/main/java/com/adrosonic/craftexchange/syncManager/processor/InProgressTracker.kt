package com.adrosonic.craftexchange.syncManager.processor

/**
 * Created by 'Rital Naik on 01/08/20.
 */
class InProgressTracker<T> {

    private var objectsInProgress: MutableSet<T> = mutableSetOf()

    /// Returns those objects from the given `objects` that are not yet in progress.
    /// These new objects are then marked as being in progress.
    fun objectsToProcess(objects: List<T>): List<T> {
        val added = objects.filter { !objectsInProgress.contains(it) }
        objectsInProgress.union(added)
        return added
    }

    /// Marks the given objects as being complete, i.e. no longer in progress.
    fun markObjectsAsComplete(objects: List<T>) {
        objectsInProgress.subtract(objects)
    }
}