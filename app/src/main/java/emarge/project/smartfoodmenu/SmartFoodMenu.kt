package emarge.project.smartfoodmenu

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import emarge.project.caloriecaffe.db.RealmMigrations
import io.realm.Realm
import io.realm.RealmConfiguration

class SmartFoodMenu : Application() {




    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        val configuration = RealmConfiguration.Builder().name("smartfoodmenu.realm")
            .schemaVersion(0)
            .migration(RealmMigrations())
            .deleteRealmIfMigrationNeeded()
            .build()




        Realm.setDefaultConfiguration(configuration)
        Realm.getInstance(configuration)

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onTerminate() {
        super.onTerminate()

    }

}