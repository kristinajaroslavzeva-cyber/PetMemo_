package com.name.petmemo.billing

import android.app.Activity
import com.android.billingclient.api.ProductDetails
import kotlinx.coroutines.flow.StateFlow

interface BillingClientWrapper {
    val billingState: StateFlow<BillingState>
    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails)
    fun restorePurchases()
    fun queryProductDetails()
}