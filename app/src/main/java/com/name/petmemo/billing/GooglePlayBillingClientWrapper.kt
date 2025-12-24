package com.name.petmemo.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.name.petmemo.billing.BillingConstants.MONTHLY_SUB_ID
import com.name.petmemo.billing.BillingConstants.YEARLY_SUB_ID
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton
data class BillingState(
    val isProUser: Boolean = false,
    val monthlyProductDetails: ProductDetails? = null,
    val yearlyProductDetails: ProductDetails? = null
)

@Singleton
class GooglePlayBillingClientWrapper @Inject constructor(
    @ApplicationContext private val context: Context
) : BillingClientWrapper, PurchasesUpdatedListener, BillingClientStateListener {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private val _billingState = MutableStateFlow(BillingState())
    override val billingState: StateFlow<BillingState> = _billingState.asStateFlow()
    private val productDetailsMap = mutableMapOf<String, ProductDetails>()

    init {
        connectToGooglePlay()
    }

    private fun connectToGooglePlay() {
        if (!billingClient.isReady) {
            billingClient.startConnection(this)
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            queryProductDetails()
            queryPurchases()
        } else {
        }
    }

    override fun onBillingServiceDisconnected() {
        connectToGooglePlay()
    }
    override fun queryProductDetails() {
        if (!billingClient.isReady) return

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(MONTHLY_SUB_ID)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(YEARLY_SUB_ID)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetailsList.forEach { details -> productDetailsMap[details.productId] = details }

                _billingState.update {
                    it.copy(
                        monthlyProductDetails = productDetailsMap[MONTHLY_SUB_ID],
                        yearlyProductDetails = productDetailsMap[YEARLY_SUB_ID]
                    )
                }
            } else {
            }
        }
    }

    // 4. Запрос текущих покупок (СТАТУС PRO)
    private fun queryPurchases() {
        if (!billingClient.isReady) return

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                processPurchases(purchaseList)
            } else {
            }
        }
    }

    // 5. Запуск потока покупки (включая ПРОМОКОДЫ)
    override fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        if (!billingClient.isReady) {
            return
        }

        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
        if (offerToken == null) {
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        val billingResult = billingClient.launchBillingFlow(activity, flowParams)
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
        }
    }

    // 6. Обработка результатов покупки (Коллбэк)
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            processPurchases(purchases)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
        }
    }

    override fun restorePurchases() {
        queryPurchases()
    }

    private fun processPurchases(purchases: List<Purchase>) {
        var isPro = false
        for (purchase in purchases) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                (purchase.products.contains(MONTHLY_SUB_ID) || purchase.products.contains(YEARLY_SUB_ID))
            ) {
                isPro = true
                if (!purchase.isAcknowledged) {
                    acknowledgePurchase(purchase.purchaseToken)
                }
            }
        }
        _billingState.update { it.copy(isProUser = isPro) }
    }

    private fun acknowledgePurchase(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            } else {
            }
        }
    }
}