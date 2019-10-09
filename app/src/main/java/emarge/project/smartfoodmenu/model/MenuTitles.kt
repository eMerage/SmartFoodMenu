package emarge.project.smartfoodmenu.model

import com.google.gson.annotations.SerializedName

data class MenuTitles (@SerializedName("name") var name: String, @SerializedName("menuTitleID") var menuTitleID: Int = 0) {}