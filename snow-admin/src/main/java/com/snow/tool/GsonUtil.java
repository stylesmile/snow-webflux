package com.snow.tool;

import com.google.gson.Gson;


/**
 * 基于Gson封装的jsonUtil
 *
 * @author chenye
 * @date 2020-0828
 * implementation 'com.google.code.gson:gson:2.8.6'
 */
public class GsonUtil {
    private static Gson gson = null;

    static {
        if (gson == null) {
            gson = new Gson();
        }
    }

    private GsonUtil() {
    }

    /**
     * 对象转成json
     */
    public static String objectToJson(Object object) {
        String json = null;
        if (gson != null) {
            json = gson.toJson(object);
        }
        return json;
    }

    /**
     * Json转成对象
     */
    public static <T> T gsonToBean(String json, Class<T> cls) {
        T t = null;
        if (gson != null) {
            t = gson.fromJson(json, cls);
        }
        return t;
    }


}
