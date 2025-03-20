package com.aegis.companion.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public final class BeanCopyUtils {
    private BeanCopyUtils() {
    }

    /**
     * 复制源对象中非空属性到目标对象，并排除指定字段
     */
    public static void copyNonNullProperties(Object source, Object target, String... ignoreProperties) {
        String[] nullPropertyNames = getNullPropertyNames(source);
        String[] ignore = new String[ignoreProperties.length + nullPropertyNames.length];
        System.arraycopy(ignoreProperties, 0, ignore, 0, ignoreProperties.length);
        System.arraycopy(nullPropertyNames, 0, ignore, ignoreProperties.length, nullPropertyNames.length);
        BeanUtils.copyProperties(source, target, ignore);
    }

    /**
     * 获取对象中所有值为 null 的属性名
     */
    public static String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        return emptyNames.toArray(new String[0]);
    }
}
