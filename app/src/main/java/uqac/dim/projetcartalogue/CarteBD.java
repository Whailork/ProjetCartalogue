package uqac.dim.projetcartalogue;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {CarteModel.class}, version = 2)
@TypeConverters({BitmapConverter.class, HashtableConverter.class})
public abstract class CarteBD extends RoomDatabase {
    private static CarteBD INSTANCE;
    public abstract CarteDao carteDao();

    public static CarteBD getDataBase(Context context){
        if (INSTANCE == null){
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(),
                                    CarteBD.class, "cartedatabase")
                            .fallbackToDestructiveMigration() // To handle database version changes
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }
}
