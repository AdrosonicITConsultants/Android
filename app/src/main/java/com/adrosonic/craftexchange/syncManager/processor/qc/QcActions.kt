package com.adrosonic.craftexchange.syncManager.processor.qc

import android.content.Context
import android.content.Intent
import com.adrosonic.craftexchange.database.entities.realmEntities.PiDetails
import com.adrosonic.craftexchange.database.entities.realmEntities.QcDetails
import com.adrosonic.craftexchange.services.pi.PiService
import com.adrosonic.craftexchange.services.qc.QcService
import com.adrosonic.craftexchange.syncManager.processor.ChangeProcessor
import com.adrosonic.craftexchange.syncManager.processor.InProgressTracker
import com.adrosonic.craftexchange.syncManager.processor.objects.ItemType

class QcActions : ChangeProcessor<ItemType> {

    override val elementsInProgress: InProgressTracker<ItemType>
        get() = InProgressTracker()

    override fun processChangedLocalElements(elements: List<ItemType>, context: Context) {
        elements.forEach { actionQc(it, context) }
    }

    override val predicateForLocallyTrackedElements: String
        get() = "${QcDetails.COLUMN_MARK_QC_FOR_SAVE_SEND}=1"

    fun actionQc(itemType: ItemType, context: Context){
        var work = Intent().apply { putExtra(QcService.KEY_ID,itemType.id) }
        QcService.enqueueWork(context, work)
    }

}