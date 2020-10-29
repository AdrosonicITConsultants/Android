package com.adrosonic.craftexchange.syncManager.processor

import android.content.Context

/**
 * Created by Rital Naik on 15/05/18.
 */
interface ChangeProcessor<Element> {

    /// Used to track if elements are already in progress.
    val elementsInProgress: InProgressTracker<Element>

    //val queue: Queue<Element>

    /// Any objects matching the predicate.
    fun processChangedLocalElements(elements: List<Element>, context: Context)
    /// The elements that this change processor is interested in.
    /// Used by `entityAndPredicateForLocallyTrackedObjects(in:)`.
    val predicateForLocallyTrackedElements: String

}