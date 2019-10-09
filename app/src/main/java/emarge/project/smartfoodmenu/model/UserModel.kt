package emarge.project.smartfoodmenu.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserModel (@SerializedName("userName") var userName: String, @SerializedName("id") var id: Int = 0, @SerializedName("isActive") var isActive: Boolean = false){}