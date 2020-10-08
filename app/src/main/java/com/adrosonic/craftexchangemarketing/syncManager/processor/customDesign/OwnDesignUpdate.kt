package com.adrosonic.craftexchangemarketing.syncManager.processor.customDesign

import android.content.Context
import android.content.Intent
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.BuyerCustomProduct
import com.adrosonic.craftexchangemarketing.services.customDesign.AddOwnProductService
import com.adrosonic.craftexchangemarketing.services.customDesign.UpdateOwnProductService
import com.adrosonic.craftexchangemarketing.syncManager.processor.ChangeProcessor
import com.adrosonic.craftexchangemarketing.syncManager.processor.InProgressTracker
import com.adrosonic.craftexchangemarketing.syncManager.processor.objects.ItemType

class OwnDesignUpdate :ChangeProcessor<ItemType> {

    override val elementsInProgress: InProgressTracker<ItemType>
        get() = InProgressTracker()

    override fun processChangedLocalElements(elements: List<ItemType>, context: Context) {
        elements.forEach { addProduct(it, context) }
    }

    override val predicateForLocallyTrackedElements: String
        get() = "${BuyerCustomProduct.COLUMN_ACTION_EDITED}=1"


    fun addProduct(itemType: ItemType, context: Context) {
        val work = Intent().apply { putExtra(AddOwnProductService.KEY_ID, itemType.id) }
        UpdateOwnProductService.enqueueWork(context, work)
    }

}