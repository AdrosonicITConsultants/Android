package com.adrosonic.craftexchange.syncManager.processor

import android.content.Context
import android.content.Intent
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.services.ProductCreateService
import com.adrosonic.craftexchange.services.ProductUpdateService
import com.adrosonic.craftexchange.syncManager.processor.objects.ItemType

/**
 * Created by 'Rital Naik on 15/05/18.
 */
class ProductUpdate: ChangeProcessor<ItemType> {

    override val elementsInProgress: InProgressTracker<ItemType>
        get() = InProgressTracker()

    override fun processChangedLocalElements(elements: List<ItemType>, context: Context) {
        elements.forEach { updateProduct(it, context) }
    }

    override val predicateForLocallyTrackedElements: String
        get() = "${ArtisanProducts.COLUMN_ACTION_UPDATE}=1"


    fun updateProduct(itemType: ItemType, context: Context) {
        val work = Intent().apply { putExtra(ProductUpdateService.KEY_ID, itemType.id) }
        ProductUpdateService.enqueueWork(context, work)
    }

}