package emarge.project.smartfoodmenu.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.aminography.choosephotohelper.ChoosePhotoHelper
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import emarge.project.smartfoodmenu.R
import emarge.project.smartfoodmenu.databinding.ActivityOutletBinding
import emarge.project.smartfoodmenu.model.City
import emarge.project.smartfoodmenu.model.District
import emarge.project.smartfoodmenu.model.OutletImages
import emarge.project.smartfoodmenu.model.UserModel
import emarge.project.smartfoodmenu.ui.adaptor.AddedOutletImagesAdaptor
import emarge.project.smartfoodmenu.ui.adaptor.CityAdapter
import emarge.project.smartfoodmenu.viewModels.OutletViewModel
import kotlinx.android.synthetic.main.activity_outlet.*
import emarge.project.smartfoodmenu.ui.adaptor.DistrictAdapter
import emarge.project.smartfoodmenu.ui.adaptor.RepAdapter
import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception
import java.util.ArrayList


class ActivityOutlet : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnMapReadyCallback {


    private lateinit var binding: ActivityOutletBinding
    private lateinit var adapterDistrict: DistrictAdapter
    lateinit var adapterCity: CityAdapter
    lateinit var repAdapter: RepAdapter

    private lateinit var mMap: GoogleMap


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    lateinit var locationRequest: LocationRequest

    lateinit var bitmap: Bitmap
    lateinit var filePath: Uri

    val LOCATION_REQUEST = 900
    private val REQUEST_CHECK_SETTINGS = 2
    val PICK_IMAGE_REQUEST = 901
    val IMAGE_PERMISSION_REQUEST = 902


    val PICK_IMAGE_REQUEST_LIB_CODE = 101
    val TAKE_IMAGE_REQUEST_LIB_CODE = 102


    lateinit var addedOutletImagesAdaptor: AddedOutletImagesAdaptor

    var outletImageList = ArrayList<OutletImages>()

    private var currentLocation: LatLng? = null
    private var userChangedLocation: LatLng? = null



    private lateinit var choosePhotoHelper: ChoosePhotoHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView<ActivityOutletBinding>(this, R.layout.activity_outlet)
        binding.outlet = ViewModelProviders.of(this).get(OutletViewModel::class.java)
        binding.outlet!!.setViewListener(applicationContext)

        setSupportActionBar(toolbar_outlet)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout_outlet,
            toolbar_outlet,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout_outlet.addDrawerListener(toggle)
        toggle.syncState()
        nav_view_outlet.setNavigationItemSelectedListener(this)



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                makeRequest()
            } else {
                createLocationRequest()
            }
        } else {
            createLocationRequest()
        }


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                onLocationChanged(locationResult!!.lastLocation)
            }
        }



        binding.outlet!!.showProgressbar.observe(this, Observer<Boolean> {
            it?.let { result ->
                if (result) {
                    progressBar.visibility = View.VISIBLE
                    imageView4.isEnabled = false
                } else {
                    progressBar.visibility = View.GONE
                    imageView4.isEnabled = true

                }
            }
        })

        binding.outlet!!.cityList.observe(this, Observer<List<City>> {
            it?.let { result ->
                adapterCity = CityAdapter(this, R.layout.activity_outlet, R.id.lbl_name, result)
                autoCompleteTextView_city.setAdapter(adapterCity)
            }
        })

        autoCompleteTextView_city.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                var city: City = parent.getItemAtPosition(position) as City
                binding.outlet!!.setSelectedCityID(city.id)
            }


        binding.outlet!!.districtList.observe(this, Observer<List<District>> {
            it?.let { result ->
                adapterDistrict =
                    DistrictAdapter(this, R.layout.activity_outlet, R.id.lbl_name, result)
                autoCompleteTextView_District.setAdapter(adapterDistrict)
            }
        })



        autoCompleteTextView_District.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                var district: District = parent.getItemAtPosition(position) as District
                binding.outlet!!.setSelectedDistrictID(district.id)
            }



        binding.outlet!!.repList.observe(this, Observer<List<UserModel>> {
            it?.let { result ->
                repAdapter = RepAdapter(this, R.layout.activity_outlet, R.id.lbl_name, result)
                autoCompleteTextView_assignrep.setAdapter(repAdapter)
            }
        })

        autoCompleteTextView_assignrep.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                var rep: UserModel = parent.getItemAtPosition(position) as UserModel
                binding.outlet!!.setSelectedRepID(rep.id)
            }




        binding.outlet!!.outletAddingError.observe(this, Observer<String> {
            it?.let { result ->
                if (result == "outlet adding successfully") {
                    val alertDialogBuilder = android.app.AlertDialog.Builder(this)
                    alertDialogBuilder.setTitle("Outlet")
                    alertDialogBuilder.setMessage("outlet adding successfully")
                    alertDialogBuilder.setPositiveButton(
                        "Add Menu"
                    ) { _, _ ->

                        val intent = Intent(this@ActivityOutlet, ActivityMenu::class.java)
                        startActivity(intent)
                        finish()

                    }
                    alertDialogBuilder.setNegativeButton(
                        "Later",
                        DialogInterface.OnClickListener { _, i ->

                            editText.setText("")
                            editText_des.setText("")
                            editText_add.setText("")
                            editText_email.setText("")
                            editText_website.setText("")
                            editText_phone.setText("")
                            editText_Pickup.setText("")
                            editText_serviceCharge.setText("")
                            autoCompleteTextView_city.setText("")
                            autoCompleteTextView_District.setText("")
                            autoCompleteTextView_city.setAdapter(adapterCity)
                            autoCompleteTextView_District.setAdapter(adapterDistrict)



                            imageView3!!.setImageBitmap(null)


                        })
                    alertDialogBuilder.show()

                } else {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                }


            }
        })


        choosePhotoHelper = ChoosePhotoHelper.with(this)
            .asFilePath()
            .build(ChoosePhotoCallback {
                Glide.with(this)
                    .load(it)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_photo_camera_black_24dp))
                    .into(imageView3)
            })


    }


    private fun onLocationChanged(location: Location) {
        currentLocation = LatLng(location.latitude, location.longitude)



        if (mapView_unapproved != null) {
            mapView_unapproved.onCreate(null)
            mapView_unapproved.onResume()
            mapView_unapproved.getMapAsync(this)
        }


    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap



        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.setAllGesturesEnabled(true)
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true


        if (currentLocation == null) {
            startLocationUpdates()
        } else {
            mMap.addMarker(MarkerOptions().position(currentLocation!!).title("").draggable(true))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17f))
        }


        mMap.setOnCameraMoveListener {
            mMap.clear()
            userChangedLocation = mMap.cameraPosition.target

            mMap.addMarker(MarkerOptions().position(userChangedLocation!!).title("").draggable(true))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userChangedLocation, 17f))
        }

        mMap.setOnCameraIdleListener {
            if (userChangedLocation == null) {
            } else {

            }
        }


        /* mMap = p0!!

         mMap.uiSettings.isMyLocationButtonEnabled = false
         mMap.uiSettings.setAllGesturesEnabled(true)
         mMap.uiSettings.isMapToolbarEnabled = true
         mMap.uiSettings.isMyLocationButtonEnabled = true


         if (currentLocation == null) {
             startLocationUpdates()
         } else {
             mMap.addMarker(MarkerOptions().position(currentLocation!!).title("").draggable(true))
             mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17f))
         }


         mMap.setOnCameraMoveListener {
             mMap.clear()
             userChangedLocation = mMap.cameraPosition.target

             mMap.addMarker(MarkerOptions().position(userChangedLocation!!).title("").draggable(true))
             mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userChangedLocation, 17f))
         }

         mMap.setOnCameraIdleListener {
             if (userChangedLocation == null) {
             } else {

             }
         }*/


        /*  mMap = p0

          val sydney = LatLng(-34.0, 151.0)
          mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
          mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
  */


    }


    fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }


        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        builder.setAlwaysShow(true)
        task.addOnSuccessListener(this) {
            startLocationUpdates()
        }
        task.addOnFailureListener(this) { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this@ActivityOutlet, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {

                }

            }
        }

    }


    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_REQUEST
        )
    }

    private fun makeRequestImage() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            IMAGE_PERMISSION_REQUEST
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        println("ccccccccc : "+requestCode)

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                startLocationUpdates()
            }else{
                Toast.makeText(this, "Location Features no longer available", Toast.LENGTH_SHORT).show()
            }
        }else{

           // if (resultCode == Activity.RESULT_OK) {
                choosePhotoHelper.onActivityResult(requestCode, resultCode, data)
                addOutletImages(data!!.data)
           /* }else{
                Toast.makeText(
                    this,
                    "Image not selected properly, Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }*/
        }




   /*     when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> startLocationUpdates()
                Activity.RESULT_CANCELED -> Toast.makeText(
                    this,
                    "Location Features no longer available",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                }
            }
            PICK_IMAGE_REQUEST_LIB_CODE -> when (resultCode) {
                Activity.RESULT_OK -> try {
                    addOutletImages(data!!.data)


                    choosePhotoHelper.onActivityResult(requestCode, resultCode, data)

                } catch (e: Exception) {
                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setMessage("Image not selected properly, Please try again$e")
                    alertDialogBuilder.setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                    alertDialogBuilder.show()
                }
                Activity.RESULT_CANCELED -> Toast.makeText(
                    this,
                    "Image not selected properly, Please try again",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                }
            }

            TAKE_IMAGE_REQUEST_LIB_CODE -> when (resultCode) {
                Activity.RESULT_OK -> try {
                    //addOutletImages(data!!.data)


                    choosePhotoHelper.onActivityResult(requestCode, resultCode, data)

                } catch (e: Exception) {
                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setMessage("Image not selected properly, Please try again$e")
                    alertDialogBuilder.setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                    alertDialogBuilder.show()
                }
                Activity.RESULT_CANCELED -> Toast.makeText(
                    this,
                    "Image not selected properly, Please try again",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                }
            }

        }*/





       /* try {

            when (requestCode) {
                REQUEST_CHECK_SETTINGS -> when (resultCode) {
                    Activity.RESULT_OK -> startLocationUpdates()
                    Activity.RESULT_CANCELED -> Toast.makeText(
                        this,
                        "Location Features no longer available",
                        Toast.LENGTH_SHORT
                    ).show()
                    else -> {
                    }
                }
                PICK_IMAGE_REQUEST -> when (resultCode) {
                    Activity.RESULT_OK -> try {
                        addOutletImages(data!!.data)


                        Glide.with(this)
                            .asBitmap()
                            .load(data!!.data)
                            .into(imageView3)

                    } catch (e: Exception) {
                        val alertDialogBuilder = AlertDialog.Builder(this)
                        alertDialogBuilder.setMessage("Image not selected properly, Please try again$e")
                        alertDialogBuilder.setPositiveButton(
                            "OK",
                            DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                        alertDialogBuilder.show()
                    }
                    Activity.RESULT_CANCELED -> Toast.makeText(
                        this,
                        "Image not selected properly, Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    else -> {
                    }
                }

            }
        } catch (ex: java.lang.Exception) {
            Toast.makeText(
                this,
                "Try again",
                Toast.LENGTH_SHORT
            ).show()
        }*/
    }


    private fun addOutletImages(imagURI: Uri) {
        var outletImage = OutletImages()


        var filePath: String = ""

        try {
            filePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    addImagesUpKitKat(imagURI)
                } else {
                    addImagesUpKitKat(imagURI)
                }
            } else {
                addImages(imagURI)
            }
        } catch (ex: Exception) {

        }

        if (filePath == "") {
            Toast.makeText(this, "Please select image again", Toast.LENGTH_LONG).show()
        } else {

            val file = File(filePath)

            outletImage.imageUrl = imagURI
            outletImage.name = file.name

            outletImageList.add(outletImage)


            addedOutletImagesAdaptor = AddedOutletImagesAdaptor(outletImageList, this)
            recyclerView_added_outletimages.adapter = addedOutletImagesAdaptor

            addedOutletImagesAdaptor.setOnItemClickListener(object :
                AddedOutletImagesAdaptor.ClickListener {
                override fun onClick(outletImages: OutletImages, aView: View) {

                    val alertDialogBuilder = AlertDialog.Builder(this@ActivityOutlet)
                    alertDialogBuilder.setTitle("Warning!")
                    alertDialogBuilder.setMessage("Do you really want to delete this image?")
                    alertDialogBuilder.setPositiveButton(
                        "YES"
                    ) { _, _ ->
                        outletImageList.remove(outletImages)
                        addedOutletImagesAdaptor.notifyDataSetChanged()

                        imageView3!!.setImageBitmap(null)

                        binding.outlet!!.setSelectedImages(outletImageList)
                    }
                    alertDialogBuilder.setNegativeButton(
                        "NO",
                        DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                    alertDialogBuilder.show()

                }
            })


        }


    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun addImagesUpKitKat(data: Uri): String {
        println("cccccccccccccccc : addImagesUpKitKat")

        var filep: String = ""
        if (data == null) {
            Toast.makeText(this, "Please select image from gallery 2", Toast.LENGTH_LONG).show()
        } else {
            filep = getPath(this, data)
        }
        return filep
    }


    private fun addImages(data: Uri?): String {
        println("cccccccccccccccc : addImages")
        var filep: String = ""
        if (data == null) {
            Toast.makeText(this, "Please select image from gallery 3", Toast.LENGTH_LONG).show()
        } else {
            filep = getRealPathFromURI(this, data)
        }
        return filep

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun getPath(context: Context, uri: Uri): String {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            println("cccccccccccccccc : 1")

            if (isExternalStorageDocument(uri)) {

                val docId = DocumentsContract.getDocumentId(uri)
                val split =
                    docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return (Environment.getExternalStorageDirectory().toString() + "/" + split[1])
                }
            } else if (isDownloadsDocument(uri)) {
                println("cccccccccccccccc : 2")

                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    uri.path.toString()
                } else {
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )

                    getDataColumn(context, contentUri, null, null)
                }


            } else if (isMediaDocument(uri)) {
                println("cccccccccccccccc : 3")
                var docId = DocumentsContract.getDocumentId(uri)
                var split =
                    docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                var type = split[0]
                var contentUri: Uri = Uri.EMPTY
                when (type) {
                    "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf<String>(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }

        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            if (isGooglePhotosUri(uri))
                return uri.lastPathSegment.toString()
            return this.getDataColumn(context, uri, null, null).toString()


        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            println("cccccccccccccccc 6")
            return uri.path.toString()
        }

        return ""
    }


    fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String {

        lateinit var cursor: Cursor
        val column = "_data"
        val projection = arrayOf<String>(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)!!
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor.close()
        }
        return ""
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf<String>(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)!!
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()

            return if (cursor.getString(column_index) == null) {
                ""
            } else {
                cursor.getString(column_index)
            }

        } finally {
            cursor?.close()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        if(requestCode == LOCATION_REQUEST){
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                createLocationRequest()
            } else {
                Toast.makeText(
                    this@ActivityOutlet,
                    "Oops! Permission Denied!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }else{

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhotoHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
            } else {
                Toast.makeText(
                    this@ActivityOutlet,
                    "Oops! Permission Denied!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }




       /* when (requestCode) {
            LOCATION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    createLocationRequest()
                } else {
                    Toast.makeText(
                        this@ActivityOutlet,
                        "Oops! Permission Denied!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
            IMAGE_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseFile()
                } else {
                    Toast.makeText(
                        this@ActivityOutlet,
                        "Oops! Permission Denied!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }*/
    }

    fun onClickImageUpload(view: View) {
      /*  val permission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequestImage()
        } else {
            chooseFile()
        }*/

        choosePhotoHelper.showChooser()

    }


    private fun chooseFile() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_REQUEST)
    }


    override fun onBackPressed() {
        if (drawer_layout_outlet.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_outlet.closeDrawer(GravityCompat.START)
        } else {
            val alertDialogBuilder = android.app.AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Exit!")
            alertDialogBuilder.setMessage("Do you really want to exit ?")
            alertDialogBuilder.setPositiveButton(
                "YES"
            ) { _, _ -> super.onBackPressed() }
            alertDialogBuilder.setNegativeButton(
                "NO",
                DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
            alertDialogBuilder.show()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_menu -> {
                val intenttop = Intent(this, ActivityMenu::class.java)
                startActivity(intenttop)
                this.finish()
            }
            R.id.nav_outlet -> {
            }
            R.id.nav_sales -> {
                val intenttop = Intent(this, ActivitySale::class.java)
                startActivity(intenttop)
                this.finish()
            }
        }
        drawer_layout_outlet.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_main_titalbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_settings -> {
                binding.outlet!!.cityAndDistrictRefrash()
                return true
            }


        }
        return super.onOptionsItemSelected(item)


    }
}
