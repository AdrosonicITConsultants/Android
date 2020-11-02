package com.adrosonic.craftexchange.syncManager.processor.taxInv

import android.content.Context
import android.content.Intent
import com.adrosonic.craftexchange.database.entities.realmEntities.PiDetails
import com.adrosonic.craftexchange.database.entities.realmEntities.TaxInvDetails
import com.adrosonic.craftexchange.services.pi.PiService
import com.adrosonic.craftexchange.services.taxInv.TaxInvService
import com.adrosonic.craftexchange.syncManager.processor.ChangeProcessor
import com.adrosonic.craftexchange.syncManager.processor.InProgressTracker
import com.adrosonic.craftexchange.syncManager.processor.objects.ItemType


class TaxInvoiceActions : ChangeProcessor<ItemType> {

    override val elementsInProgress: InProgressTracker<ItemType>
        get() = InProgressTracker()

    override fun processChangedLocalElements(elements: List<ItemType>, context: Context) {
        elements.forEach { actionTi(it, context) }
    }

    override val predicateForLocallyTrackedElements: String
        get() = "${TaxInvDetails.COLUMN_MARK_TI_FOR_SENDING}=1"

    fun actionTi(itemType: ItemType, context: Context){
        var work = Intent().apply { putExtra(TaxInvService.KEY_ID,itemType.id) }
        TaxInvService.enqueueWork(context, work)
    }

}