package emarge.project.smartfoodmenu.ui.activity


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import emarge.project.smartfoodmenu.R
import emarge.project.smartfoodmenu.databinding.ActivityMenuTwoBinding
import emarge.project.smartfoodmenu.databinding.DialogFoodItemCategoryBinding
import emarge.project.smartfoodmenu.model.FoodItem
import emarge.project.smartfoodmenu.model.FoodItemsCategorys
import emarge.project.smartfoodmenu.ui.adaptor.FoodItemCategoryAdapter
import emarge.project.smartfoodmenu.ui.adaptor.FoodItemsAdapter
import emarge.project.smartfoodmenu.viewModels.MenuTwoViewModel
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_menu_two.*
import kotlinx.android.synthetic.main.activity_menu_two.imageView3
import kotlinx.android.synthetic.main.activity_menu_two.progressBar
import java.io.File
import java.io.FileNotFoundException

class ActivityMenuTwo : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, FoodItemsAdapter.ClickListener {


    val IMAGE_PERMISSION_REQUEST = 702
    val PICK_IMAGE_REQUEST = 701


    lateinit var bitmap: Bitmap
    lateinit var filePath: Uri


    private lateinit var binding: ActivityMenuTwoBinding

    lateinit var foodItemsAdapter: FoodItemsAdapter

    lateinit var dialogNewFoodItemCat: Dialog


    lateinit var foodItemCategoryAdapter: FoodItemCategoryAdapter

    var outletMenuTitleID: Int = 0
    var outletID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_two)

        binding = DataBindingUtil.setContentView<ActivityMenuTwoBinding>(this, R.layout.activity_menu_two)
        binding.menutwo = ViewModelProviders.of(this).get(MenuTwoViewModel::class.java)
        binding.menutwo!!.setViewListener(applicationContext)



        setSupportActionBar(toolbar_menu_two)


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout_menu_two, toolbar_menu_two, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout_menu_two.addDrawerListener(toggle)
        toggle.syncState()
        nav_view_menu_two.setNavigationItemSelectedListener(this)



        outletMenuTitleID = intent.extras.getInt("OutletMenuTitleID", 0)
        outletID = intent.extras.getInt("SelectedOutletID", 0)

        binding.menutwo!!.getOutletMenuTitleID(outletMenuTitleID)
        binding.menutwo!!.getOutleID(outletID)



        binding.menutwo!!.showProgressbar.observe(this, Observer<Boolean> {
            it?.let { result ->
                if(result){
                    progressBar.visibility= View.VISIBLE
                    button2.isEnabled= false
                    button3.isEnabled= false
                }else{
                    progressBar.visibility= View.GONE
                    button2.isEnabled= true
                    button3.isEnabled= true

                }
            }
        })




        val layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerview_fooditems!!.layoutManager = layoutManager
        recyclerview_fooditems!!.itemAnimator = DefaultItemAnimator() as RecyclerView.ItemAnimator?
        recyclerview_fooditems!!.isNestedScrollingEnabled = false


        binding.menutwo!!.foodAddingResponseMessage.observe(this, Observer<String> {
            it?.let { result ->
                Toast.makeText(this, result, Toast.LENGTH_LONG).show()
            }
        })

        binding.menutwo!!.itemCategoryAddingMessage.observe(this, Observer<String> {
            it?.let { result ->
                Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                if (result == "Food Item Category adding successfully") {
                    dialogNewFoodItemCat.dismiss()
                }

            }
        })

        binding.menutwo!!.foodItemArrayFinal.observe(this, Observer<ArrayList<FoodItem>> {
            it?.let { result ->

                foodItemsAdapter = FoodItemsAdapter(result, this, this)
                recyclerview_fooditems!!.adapter = foodItemsAdapter
            }
        })

        binding.menutwo!!.addFoodCompleteMsg.observe(this, Observer<String> {
            it?.let { result ->
                if(result == "Menu adding successfully"){


                    binding.menutwo!!.claerFoodItems()
                    foodItemsAdapter.notifyDataSetChanged()

                }else{
                }
                Toast.makeText(this, result, Toast.LENGTH_LONG).show()
            }
        })


        binding.menutwo!!.getfoodItemCategoryMsg.observe(this, Observer<String> {
            it?.let { result ->
                Toast.makeText(this, result, Toast.LENGTH_LONG).show()
            }
        })


        binding.menutwo!!.foodItemCategoryList.observe(this, Observer<ArrayList<FoodItemsCategorys>> {
            it?.let { result ->
                foodItemCategoryAdapter = FoodItemCategoryAdapter(this, R.layout.activity_outlet, R.id.lbl_name, result)
                autoCompleteTextView_food_itemcat.setAdapter(foodItemCategoryAdapter)
            }
        })



        autoCompleteTextView_food_itemcat.onItemClickListener = AdapterView.OnItemClickListener{ parent, _, position, _ ->
            var foodItemsCategorys : FoodItemsCategorys = parent.getItemAtPosition(position) as FoodItemsCategorys
            binding.menutwo!!.setSelectedfoodItemCategoryID(foodItemsCategorys.id)
        }



    }


    fun onClickImageUpload(view: View) {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequestImage()
        } else {
            chooseFile()
        }

    }

    private fun chooseFile() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_REQUEST)
    }

    private fun makeRequestImage() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), IMAGE_PERMISSION_REQUEST)
    }


    fun onClickFoodItemCatAdd(view: View) {
        dialogNewFoodItemCat = Dialog(this)
        dialogNewFoodItemCat.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogNewFoodItemCat.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialogNewFoodItemCat.setCancelable(true)

        var bindingDialog: DialogFoodItemCategoryBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_food_item_category, null, false)
        dialogNewFoodItemCat.setContentView(bindingDialog.root)
        bindingDialog.fooditemcat = ViewModelProviders.of(this).get(MenuTwoViewModel::class.java)





        dialogNewFoodItemCat.show()

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            IMAGE_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseFile()
                } else {
                    Toast.makeText(this@ActivityMenuTwo, "Oops! Permission Denied!!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGE_REQUEST -> when (resultCode) {
                Activity.RESULT_OK -> try {
                    var filePath: String = ""
                    try {
                        filePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                addImagesUpKitKat(data!!.data)
                            } else {
                                addImagesUpKitKat(data!!.data)
                            }
                        } else {
                            addImages(data!!.data)
                        }


                    if (filePath == "") {
                        Toast.makeText(this, "Please select image again", Toast.LENGTH_LONG).show()
                    } else {

                        val file = File(filePath)
                        binding.menutwo!!.setSelectedFoodImageBitmap(file.name)

                    }

                    Glide.with(this)
                        .asBitmap()
                        .load(data!!.data)
                        .into(imageView3)


                    } catch (ex: java.lang.Exception) {
                        Toast.makeText(this, "Please select image again", Toast.LENGTH_LONG).show()
                    }
                  /*  var path : String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        addImagesUpKitKat(data)
                    }else{
                        addImages(data)
                    }


                    if (path == "") {
                        Toast.makeText(this, "Please select image from gallery ", Toast.LENGTH_LONG).show()
                    } else {
                        val file = File(path)
                        binding.menutwo!!.setSelectedFoodImageBitmap(file.name)

                        try {
                            bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(data!!.data))
                        } catch (e: FileNotFoundException) {
                            Toast.makeText(this, "Image not selected properly, Please try again", Toast.LENGTH_SHORT).show()
                        }
                        imageView3!!.setImageBitmap(bitmap)

                    }

*/
                    /*try {
                        bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(data!!.data))
                    } catch (e: FileNotFoundException) {
                        Toast.makeText(this, "Image not selected properly, Please try again", Toast.LENGTH_SHORT).show()
                    }
                    imageView3!!.setImageBitmap(bitmap)


                    var filePath =  getRealPathFromURI(this,data!!.data)

                    if (filePath == "") {
                        Toast.makeText(this, "Please select image from gallery ", Toast.LENGTH_LONG).show()
                    } else {
                        val file = File(filePath)
                        binding.menutwo!!.setSelectedFoodImageBitmap(filePath)
                    }*/

                } catch (e: Exception) {
                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setMessage("Image not selected properly, Please try again$e")
                    alertDialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                    alertDialogBuilder.show()
                }
                Activity.RESULT_CANCELED -> Toast.makeText(this, "Image not selected properly, Please try again", Toast.LENGTH_SHORT).show()
                else -> {
                }
            }


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


    override fun onCancelClick(v: View?, position: Int) {
        binding.menutwo!!.foodItemArrayFinal.value?.removeAt(position)
        foodItemsAdapter.notifyDataSetChanged()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_menu -> {
                // Handle the camera action
            }
            R.id.nav_outlet -> {
                val intent = Intent(this@ActivityMenuTwo, ActivityOutlet::class.java)
                startActivity(intent)
                finish()
            }
            R.id.nav_sales -> {
                val intenttop = Intent(this, ActivitySale::class.java)
                startActivity(intenttop)
                super.onBackPressed()
            }

        }

        drawer_layout_menu_two.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onBackPressed() {
        if (drawer_layout_menu_two.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_menu_two.closeDrawer(GravityCompat.START)
        } else {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Exit!")
            alertDialogBuilder.setMessage("Do you really want to exit ?")
            alertDialogBuilder.setPositiveButton("YES"
            ) { _, _ ->

                 val intenttop = Intent(this, ActivityMenu::class.java)
                 startActivity(intenttop)
                 super.onBackPressed()


            }
            alertDialogBuilder.setNegativeButton("NO", DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
            alertDialogBuilder.show()
        }
    }
}
