package com.adrosonic.craftexchangemarketing.syncManager.processor.moq

import android.content.Context
import android.content.Intent
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.Moqs
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.Notifications
import com.adrosonic.craftexchangemarketing.services.moq.MoqSendService
import com.adrosonic.craftexchangemarketing.services.notification.NotificationReadService
import com.adrosonic.craftexchangemarketing.syncManager.processor.ChangeProcessor
import com.adrosonic.craftexchangemarketing.syncManager.processor.InProgressTracker
import com.adrosonic.craftexchangemarketing.syncManager.processor.objects.ItemType

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