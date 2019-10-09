package emarge.project.smartfoodmenu.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Outlet (
        @SerializedName("name") var name: String,
        @SerializedName("id") var id: Int = 0,
        var isSelect: Boolean = false


) {}