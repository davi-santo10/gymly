// In data/DatabaseCallback.kt
import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import br.santo.gymly.data.AppDatabase
import br.santo.gymly.data.Exercise
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader

class PrepopulateCallback(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        CoroutineScope(Dispatchers.IO).launch {
            // Get DAO instance from the database
            val database = AppDatabase.getDatabase(context) // We'll need to create this getDatabase method
            val dao = database.exerciseDao()

            // Read and parse the JSON
            val typeToken = object : TypeToken<List<Exercise>>() {}.type
            val exercises: List<Exercise> = Gson().fromJson(
                InputStreamReader(context.assets.open("predefined_exercises.json")),
                typeToken
            )

            // Insert all exercises
            exercises.forEach { dao.insert(it) }
        }
    }
}