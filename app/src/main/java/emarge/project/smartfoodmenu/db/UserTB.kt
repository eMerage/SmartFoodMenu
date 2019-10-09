package emarge.project.smartfoodmenu.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserTB : RealmObject() {


    @PrimaryKey
    var rowID: Int? = null
    var userID: Int? = null
    var userName: String? = null




}