package emarge.project.caloriecaffe.db

import io.realm.DynamicRealm
import io.realm.RealmMigration
import io.realm.RealmSchema

/**
 * Created by Himanshu Emerge on 3/2/2018.
 */

class RealmMigrations : RealmMigration {


    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val realmSchema = realm.schema
        for (version in oldVersion until newVersion) {
            if (version == 0L) { // to 1

            }
        }
    }


}
