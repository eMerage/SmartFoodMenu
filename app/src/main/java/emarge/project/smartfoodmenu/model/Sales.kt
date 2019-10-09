package emarge.project.smartfoodmenu.model

import com.google.gson.annotations.SerializedName

data class Sales(
        @SerializedName("monthlyTotalSale") var monthlyTotalSale: String,
        @SerializedName("monthlyTotalSaleQty") var monthlyTotalSaleQty: String,
        @SerializedName("todayTotalSale") var todayTotalSale: String,
        @SerializedName("todayTotalSaleQty") var todayTotalSaleQty: String,
        @SerializedName("todayRepComission") var todayRepComission: String,
        @SerializedName("monthlyRepComission") var monthlyRepComission: String,
        @SerializedName("outletList")  var salesList: ArrayList<SalesList>

) {}