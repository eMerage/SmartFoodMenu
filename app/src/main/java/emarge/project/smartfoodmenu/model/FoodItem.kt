package emarge.project.smartfoodmenu.model

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FoodItem {

    lateinit var foodItemtitle: String
    lateinit var foodItemdescription: String
    lateinit var foodItemimage: String
    lateinit var foodItemType: String
    lateinit var foodItemSize: String
    var foodItemPrice: Double = 0.0


}