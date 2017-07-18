package de.bht.beuthbot.utils;

import com.google.gson.Gson;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 12.07.17
 */
public class GsonUtils {
    private static Gson gson = new Gson();

    static {
        // extra init logic goes here
    }

    public static <T> T fromJson(final String json, final Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }


    public static String toJson(final Object object) {
        if (object == null) {
            return "";
        }
        return gson.toJson(object);
    }
}
