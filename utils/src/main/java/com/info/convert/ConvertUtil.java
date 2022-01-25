package com.info.convert;


import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConvertUtil {
    //entity与dto转换
    public static <T> T convert(Object from, Class<T> clazz) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        T t = clazz.newInstance();
        if (from != null) {
            for (Field tField : clazz.getDeclaredFields()) {
                for (Field fField : from.getClass().getDeclaredFields()) {
                    if (tField.getName().equals(fField.getName())) {
                        tField.setAccessible(true);
                        fField.setAccessible(true);
                        if (!Objects.equals(tField.getGenericType().getTypeName(), fField.getGenericType().getTypeName()) && !Objects.isNull(fField.get(from))) {
                            if (Objects.equals(tField.getGenericType().getTypeName(), "java.util.Date")) {
                                tField.set(t, sdf.parse(String.valueOf(fField.get(from))));
                            } else if (Objects.equals(fField.getGenericType().getTypeName(), "java.util.Date")) {
                                tField.set(t, sdf.format(fField.get(from)));
                            }
                        } else {
                            tField.set(t, fField.get(from));
                        }
                    }
                }
            }
        }
        return t;
    }

    //map转java对象
    public static Object mapToObject(Map<Object, Object> map, Class<?> beanClass) throws Exception {
        String jsonStr = JSONObject.toJSONString(map);
        return JSONObject.parseObject(jsonStr, beanClass);
    }

    //java对象转map
    public static Map<String, Object> objectToMap(Object obj) {
        String jsonStr = JSONObject.toJSONString(obj);
        return JSONObject.parseObject(jsonStr);
    }
}
