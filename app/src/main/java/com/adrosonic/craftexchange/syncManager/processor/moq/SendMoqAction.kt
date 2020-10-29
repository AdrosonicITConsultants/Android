package com.adrosonic.craftexchange.syncManager.processor.moq

import android.content.Context
import android.content.Intent
import com.adrosonic.craftexchange.database.entities.realmEntities.Moqs
import com.adrosonic.craftexchange.database.entities.realmEntities.Notifications
import com.adrosonic.craftexchange.services.moq.MoqSendService
import com.adrosonic.craftexchange.services.notification.NotificationReadService
import com.adrosonic.craftexchange.syncManager.processor.ChangeProcessor
import com.adrosonic.craftexchange.syncManager.processor.InProgressTracker
import com.adrosonic.craftexchange.syncManager.processor.objects.ItemType

class SendMoqAction : ChangeProcessor<ItemType> {

    override val elementsInProgress: InProgressTracker<ItemType>
        get() = InProgressTracker()

    override fun processChangedLocalElements(elements: List<ItemType>, context: Context) {
        elements.forEach { actionSendMoq(it, context) }
    }

    override val predicateForLocallyTrackedElements: String
        get() = "${Moqs.COLUMN_MARK_MOQ_FOR_SENDING}=1"

    fun actionSendMoq(itemType: ItemType, context: Context){
        var work = Intent().apply { putExtra(MoqSendService.KEY_ID,itemType.id) }
        MoqSendService.enqueueWork(context, work)
    }

}