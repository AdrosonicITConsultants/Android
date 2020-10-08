package com.adrosonic.craftexchangemarketing.syncManager.processor.pi

import android.content.Context
import android.content.Intent
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.PiDetails
import com.adrosonic.craftexchangemarketing.services.pi.PiService
import com.adrosonic.craftexchangemarketing.syncManager.processor.ChangeProcessor
import com.adrosonic.craftexchangemarketing.syncManager.processor.InProgressTracker
import com.adrosonic.craftexchangemarketing.syncManager.processor.objects.ItemType

class PiActions : ChangeProcessor<ItemType> {

    override val elementsInProgress: InProgressTracker<ItemType>
        get() = InProgressTracker()

    override fun processChangedLocalElements(elements: List<ItemType>, context: Context) {
        elements.forEach { actionPi(it, context) }
    }

    override val predicateForLocallyTrackedElements: String
        get() = "${PiDetails.COLUMN_MARK_PI_FOR_SAVE}=1"

    fun actionPi(itemType: ItemType, context: Context){
        var work = Intent().apply { putExtra(PiService.KEY_ID,itemType.id) }
        PiService.enqueueWork(context, work)
    }

}