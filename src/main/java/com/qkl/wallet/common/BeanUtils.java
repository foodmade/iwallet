package com.qkl.wallet.common;

import com.qkl.wallet.common.exception.BeanUtilsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class BeanUtils {

    private BeanUtils(){}

    /**
     * Transforms from the source object. (copy same properties only)
     *
     * @param source      source data
     * @param targetClass target class must not be null
     * @param <T>         target class type
     * @return instance with specified type copying from source data; or null if source data is null
     * @throws BeanUtilsException if newing target instance failed or copying failed
     */
    @Nullable
    public static <T> T transformFrom(@Nullable Object source, @NonNull Class<T> targetClass) throws BeanUtilsException {
        Assert.notNull(targetClass, "Target class must not be null");

        if (source == null) {
            return null;
        }

        // Init the instance
        try {
            // New instance for the target class
            T targetInstance = targetClass.newInstance();
            // Copy properties
            org.springframework.beans.BeanUtils.copyProperties(source, targetInstance, getNullPropertyNames(source));
            // Return the target instance
            return targetInstance;
        } catch (Exception e) {
            throw new BeanUtilsException("Failed to new " + targetClass.getName() + " instance or copy properties", e);
        }
    }

    /**
     * Update properties (non null).
     *
     * @param source source data must not be null
     * @param target target data must not be null
     * @throws BeanUtilsException if copying failed
     */
    public static void updateProperties(@NonNull Object source, @NonNull Object target) throws BeanUtilsException {
        Assert.notNull(source, "source object must not be null");
        Assert.notNull(target, "target object must not be null");

        // Set non null properties from source properties to target properties
        try {
            org.springframework.beans.BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
        } catch (BeansException e) {
            throw new BeanUtilsException("Failed to copy properties", e);
        }
    }

    /**
     * Gets null names array of property.
     *
     * @param source object data must not be null
     * @return null name array of property
     */
    @NonNull
    private static String[] getNullPropertyNames(@NonNull Object source) {
        return getNullPropertyNameSet(source).toArray(new String[0]);
    }

    /**
     * Gets null names set of property.
     *
     * @param source object data must not be null
     * @return null name set of property
     */
    @NonNull
    private static Set<String> getNullPropertyNameSet(@NonNull Object source) {

        Assert.notNull(source, "source object must not be null");
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(source);
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String propertyName = propertyDescriptor.getName();
            Object propertyValue = beanWrapper.getPropertyValue(propertyName);

            // if property value is equal to null, add it to empty name set
            if (propertyValue == null) {
                emptyNames.add(propertyName);
            }
        }

        return emptyNames;
    }

    public static <T> List<T> coventObject(Collection<?> collection, Class<T> clazz){
        return collection.stream().map(oldObject -> {
            T instance;
            try{
                instance = clazz.newInstance();
                org.springframework.beans.BeanUtils.copyProperties(oldObject, instance);
                return instance;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }).filter(Objects::nonNull)
          .collect(Collectors.toList());
    };

    /**
     * Bean trans to map
     * @param bean original bean.
     * @return Map
     */
    public static Map convertBean(Object bean) throws Exception {
        Class type = bean.getClass();
        Map returnMap = new HashMap();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : propertyDescriptors) {
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean);
                if (result != null) {
                    returnMap.put(propertyName, result);
                } else {
                    returnMap.put(propertyName, "");
                }
            }
        }
        return returnMap;
    }
}
