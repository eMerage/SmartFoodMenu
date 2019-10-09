package emarge.project.smartfoodmenu.model

import android.net.Uri
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.net.URI

data class OutletImages (
        @SerializedName("name")
        var name: String = "",

        @SerializedName("imageUrl")
        var imageUrl: Uri = Uri.EMPTY
        ) {}