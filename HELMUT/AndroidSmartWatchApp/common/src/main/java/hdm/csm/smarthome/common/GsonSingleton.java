package hdm.csm.smarthome.common;

import android.os.Bundle;
import com.google.gson.*;

import java.lang.reflect.Type;

public class GsonSingleton {
    private static Gson gson = null;

    public static Gson getGson() {
        if (gson == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Bundle.class, new JsonSerializer<Bundle>() {
                        @Override
                        public JsonElement serialize(Bundle src, Type typeOfSrc, JsonSerializationContext context) {
                            JsonObject jsonObject = new JsonObject();

                            for (String key : src.keySet()) {
                                jsonObject.add(key, context.serialize(src.get(key)));
                            }

                            return jsonObject;
                        }
                    })
                    .create();
        }

        return gson;
    }

}
