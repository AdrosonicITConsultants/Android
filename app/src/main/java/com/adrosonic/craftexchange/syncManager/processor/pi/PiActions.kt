package com.adrosonic.craftexchange.syncManager.processor.pi

import android.content.Context
import android.content.Intent
import com.adrosonic.craftexchange.database.entities.realmEntities.PiDetails
import com.adrosonic.craftexchange.services.pi.PiService
import com.adrosonic.craftexchange.syncManager.processor.ChangeProcessor
import com.adrosonic.craftexchange.syncManager.processor.InProgressTracker
import com.adrosonic.craftexchange.syncManager.processor.objects.ItemType

class PiActions : ChangeProcessor<ItemType> {

    override val elementsInProgress: InProgressTracker<ItemType>
        get() = InProgressTracker()

    override fun processChangedLocalElements(elements: List<ItemType>, context: Context) {
        elements.forEach { actionPi(it, context) }
    }

    override val predicateForLocallyTrackedElements: String
        get() = "${PiDetails.COLUMN_MARK_PI_FOR_SAVE}=1 OR ${PiDetails.COLUMN_MARK_PI_FOR_REVISE}=1"

    fun actionPi(itemType: ItemType, context: Context){
        var work = Intent().apply { putExtra(PiService.KEY_ID,itemType.id) }
        PiService.enqueueWork(context, work)
    }

}