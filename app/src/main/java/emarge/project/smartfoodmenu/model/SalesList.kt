package emarge.project.smartfoodmenu.model

import com.google.gson.annotations.SerializedName

data class SalesList(
        @SerializedName("name") var name: String,
        @SerializedName("city") var city: String,
        @SerializedName("outletSale") var outletSale: String,
        @SerializedName("outletSaleQty") var outletSaleQty: Int = 0,
        @SerializedName("outletSaleCommission") var outletSaleCommission: String,
        @SerializedName("id") var id: Int,
        var isSelect: Boolean = false




) {}