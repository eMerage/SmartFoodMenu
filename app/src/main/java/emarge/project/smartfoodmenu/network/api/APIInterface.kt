package emarge.project.caloriecaffe.network.api


import com.google.gson.JsonObject
import emarge.project.smartfoodmenu.model.*

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

import java.util.ArrayList

/**
 * Created by kamal on 8/2/18.
 */
interface APIInterface{

    @GET("MealTimeUser/ValidateRep")
    fun validateRep(@Query("password") password: String): Observable<UserModel>

     @GET("MealTimeUser/GetDistrictList")
     fun getDistrict(): Observable<List<District>>

     @GET("MealTimeUser/GetCityList")
     fun getCity(): Observable<List<City>>

     @POST("Outlet/RegisterOutletByRep")
     fun saveOutlet(@Body outletInfo: JsonObject): Observable<Int>


    @GET("outlet/GetOutletsByRep")
    fun getOutlets(@Query("userID") userID: Int):  Observable<ArrayList<Outlet>>


    @POST("Menu/SaveMenuTitle")
    fun saveMenuTitle(@Body menuTitleInfo: JsonObject): Observable<Int>


    @GET("Menu/GetMenuTitles")
    fun getMenuTitles(@Query("menuCategoryID") menuCategoryID: String,
                      @Query("userID") userID: Int,
                      @Query("addressID") addressID: String,
                      @Query("dispatchType") dispatchType: String,
                      @Query("searchType") searchType: String):  Observable<ArrayList<MenuTitles>>


    @POST("Menu/RegisterOutletMenuTitle")
    fun registerOutletMenuTitle(@Body menuTitleInfo: JsonObject): Observable<Int>


    @POST("FoodItem/SaveFoodItemCategory")
    fun saveFoodItemCategory(@Body Info: JsonObject): Observable<Int>


    @POST("FoodItem/SaveFoodItemCategory")
    fun saveFoodItem(@Body Info: JsonObject): Observable<Int>


    @GET("FoodItem/GetFoodItemCategories")
    fun getFoodItemCategories(@Query("IsBase") IsBase: Int):  Observable<ArrayList<FoodItemsCategorys>>


    @POST("Outlet/SaveOutletMenuItems")
    fun saveOutletMenuItems(@Body Info: JsonObject): Observable<Int>



    @GET("outlet/GetOutletStatsByRep")
    fun getOutletStatsByRepNew(@Query("userID") userID: Int,@Query("dateRangeStart") sDat: String,@Query("dateRangeEnd") eDate: String):  Observable<Sales>


    @GET("MealTimeUser/GetAllReps")
    fun getAllReps(): Observable<List<UserModel>>




}
