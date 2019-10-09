package emarge.project.smartfoodmenu.viewModels


import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.view.View
import androidx.lifecycle.MutableLiveData

import java.util.ArrayList
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.ViewModel
import emarge.project.caloriecaffe.network.api.APIInterface
import emarge.project.caloriecaffe.network.api.ApiClient
import io.realm.Realm
import android.widget.AdapterView
import androidx.databinding.ObservableField
import emarge.project.smartfoodmenu.R
import emarge.project.smartfoodmenu.model.FoodItem
import android.graphics.drawable.Drawable
import android.net.NetworkInfo
import android.util.Base64
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import emarge.project.smartfoodmenu.model.FoodItemsCategorys
import java.io.ByteArrayOutputStream

import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class MenuTwoViewModel : ViewModel() {


    var showProgressbar = MutableLiveData<Boolean>()

    private var foodItemCategoryBaseType = 1
    private var foodItemCategoryCompulsory = false

    private val selectedfoodItemCategoryID = MutableLiveData<Int>()

    //Food Item Category Dialog
    val editTextFoodItemCategory = MutableLiveData<String>()
    val editTextFoodItemCategoryDescription = MutableLiveData<String>()


    val getfoodItemCategoryMsg = MutableLiveData<String>()
    var foodItemCategoryList = MutableLiveData<ArrayList<FoodItemsCategorys>>()

    val foodItemEnable = ObservableField<Boolean>()
    val foodItemCategoryEnable = ObservableField<Boolean>()



    val completeButtonEnable = ObservableField<Boolean>()

    val textFoodDescription = ObservableField<Boolean>()
    val textFoodTitle = ObservableField<Boolean>()
    val imageUpload = ObservableField<Boolean>()


    val editTextFoodItem = MutableLiveData<String>()

    val editTextFoodTitle = ObservableField<String>()
    val editTextFoodDescription = ObservableField<String>()

    private val foodItemType = MutableLiveData<String>()
    private val foodItemSize = MutableLiveData<String>()
    val editTextFoodPrice = ObservableField<String>()


    val foodAddingResponseMessage = MutableLiveData<String>()


    var foodItemArray = ArrayList<FoodItem>()
    var foodItemArrayFinal = MutableLiveData<ArrayList<FoodItem>>()


    val outletMenuTitleID = MutableLiveData<Int>()
    val outletID = MutableLiveData<Int>()


    val addFoodCompleteMsg = MutableLiveData<String>()


    //Food Item Category Dialog
    val itemCategoryAddingMessage = MutableLiveData<String>()


    var cm: ConnectivityManager? = null
    var contx: Context? = null
    var apiInterface: APIInterface? = null
    lateinit var realm: Realm


   // var foodImage = MutableLiveData<Bitmap>()
   var foodImage = MutableLiveData<String>()

    fun setViewListener(con: Context) {
        contx = con
        cm = con.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        realm = Realm.getDefaultInstance()
        apiInterface = ApiClient.client()

        foodItemEnable.set(true)
        foodItemCategoryEnable.set(true)
        getfoodItemCategory(1)


        textFoodDescription.set(true)
        textFoodTitle.set(true)
        imageUpload.set(true)
    }


    //Food Item Category Dialog
    fun onNewfoodItemCategoryBaseTypeChanged(radioGroup: RadioGroup, id: Int) {
        foodItemCategoryBaseType = if (id == R.id.radioButton5) {
            0
        } else {
            1
        }


    }

    //Food Item Category Dialog
    fun onNewfoodItemCategoryCompulsoryTypeChanged(radioGroup: RadioGroup, id: Int) {
        foodItemCategoryCompulsory = id != R.id.radioButton7
    }


    fun onfoodItemCategoryBaseType(radioGroup: RadioGroup, id: Int) {
        var type: Int = if (id == R.id.radioButton1) {
            1
        } else {
            0
        }
        getfoodItemCategory(type)
    }

    fun getOutletMenuTitleID(id: Int) {
        outletMenuTitleID.value = id
    }


    fun getOutleID(id: Int) {
        outletID.value = id
    }

    //Food Item Category Dialog
    fun onAddFoodItemCategory() {
        val activeNetwork: NetworkInfo? = cm!!.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        when {
            !isConnected -> itemCategoryAddingMessage.value = "No internet connection !"
            editTextFoodItemCategory.value.isNullOrEmpty() -> itemCategoryAddingMessage.value =
                "Please fill the Food Item Category"
            editTextFoodItemCategoryDescription.value.isNullOrEmpty() -> itemCategoryAddingMessage.value =
                "Please fill the Description"
            else -> sendNewFoodItemCategoryToServer()
        }

    }


    fun getfoodItemCategory(baseType: Int) {

        val activeNetwork: NetworkInfo? = cm!!.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if (!isConnected) {
            getfoodItemCategoryMsg.value = "No internet connection !"
        } else {
            showProgressbar.value = true

            apiInterface!!.getFoodItemCategories(baseType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ArrayList<FoodItemsCategorys>> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(log: ArrayList<FoodItemsCategorys>) {
                        foodItemCategoryList.value = log
                    }

                    override fun onError(e: Throwable) {
                        showProgressbar.value = false
                        getfoodItemCategoryMsg.value = "Something went wrong, Please try again "
                    }

                    override fun onComplete() {
                        showProgressbar.value = false
                    }
                })


        }
    }


    fun sendNewFoodItemCategoryToServer() {

        showProgressbar.value = true

        val jsonObject = JsonObject()

        jsonObject.addProperty("IsBase", foodItemCategoryBaseType)
        jsonObject.addProperty("Name", editTextFoodItemCategory.value)
        jsonObject.addProperty("Description", editTextFoodItemCategoryDescription.value)
        if (foodItemCategoryBaseType == 1) {
            foodItemCategoryCompulsory = true
        }
        jsonObject.addProperty("IsCompulsory", foodItemCategoryCompulsory)
        jsonObject.addProperty("OutletID", outletID.value)
        jsonObject.addProperty("OutletMenuTitleID", outletMenuTitleID.value)

        println("cccccccc  : " + jsonObject)

        apiInterface!!.saveFoodItemCategory(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(log: Int) {
                    if (log == 0) {
                        itemCategoryAddingMessage.value = "Food Item Category adding successfully"
                    } else {
                        itemCategoryAddingMessage.value = "Food Item Category adding Fail,Try again"
                    }

                }

                override fun onError(e: Throwable) {
                    showProgressbar.value = false

                }

                override fun onComplete() {
                    showProgressbar.value = false
                }
            })

    }


    fun setSelectedfoodItemCategoryID(id: Int) {
        selectedfoodItemCategoryID.value = id
    }

  /*  fun setSelectedFoodImageBitmap(bitmap: Bitmap?) {
        foodImage.value = bitmap
    }
*/

    fun setSelectedFoodImageBitmap(bitmap: String) {
        foodImage.value = bitmap
    }


    fun onItemSelectedFoodItemType(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.getItemAtPosition(position).toString()) {
            "Plate" -> foodItemType.value = "PL"
            "Extra" -> foodItemType.value = "EX"
            "Portion" -> foodItemType.value = "PO"
        }

    }

    fun onItemSelectedFoodItemSize(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.getItemAtPosition(position).toString()) {
            "Normal" -> foodItemSize.value = "N"
            "Small" -> foodItemSize.value = "S"
            "Large" -> foodItemSize.value = "L"
        }

    }


    fun onAddFoodItmes() {
        when {
            selectedfoodItemCategoryID.value == null -> foodAddingResponseMessage.value =
                "Please select Food Item Category"
            editTextFoodItem.value.isNullOrEmpty() -> foodAddingResponseMessage.value = "Please select Food Item "
            editTextFoodTitle.get().isNullOrEmpty() -> foodAddingResponseMessage.value = "Please fill Food Title"
            editTextFoodDescription.get().isNullOrEmpty() -> foodAddingResponseMessage.value =
                "Please fill Food Description"
            foodImage.value == null -> foodAddingResponseMessage.value = "Please select Food Image"
            foodItemType.value == null -> foodAddingResponseMessage.value = "Please select Food Type"
            foodItemSize.value == null -> foodAddingResponseMessage.value = "Please select Food Size"
            editTextFoodPrice.get().isNullOrEmpty() -> foodAddingResponseMessage.value = "Please fill Food Description"
            else -> addFoodItems()
        }

    }

    fun claerFoodItems() {

        foodItemArrayFinal.value!!.clear()


    }


    fun addFoodItems() {


        var food = FoodItem()
        food.foodItemtitle = editTextFoodTitle.get().toString()
        food.foodItemdescription = editTextFoodDescription.get().toString()
        food.foodItemimage = foodImage.value!!
        food.foodItemType = foodItemType.value.toString()
        food.foodItemSize = foodItemSize.value.toString()
        food.foodItemPrice = editTextFoodPrice.get()!!.toDouble()




        foodItemArray.add(food)
        foodItemArrayFinal.value = foodItemArray



        textFoodDescription.set(false)
        textFoodTitle.set(false)
       // imageUpload.set(false)

        foodItemEnable.set(false)
        foodItemCategoryEnable.set(false)


    }


    fun onAddFoodComplete() {
        val activeNetwork: NetworkInfo? = cm!!.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        when {
            !isConnected -> addFoodCompleteMsg.value = "No internet connection !"
            selectedfoodItemCategoryID.value == null -> addFoodCompleteMsg.value = "Please select Food Item Category"
            editTextFoodItem.value.isNullOrEmpty() -> addFoodCompleteMsg.value = "Please select Food Item "
            foodItemArrayFinal.value.isNullOrEmpty() -> addFoodCompleteMsg.value = "Please add food items"
            else -> sendFoodToServer()
        }

    }


    fun sendFoodToServer() {

        completeButtonEnable.set(false)
        val jsonObject = JsonObject()

        jsonObject.addProperty("OutletID", outletID.value)
        jsonObject.addProperty("OutletMenuTitleID", outletMenuTitleID.value)
        jsonObject.addProperty("FoodItemCategoryID", selectedfoodItemCategoryID.value)
        jsonObject.addProperty("FoodItem", editTextFoodItem.value)


        val cartJsonArr = JsonArray()

        for (item in foodItemArrayFinal.value!!) {
            val ob = JsonObject()

            ob.addProperty("Name", item.foodItemtitle)
            ob.addProperty("Description", item.foodItemdescription)
            ob.addProperty("FoodItemTypeCode", item.foodItemType)
            ob.addProperty("FoodItemSizeCode", item.foodItemSize)
            ob.addProperty("Price", item.foodItemPrice)
            ob.addProperty("ImageBase64", item.foodItemimage)
            // ob.addProperty("ImageBase64", "")

            cartJsonArr.add(ob)

        }

        jsonObject.add("OutletFoodItems", cartJsonArr)


        println("cccc :" + jsonObject)


        apiInterface!!.saveOutletMenuItems(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(log: Int) {
                    if (log == 1) {
                        addFoodCompleteMsg.value = "Menu adding successfully"
                    } else {
                        addFoodCompleteMsg.value = "Menu adding Fail,Try again"
                    }
                }

                override fun onError(e: Throwable) {
                    showProgressbar.value = false
                    completeButtonEnable.set(true)
                }

                override fun onComplete() {

                    completeButtonEnable.set(true)
                    showProgressbar.value = false


                    textFoodDescription.set(true)
                    textFoodTitle.set(true)
                    imageUpload.set(true)

                    foodItemEnable.set(true)
                    foodItemCategoryEnable.set(true)

                    editTextFoodTitle.set("")
                    editTextFoodDescription.set("")
                    editTextFoodPrice.set("")
                }
            })


    }


    fun BitMapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }
}