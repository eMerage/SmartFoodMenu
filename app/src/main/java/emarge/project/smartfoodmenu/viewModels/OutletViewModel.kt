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
import com.google.gson.JsonObject
import emarge.project.caloriecaffe.network.api.APIInterface
import emarge.project.caloriecaffe.network.api.ApiClient
import emarge.project.smartfoodmenu.model.City
import emarge.project.smartfoodmenu.model.District
import io.realm.Realm
import android.widget.AdapterView
import com.google.gson.JsonArray
import com.justinnguyenme.base64image.Base64Image
import emarge.project.smartfoodmenu.db.UserTB
import emarge.project.smartfoodmenu.model.OutletImages
import emarge.project.smartfoodmenu.model.UserModel
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import java.io.ByteArrayOutputStream


class OutletViewModel : ViewModel() {


    var showProgressbar = MutableLiveData<Boolean>()
    var districtList = MutableLiveData<List<District>>()
    var districtID = MutableLiveData<Int>()


    var cityList = MutableLiveData<List<City>>()
    var cityID = MutableLiveData<Int>()


    var repList = MutableLiveData<List<UserModel>>()
    var repID = MutableLiveData<Int>()


    var latitude = MutableLiveData<Double>()
    var longitude = MutableLiveData<Double>()
    var outletImage = MutableLiveData<Bitmap>()

    var outletType = MutableLiveData<String>()

    var dispatchType = MutableLiveData<String>()

    var outletAddingError = MutableLiveData<String>()




    val editText = MutableLiveData<String>()
    val editTextOwnersName = MutableLiveData<String>()
    val editTextDescription = MutableLiveData<String>()
    val editTextAddress = MutableLiveData<String>()
    val editTextEmail = MutableLiveData<String>()
    val editTextWebsite = MutableLiveData<String>()
    val editTextPhone = MutableLiveData<String>()
    val editTextPickupTime = MutableLiveData<String>()
    val editTextserviceCharge = MutableLiveData<String>()




    var outletImageList = ArrayList<OutletImages>()

    var cm: ConnectivityManager? = null
    var contx: Context? = null
    var apiInterface: APIInterface? = null
    lateinit var realm: Realm

    fun setViewListener(con: Context) {
        contx = con
        cm = con.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        realm = Realm.getDefaultInstance()
        apiInterface = ApiClient.client()
        getDistrictList()
        getCityList()
        getRepList()
    }


    fun setSelectedImages(list : ArrayList<OutletImages>) {
        outletImageList = list
    }


    fun setSelectedImageBitmap(bitmap: Bitmap) {
        outletImage.value = bitmap
    }


    fun setLocation(lat: Double, lon: Double) {
        latitude.value = lat
        longitude.value = lon
    }

    fun setSelectedDistrictID(id: Int) {
        districtID.value = id
    }

    fun setSelectedCityID(id: Int) {
        cityID.value = id
    }


    fun setSelectedRepID(id: Int) {
        repID.value = id
    }


    fun onItemSelectedNumber(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        outletType.value = parent.getItemAtPosition(position).toString()

    }

    fun onItemSelectedDeliveryOrPickUp(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        var typ: String = parent.getItemAtPosition(position).toString()
        when (typ) {
            "Delivery" -> dispatchType.value = "DL"
            "Pick Up" -> dispatchType.value = "PU"
            else -> dispatchType.value = "BOTH"
        }
    }


    fun onAddOutletClick() {

        val activeNetwork: NetworkInfo? = cm!!.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        when {
            !isConnected -> outletAddingError.value = "No internet connection !"
            editText.value.isNullOrEmpty() -> outletAddingError.value = "Please fill the Name"
            editTextDescription.value.isNullOrEmpty() -> outletAddingError.value = "Please fill the Description"
            editTextOwnersName.value.isNullOrEmpty() -> outletAddingError.value = "Please fill the Owner's Name"
            editTextAddress.value.isNullOrEmpty() -> outletAddingError.value = "Please fill the Address"
            cityID.value == null -> outletAddingError.value = "Please select City"
            districtID.value == null -> outletAddingError.value = "Please select District"
            (!editTextEmail.value.isNullOrEmpty()).and((!android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmail.value.toString()).matches())) -> outletAddingError.value = "Please enter valid Email"
            (!editTextWebsite.value.isNullOrEmpty()).and((!android.util.Patterns.WEB_URL.matcher(editTextWebsite.value.toString()).matches())) -> outletAddingError.value = "Please enter valid Web Site"
            editTextPhone.value.isNullOrEmpty() -> outletAddingError.value = "Please fill the Phone Number"
            editTextPhone.value!!.length != 10 -> outletAddingError.value = "Please enter valid Phone Number"
            outletImageList.isEmpty() -> outletAddingError.value = "Please select image"
            editTextPickupTime.value.isNullOrEmpty() -> outletAddingError.value = "Please fill the PickupTime"
            editTextserviceCharge.value.isNullOrEmpty() -> outletAddingError.value = "Please add the Service Charge"
            else -> sendNewOutletToServer()
        }

    }

    fun cityAndDistrictRefrash() {

        getCityList()
        getDistrictList()

    }


    fun sendNewOutletToServer() {
        showProgressbar.value = true


        val user = realm.where(UserTB::class.java!!).findFirst()

        val jsonObject = JsonObject()
        jsonObject.addProperty("OutletTypeCode", outletType.value)
        jsonObject.addProperty("Name", editText.value)
        jsonObject.addProperty("OwnerName", editTextOwnersName.value)
        jsonObject.addProperty("Description", editTextDescription.value)
        jsonObject.addProperty("Address", editTextAddress.value)
        jsonObject.addProperty("City", "")
        jsonObject.addProperty("CountryID", 1)
        jsonObject.addProperty("DistrictID", districtID.value)
        jsonObject.addProperty("CityID", cityID.value)
        jsonObject.addProperty("Latitude", latitude.value)
        jsonObject.addProperty("Longitude", longitude.value)
        jsonObject.addProperty("EMail", if (editTextEmail.value == null) { "" } else { editTextEmail.value })
        jsonObject.addProperty("Website", if (editTextWebsite.value == null) { "" } else { editTextWebsite.value })
        jsonObject.addProperty("Phone01", editTextPhone.value)
        jsonObject.addProperty("Phone02", "")
        jsonObject.addProperty("ImageBase64", "")
        jsonObject.addProperty("ServiceCharge", editTextserviceCharge.value)
        jsonObject.addProperty("PickupTimeInterval", editTextPickupTime.value)
        jsonObject.addProperty("UserStamp", user?.userID)
        jsonObject.addProperty("DispatchType", dispatchType.value)
        jsonObject.addProperty("IncentiveID", if ( repID.value == null) {  user?.userID } else { repID.value })



        val cartJsonArr = JsonArray()
        for (item in outletImageList) {
            val ob = JsonObject()
            ob.addProperty("ImageCode", item.name)
            cartJsonArr.add(ob)
        }

        jsonObject.add("Images", cartJsonArr)



        println("cccccccccc jsonobject :$jsonObject")


        apiInterface!!.saveOutlet(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(log: Int) {
                        if (log == 0) {
                            outletAddingError.value = "outlet adding successfully"
                        } else {
                            outletAddingError.value = "outlet adding Fail,Try again"
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


    fun getCityList() {
        val activeNetwork: NetworkInfo? = cm!!.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if (!isConnected) {
        } else {
            showProgressbar.value = true
            apiInterface!!.getCity()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<List<City>> {
                        override fun onSubscribe(d: Disposable) {}
                        override fun onNext(log: List<City>) {
                            cityList.value = log
                        }

                        override fun onError(e: Throwable) {
                            showProgressbar.value = false
                        }

                        override fun onComplete() {
                            showProgressbar.value = false
                        }
                    })
        }
    }


    fun getRepList() {
        val activeNetwork: NetworkInfo? = cm!!.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if (!isConnected) {
        } else {
            showProgressbar.value = true
            apiInterface!!.getAllReps()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<List<UserModel>> {
                        override fun onSubscribe(d: Disposable) {}
                        override fun onNext(log: List<UserModel>) {
                            repList.value = log
                        }

                        override fun onError(e: Throwable) {
                            showProgressbar.value = false
                        }

                        override fun onComplete() {
                           // showProgressbar.value = false
                        }
                    })
        }
    }


    fun getDistrictList() {
        val activeNetwork: NetworkInfo? = cm!!.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if (!isConnected) {
        } else {
            showProgressbar.value = true
            apiInterface!!.getDistrict()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<List<District>> {
                        override fun onSubscribe(d: Disposable) {}
                        override fun onNext(log: List<District>) {
                            districtList.value = log
                        }

                        override fun onError(e: Throwable) {
                            showProgressbar.value = false
                        }

                        override fun onComplete() {
                            showProgressbar.value = false
                        }
                    })
        }
    }

    fun BitMapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

}

