package emarge.project.smartfoodmenu.viewModels


import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Base64
import android.view.View
import androidx.lifecycle.MutableLiveData

import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import androidx.lifecycle.ViewModel
import emarge.project.caloriecaffe.network.api.APIInterface
import emarge.project.caloriecaffe.network.api.ApiClient
import io.realm.Realm
import android.widget.AdapterView
import com.google.gson.JsonObject
import emarge.project.smartfoodmenu.db.UserTB
import emarge.project.smartfoodmenu.model.MenuTitles
import emarge.project.smartfoodmenu.model.Outlet
import emarge.project.smartfoodmenu.model.OutletImages
import java.io.ByteArrayOutputStream
import kotlin.collections.ArrayList


class MenuViewModel : ViewModel() {


    var showProgressbar = MutableLiveData<Boolean>()

    var selectedMenuCatIDForMenuTitle = MutableLiveData<Int>()


    var selectedOutletID = MutableLiveData<Int>()
    var selectedMenuCatID = MutableLiveData<Int>()
    var selectedMenuTitleID = MutableLiveData<Int>()



    val editTextMenuName= MutableLiveData<String>()
    val editTextMenuDescription= MutableLiveData<String>()
    val editTextMaxOrderQty= MutableLiveData<String>()
    val editTextMaxAvailableQty= MutableLiveData<String>()
    val editTextMaxCurryCount= MutableLiveData<String>()
    val editTextMaxExtrasQty= MutableLiveData<String>()
    val editTextMaxFreeItemQty= MutableLiveData<String>()


    var menuAddingMsg = MutableLiveData<String>()
    var menuAddingResult = MutableLiveData<Int>()


    //dialog
    val editTextMenuTitle= MutableLiveData<String>()
  //  var menuTitleImage = MutableLiveData<Bitmap>()
    var menuTitleImage = MutableLiveData<String>()
    var menuTitleAddingMsg = MutableLiveData<String>()



    var outletsList = MutableLiveData<ArrayList<Outlet>>()
    var outletsListgettingMsg = MutableLiveData<String>()


    var menuTitlesList = MutableLiveData<ArrayList<MenuTitles>>()
    var menuTitlesListgettingMsg = MutableLiveData<String>()



    var cm: ConnectivityManager? = null
    var contx: Context? = null
    var apiInterface: APIInterface? = null
    lateinit var realm: Realm


    //var menuImage = MutableLiveData<Bitmap>()
    var menuImage = MutableLiveData<String>()


    var imageName = ""

    fun setViewListener(con : Context) {
        contx = con
        cm = con.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        realm = Realm.getDefaultInstance()
        apiInterface = ApiClient.client()

        getOutlets()


    }

   /* fun setSelectedImageBitmap(bitmap : Bitmap){
        menuImage.value = bitmap
    }*/

    fun setSelectedImageBitmap(bitmap : String){
        menuImage.value = bitmap
    }


   /* fun setSelectedImageBitmapMenuTitle(bitmap : Bitmap){
        menuTitleImage.value = bitmap
    }*/

    fun setSelectedImageBitmapMenuTitle(bitmap : String){
        menuTitleImage.value = bitmap
    }


    fun setSelectedOutletID(id : Int){
        selectedOutletID.value = id
    }

    fun setSelectedMenuTitleID(id : Int){
        selectedMenuTitleID.value = id
    }


    fun onItemSelectedMenuCategory(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        var menucat : String = parent.getItemAtPosition(position).toString()
        when (menucat) {
            "Breakfast" -> selectedMenuCatID.value = 1
            "Lunch" -> selectedMenuCatID.value = 2
            "Dinner" -> selectedMenuCatID.value = 3
            "BR/LU/DI" -> selectedMenuCatID.value = 1007
            "L/D" -> selectedMenuCatID.value = 1008

        }

        getMenuTitles(selectedMenuCatID.value!!)
    }

    fun onItemSelectedMenuCategoryForMenuTitle(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        var menucat : String = parent.getItemAtPosition(position).toString()
        when (menucat) {
            "Breakfast" -> selectedMenuCatID.value = 1
            "Lunch" -> selectedMenuCatID.value = 2
            "Dinner" -> selectedMenuCatID.value = 3
            "BR/LU/DI" -> selectedMenuCatID.value = 1007
            "L/D" -> selectedMenuCatID.value = 1008
        }
    }


    fun onAddMenuTitleClick() {

        val activeNetwork: NetworkInfo? = cm!!.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        when {
            !isConnected -> menuTitleAddingMsg.value = "No internet connection !"
            editTextMenuTitle.value==null -> menuTitleAddingMsg.value = "Please fill Menu"
            menuTitleImage.value==null -> menuTitleAddingMsg.value = "Please select image"
            else -> sendNewMenuTitleToServer()
        }

    }


    fun sendNewMenuTitleToServer(){
        showProgressbar.value = true


        val jsonObject = JsonObject()
        jsonObject.addProperty("MenuCategoryID",selectedMenuCatIDForMenuTitle.value)
        jsonObject.addProperty("Name", editTextMenuTitle.value)
        jsonObject.addProperty("ImageBase64", menuTitleImage.value!!)



        apiInterface!!.saveMenuTitle(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(log: Int) {
                        if(log==0){
                            menuTitleAddingMsg.value = "Menu Title adding successfully"
                        }else{
                            menuTitleAddingMsg.value = "Menu Title adding Fail,Try again"
                        }

                    }
                    override fun onError(e: Throwable) {
                        println("eror : $e")
                        showProgressbar.value = false

                    }

                    override fun onComplete() {
                        showProgressbar.value = false
                    }
                })




    }



    fun onAddMenuClick() {

        val activeNetwork: NetworkInfo? = cm!!.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        when {
            !isConnected -> menuAddingMsg.value = "No internet connection !"
            selectedOutletID.value==null -> menuAddingMsg.value = "Please select Outlet"
            selectedMenuTitleID.value==null -> menuAddingMsg.value = "Please select Menu Title"
            editTextMenuName.value.isNullOrEmpty() -> menuAddingMsg.value = "Please fill the Menu Name"
            editTextMenuDescription.value.isNullOrEmpty() -> menuAddingMsg.value = "Please fill the Menu Description"
            editTextMaxOrderQty.value.isNullOrEmpty() -> menuAddingMsg.value = "Please fill Max Order Qty"
            editTextMaxAvailableQty.value.isNullOrEmpty() -> menuAddingMsg.value = "Please fill Max Available Qty"
            editTextMaxCurryCount.value.isNullOrEmpty() -> menuAddingMsg.value = "Please fill Max Curry Count"
            editTextMaxExtrasQty.value.isNullOrEmpty() -> menuAddingMsg.value = "Please fill Max Extras Qty"
            editTextMaxFreeItemQty.value.isNullOrEmpty() -> menuAddingMsg.value = "Please fill Max Free Item Qty"
            menuImage.value==null -> menuAddingMsg.value = "Please select image"
            else -> sendNewMenuToServer()
        }

    }

    fun sendNewMenuToServer(){

        showProgressbar.value = true

        val jsonObject = JsonObject()

        jsonObject.addProperty("outletID", selectedOutletID.value)
        jsonObject.addProperty("menuCategoryID", selectedMenuCatID.value)
        jsonObject.addProperty("menuTitleID", selectedMenuTitleID.value)
        jsonObject.addProperty("outletMenuName", editTextMenuName.value)
        jsonObject.addProperty("outletMenuDescription", editTextMenuName.value)
        jsonObject.addProperty("MaxOrderQty", editTextMaxOrderQty.value)
        jsonObject.addProperty("MaxAvailableQty", editTextMaxAvailableQty.value)
        jsonObject.addProperty("MaxCurryCount", editTextMaxCurryCount.value)
        jsonObject.addProperty("MaxExtrasQty", editTextMaxExtrasQty.value)
        jsonObject.addProperty("MaxFreeItemQty",editTextMaxFreeItemQty.value)
        jsonObject.addProperty("ImageBase64",  menuImage.value!!)


        println("ddddddddd : "+jsonObject)



        apiInterface!!.registerOutletMenuTitle(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(log: Int) {
                        menuAddingResult.value = log

                    }
                    override fun onError(e: Throwable) {
                        showProgressbar.value = false

                    }

                    override fun onComplete() {
                        showProgressbar.value = false
                    }
                })


    }

    fun getMenuTitles(menuCategoryID : Int) {

        println("rrrrrrrrrrr menuCategoryID : "+menuCategoryID)


        val activeNetwork: NetworkInfo? = cm!!.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if(!isConnected){
            menuTitlesListgettingMsg.value = "No internet connection !"
        }else{
            showProgressbar.value = true
            val user = realm.where(UserTB::class.java!!).findFirst()

            println("rrrrrrrrrrr user : "+user)

            if (user != null) {
                user.userID?.let {
                    apiInterface!!.getMenuTitles(menuCategoryID.toString(),it,"","","")
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : Observer<ArrayList<MenuTitles>> {
                                override fun onSubscribe(d: Disposable) {}
                                override fun onNext(log: ArrayList<MenuTitles>) {
                                    menuTitlesList.value = log
                                }
                                override fun onError(e: Throwable) {
                                    showProgressbar.value = false
                                    menuTitlesListgettingMsg.value = "Something went wrong, Please try again $e"

                                }
                                override fun onComplete() {
                                    showProgressbar.value = false
                                }
                            })
                }
            }else{
                menuTitlesListgettingMsg.value = "Something went wrong, Please try again"
            }

        }
    }




    fun getOutlets() {
        val activeNetwork: NetworkInfo? = cm!!.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if(!isConnected){
            outletsListgettingMsg.value = "No internet connection !"
        }else{
            showProgressbar.value = true
            val user = realm.where(UserTB::class.java!!).findFirst()

            if (user != null) {
                user.userID?.let {
                    apiInterface!!.getOutlets(it)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : Observer<ArrayList<Outlet>> {
                                override fun onSubscribe(d: Disposable) {}
                                override fun onNext(log: ArrayList<Outlet>) {
                                    outletsList.value = log
                                }

                                override fun onError(e: Throwable) {
                                    showProgressbar.value = false
                                    outletsListgettingMsg.value = "Something went wrong, Please try again $e"
                                }

                                override fun onComplete() {
                                    showProgressbar.value = false

                                }
                            })
                }
            }else{
                outletsListgettingMsg.value = "Something went wrong, Please try again"
            }
        }
    }

    fun BitMapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }


}