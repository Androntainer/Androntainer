package com.wyq0918dev.androntainer.service

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import android.util.Log
import com.android.vending.billing.IInAppBillingService

class InAppBillingService : Service() {

    private val tag = "FakeInAppStore"

    private val mInAppBillingService: IInAppBillingService.Stub =
        object : IInAppBillingService.Stub() {
            override fun isBillingSupported(
                apiVersion: Int,
                packageName: String,
                type: String
            ): Int {
                return isBillingSupportedV7(apiVersion, packageName, type, Bundle())
            }

            override fun getSkuDetails(
                apiVersion: Int,
                packageName: String,
                type: String,
                skusBundle: Bundle
            ): Bundle {
                return getSkuDetailsV10(apiVersion, packageName, type, skusBundle, Bundle())
            }

            override fun getBuyIntent(
                apiVersion: Int,
                packageName: String,
                sku: String,
                type: String,
                developerPayload: String
            ): Bundle {
                return getBuyIntentV6(
                    apiVersion,
                    packageName,
                    sku,
                    type,
                    developerPayload,
                    Bundle()
                )
            }

            override fun getPurchases(
                apiVersion: Int,
                packageName: String,
                type: String,
                continuationToken: String
            ): Bundle {
                return getPurchasesV6(apiVersion, packageName, type, continuationToken, Bundle())
            }

            override fun consumePurchase(
                apiVersion: Int,
                packageName: String,
                purchaseToken: String
            ): Int {
                return consumePurchaseV9(
                    apiVersion,
                    packageName,
                    purchaseToken,
                    Bundle()
                ).getInt("RESPONSE_CODE", 8)
            }

            override fun getBuyIntentToReplaceSkus(
                apiVersion: Int,
                packageName: String,
                oldSkus: List<String>,
                newSku: String,
                type: String,
                developerPayload: String
            ): Bundle {
                Log.d(
                    tag,
                    "getBuyIntentToReplaceSkus($apiVersion, $packageName, $newSku, $type, $developerPayload)"
                )
                val data = Bundle()
                data.putInt("RESPONSE_CODE", 4)
                return data
            }

            override fun getBuyIntentV6(
                apiVersion: Int,
                packageName: String,
                sku: String,
                type: String,
                developerPayload: String,
                extras: Bundle
            ): Bundle {
                Log.d(
                    tag,
                    "getBuyIntent($apiVersion, $packageName, $sku, $type, $developerPayload)"
                )
                val data = Bundle()
                data.putInt("RESPONSE_CODE", 4)
                return data
            }

            override fun getPurchasesV6(
                apiVersion: Int,
                packageName: String,
                type: String,
                continuationToken: String,
                extras: Bundle
            ): Bundle {
                return getPurchasesV9(apiVersion, packageName, type, continuationToken, extras)
            }

            override fun isBillingSupportedV7(
                apiVersion: Int,
                packageName: String,
                type: String,
                extras: Bundle
            ): Int {
                Log.d(
                    tag,
                    "isBillingSupported($apiVersion, $packageName, $type)"
                )
                return 0
            }

            override fun getPurchasesV9(
                apiVersion: Int,
                packageName: String,
                type: String,
                continuationToken: String,
                extras: Bundle
            ): Bundle {
                Log.d(
                    tag,
                    "getPurchases($apiVersion, $packageName, $type, $continuationToken)"
                )
                val data = Bundle()
                data.putInt("RESPONSE_CODE", 0)
                data.putStringArrayList("INAPP_PURCHASE_ITEM_LIST", ArrayList())
                data.putStringArrayList("INAPP_PURCHASE_DATA_LIST", ArrayList())
                data.putStringArrayList("INAPP_DATA_SIGNATURE_LIST", ArrayList())
                return data
            }

            override fun consumePurchaseV9(
                apiVersion: Int,
                packageName: String,
                purchaseToken: String,
                extras: Bundle
            ): Bundle {
                Log.d(
                    tag,
                    "consumePurchase($apiVersion, $packageName, $purchaseToken)"
                )
                val data = Bundle()
                data.putInt("RESPONSE_CODE", 8)
                return data
            }

            override fun getPriceChangeConfirmationIntent(
                apiVersion: Int,
                packageName: String,
                sku: String,
                type: String,
                extras: Bundle
            ): Bundle {
                Log.d(
                    tag,
                    "getPriceChangeConfirmationIntent($apiVersion, $packageName, $sku, $type)"
                )
                val data = Bundle()
                data.putInt("RESPONSE_CODE", 4)
                return data
            }

            override fun getSkuDetailsV10(
                apiVersion: Int,
                packageName: String,
                type: String,
                skuBundle: Bundle,
                extras: Bundle
            ): Bundle {
                Log.d(tag, "getSkuDetails($apiVersion, $packageName, $type)")
                val data = Bundle()
                data.putInt("RESPONSE_CODE", 0)
                data.putStringArrayList("DETAILS_LIST", ArrayList())
                return data
            }

            override fun acknowledgePurchase(
                apiVersion: Int,
                packageName: String,
                purchaseToken: String,
                extras: Bundle
            ): Bundle {
                Log.d(
                    tag,
                    "acknowledgePurchase($apiVersion, $packageName, $purchaseToken)"
                )
                val data = Bundle()
                data.putInt("RESPONSE_CODE", 8)
                return data
            }

            @Throws(RemoteException::class)
            override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
                if (super.onTransact(code, data, reply, flags)) return true
                Log.d(
                    tag,
                    "onTransact [unknown]: $code, $data, $flags"
                )
                return false
            }
        }

    override fun onBind(intent: Intent?): IBinder {
        return mInAppBillingService
    }
}