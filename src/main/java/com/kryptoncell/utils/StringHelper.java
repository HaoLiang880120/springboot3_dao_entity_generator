package com.kryptoncell.utils;

import static java.util.Objects.isNull;

public final class StringHelper {

    /**
     * 将下划线命名改为驼峰命名。
     * user_name 变为 userName
     *
     * @param originString 下划线命名的字符串
     * @param camelFirstLetterLowerCase 驼峰命名的首字母是否小写
     */
    public static String toCamelName(String originString, boolean camelFirstLetterLowerCase) {
        var strArr = originString.split("_");

        var sb = new StringBuilder();
        for (var str : strArr) {
            if (isNull(str) || str.isEmpty() || str.isBlank()) {
                continue;
            }

            //将第一个字母大写
            sb.append(str.substring(0, 1).toUpperCase());

            if (str.length() > 1) {
                sb.append(str.substring(1));
            }
        }

        var resultStr = sb.toString();

        // 如果要求驼峰命名首字母小写
        if (camelFirstLetterLowerCase) {
            resultStr = resultStr.substring(0, 1).toLowerCase() + resultStr.substring(1);
        }

        return resultStr;
    }
}
