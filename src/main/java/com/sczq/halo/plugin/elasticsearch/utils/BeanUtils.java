package com.sczq.halo.plugin.elasticsearch.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

public class BeanUtils {

    public static <T> void copyProperties(Object bean, Map<String, T> properties) {
        if (bean == null || properties == null) {
            return;
        }

        try {
            for (Map.Entry<String, T> entry : properties.entrySet()) {
                String propertyName = entry.getKey();
                Object propertyValue = entry.getValue();

                PropertyDescriptor descriptor =
                    new PropertyDescriptor(propertyName, bean.getClass());
                Method writeMethod = descriptor.getWriteMethod();
                if (writeMethod != null) {
                    writeMethod.invoke(bean, propertyValue);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error copying properties to bean", e);
        }
    }
}