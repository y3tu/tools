package com.y3tu.tools.lowcode.common.util;


import com.y3tu.tools.kit.base.ObjectUtil;
import com.y3tu.tools.kit.collection.CollectionUtil;
import com.y3tu.tools.kit.reflect.ReflectUtil;
import com.y3tu.tools.lowcode.common.entity.domain.DictData;
import com.y3tu.tools.lowcode.common.service.DictService;
import com.y3tu.tools.lowcode.exception.LowCodeException;
import com.y3tu.tools.web.util.SpringContextUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典工具类
 *
 * @author y3tu
 */
public class DictUtil {

    /**
     * 根据字典编码获取字典数据
     *
     * @param code        字典编码
     * @param targetValue 筛选值
     * @return
     */
    public static List<DictData> getDictData(String code, Object... targetValue) {
        DictService dictService = (DictService) SpringContextUtil.getBean(DictService.class);
        return dictService.getDictData(code, targetValue);
    }

    /**
     * 把数据中的属性值替换成字典的name
     *
     * @param dest          目标对象，可以List、数组或普通对象
     * @param code          字典编码
     * @param nameProperty  需要赋值的类属性
     * @param valueProperty value值对应的类属性
     * @return
     */
    public static boolean fillNameByValue(Object dest, String code, String nameProperty, String valueProperty) {
        if (ObjectUtil.isEmpty(dest)) {
            return false;
        }
        List<Object> list = new ArrayList();
        if (dest.getClass().isArray()) {
            //目标对象是数组
            list = Arrays.asList((Object[]) dest);
        } else if (dest instanceof List) {
            //目标对象是集合
            list = (List<Object>) dest;
        } else {
            list.add(dest);
        }
        if (CollectionUtil.isNotEmpty(list)) {
            for (Object obj : list) {
                try {
                    Object value = ReflectUtil.getFieldValue(obj, valueProperty);
                    List<DictData> dictDataList = getDictData(code, value);
                    if (dictDataList.size() > 0) {
                        Map<String, DictData> dictDataMap = listToMap(dictDataList);
                        DictData dictData = dictDataMap.get(value.toString());
                        ReflectUtil.setFieldValue(obj, nameProperty, dictData.getName());
                    }
                } catch (Exception e) {
                    throw new LowCodeException("根据字典填充实体对象失败", e);
                }
            }
        }
        return false;
    }

    private static Map<String, DictData> listToMap(List<DictData> dictDataList) {
        Map<String, DictData> map = new HashMap<>();
        for (DictData item : dictDataList) {
            map.put(item.getValue(), item);
        }
        return map;
    }
}
