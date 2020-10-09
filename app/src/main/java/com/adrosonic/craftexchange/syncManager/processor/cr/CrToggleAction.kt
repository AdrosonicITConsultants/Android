package com.adrosonic.craftexchange.syncManager.processor.cr

import android.content.Context
import android.content.Intent
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.database.entities.realmEntities.PiDetails
import com.adrosonic.craftexchange.services.cr.CrToggleService
import com.adrosonic.craftexchange.services.pi.PiService
import com.adrosonic.craftexchange.syncManager.processor.ChangeProcessor
import com.adrosonic.craftexchange.syncManager.processor.InProgressTracker
import com.adrosonic.craftexchange.syncManager.processor.objects.ItemType

class CrToggleAction : ChangeProcessor<ItemType> {

    override val elementsInProgress: InProgressTracker<ItemType>
        get() = InProgressTracker()

    override fun processChangedLocalElements(elements: List<ItemType>, context: Context) {
        elements.forEach { actionCr(it, context) }
    }

    override val predicateForLocallyTrackedElements: String
        get() = "${Orders.COLUMN_ACTION_MARK_CR}=1"

    fun actionCr(itemType: ItemType, context: Context){
        var work = Intent().apply { putExtra(CrToggleService.KEY_ID,itemType.id) }
        CrToggleService.enqueueWork(context, work)
    }

}