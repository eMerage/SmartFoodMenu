package emarge.project.smartfoodmenu.model

import com.google.gson.annotations.SerializedName

data class FoodItemsCategorys(@SerializedName("name") var name: String, @SerializedName("id") var id: Int = 0) {}