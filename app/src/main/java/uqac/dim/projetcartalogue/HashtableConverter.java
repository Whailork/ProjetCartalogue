package uqac.dim.projetcartalogue;

import androidx.room.TypeConverter;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.Hashtable;

public class HashtableConverter {
    // Convert Hashtable<String, String> to a JSON string
    @TypeConverter
    public static String fromHashtable(Hashtable<String, String> hashtable) {
        if (hashtable == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(hashtable);
    }

    // Convert JSON string back to Hashtable<String, String>
    @TypeConverter
    public static Hashtable<String, String> toHashtable(String data) {
        if (data == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Hashtable<String, String>>(){}.getType();
        return gson.fromJson(data, type);
    }
}
