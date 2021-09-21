package com.y3tu.tools.web.util;

import com.y3tu.tools.kit.base.JsonUtil;
import com.y3tu.tools.kit.collection.ArrayUtil;
import com.y3tu.tools.kit.lang.Validator;
import com.y3tu.tools.kit.regex.RegexUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * 翻译工具类
 *
 * @author y3tu
 */
@Slf4j
public class TranslatorUtil {

    /**
     * 翻译
     * 中英文互换
     *
     * @param word 需要翻译的文字
     * @return 翻译后的文字
     */
    public static String translate(String word) {
        try {
            String url = "";
            //判断word是中文还是英文
            if (Validator.isMatchRegex(RegexUtil.CHINESES, word)) {
                //中文转英文
                url = "https://translate.googleapis.com/translate_a/single?" +
                        "client=gtx&" +
                        "sl=zh-CN" +
                        "&tl=en" +
                        "&dt=t&q=" + URLEncoder.encode(word, "UTF-8");
            } else {
                //英文转中文
                url = "https://translate.googleapis.com/translate_a/single?" +
                        "client=gtx&" +
                        "sl=en" +
                        "&tl=zh-CN" +
                        "&dt=t&q=" + URLEncoder.encode(word, "UTF-8");
            }

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return parseResult(response.toString());
        } catch (Exception e) {
            log.error("翻译失败！", e);
            return word;
        }
    }

    /**
     * 解析返回的翻译Json字符串
     *
     * @param inputJson 待解析Json字符串
     * @return 解析完成的字符串
     */
    private static String parseResult(String inputJson) {
        Object[] objs = JsonUtil.parseObject(inputJson, Object[].class);
        StringBuilder result = new StringBuilder();
        if (ArrayUtil.isNotEmpty(objs)) {
            List objList = (List) objs[0];
            List obj = (List) objList.get(0);
            result.append(obj.get(0));
        }
        return result.toString();
    }
}
