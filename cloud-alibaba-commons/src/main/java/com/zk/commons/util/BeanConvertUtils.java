package com.zk.commons.util;

import cn.hutool.core.convert.ConvertException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * <b>】要拷贝字段的目标对象不可以是抽象类、接口，或者只有有参构造方法，
 * 否则在targetClass.newInstance()时会报java.lang.InstantiationException</b><br/>
 * <br><b>class:</b> BeanConvertUtil
 * <br><b>date:</b> 2017年11月28日 下午4:55:07
 *
 * @since 1.0
 */
public class BeanConvertUtils {

    public static <T> T convert(Object source, Class<T> targetClass) {
        return convert(source, targetClass, (String[]) null);
    }

    public static <T> void convert(Object source, T target) {
        convert(source, target, (String[]) null);
    }

    public static <T> T convertIgnoreNullProperty(Object source, Class<T> targetClass) {
        return convert(source, targetClass, getNullPropertyNames(source));
    }

    public static <T> void convertIgnoreNullProperty(Object source, T target) {
        convert(source, target, getNullPropertyNames(source));
    }

    public static <T> List<T> convert(List<?> sourceList, Class<T> targetClass) {
        return convert(sourceList, targetClass, false);
    }

    public static <T> List<T> convertIgnoreNullProperty(List<?> sourceList, Class<T> targetClass) {
        return convert(sourceList, targetClass, true);
    }

    private static <T> void convert(Object source, T target, String... ignoreProperties) {
        BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    private static <T> T convert(Object source, Class<T> targetClass, String... ignoreProperties) {
        T target = null;
        try {
            target = targetClass.newInstance();

            BeanUtils.copyProperties(source, target, ignoreProperties);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ConvertException(
                    String.format("[%s].newInstance() happend error.", targetClass.getName()), e);
        }
        return target;
    }

    /**
     * 方法说明：将bean转化为另一种bean实体
     *  
     *
     * @param object
     * @param entityClass
     * @return
     */
    public static <T> T convertBean(Object object, Class<T> entityClass) {
        if (null == object) {
            return null;
        }
        try {
            T t = entityClass.newInstance();
            BeanUtils.copyProperties(object, t);
            return t;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 方法说明：map转化为对象
     *  
     *
     * @param map
     * @param t
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> T mapToObject(Map<String, Object> map, Class<T> t) {
        return mapToEntity(map, t);
    }

    /**
     * 方法说明：对象转化为Map
     *  
     *
     * @param object
     * @return
     */
    public static Map<?, ?> objectToMap(Object object) {
        return convertBean(object, Map.class);
    }

    public static <T> T mapToEntity(Map<String, Object> map, Class<T> entity) {
        T t = null;
        try {
            t = entity.newInstance();
            for (Field field : entity.getDeclaredFields()) {
                if (map.containsKey(field.getName())) {
                    boolean flag = field.isAccessible();
                    field.setAccessible(true);
                    Object object = map.get(field.getName());
                    if (object != null && field.getType().isAssignableFrom(object.getClass())) {
                        field.set(t, object);
                    }
                    field.setAccessible(flag);
                }
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    private static <T> List<T> convert(List<?> sourceList, Class<T> targetClass,
                                       boolean isIgnoreProperties) {
        if (sourceList == null || sourceList.size() == 0) {
            return new ArrayList<T>(0);
        }
        List<T> list = new ArrayList<T>(sourceList.size());
        for (Object obj : sourceList) {
            String[] ignoreProperties = (String[]) null;
            if (isIgnoreProperties) {
                ignoreProperties = getNullPropertyNames(obj);
            }
            list.add(convert(obj, targetClass, ignoreProperties));
        }
        return list;
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

}
