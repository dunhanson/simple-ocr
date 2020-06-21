package site.dunhanson.ocr.utils;

import com.baidu.aip.ocr.AipOcr;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import site.dunhanson.ocr.entity.App;
import site.dunhanson.ocr.exception.NotFoundValidAipOcrException;
import site.dunhanson.utils.basic.YamlUtils;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author dunhanson
 * 2020-06-18
 * 百度OCR工具类
 */
@Slf4j
public class OcrUtils {
    // OCR配置集合
    private static Map<App, AipOcr> store = Collections.synchronizedMap(new HashMap<>());

    /**
     * 静态代码块，初始化OCR配置
     */
    static {
        Type type = new TypeToken<List<App>>(){}.getType();
        List<App> apps = YamlUtils.load("baidu-ocr.yaml", type, "apps");
        for(App app : apps) {
            AipOcr aipOcr = new AipOcr(app.getAppId(), app.getApiKey(), app.getSecretKey());
            store.put(app, aipOcr);
        }
    }

    /**
     * 文本识别
     * @param pathOrUrl
     * @return
     */
    public static String ocr(String pathOrUrl) throws NotFoundValidAipOcrException {
        if(store.isEmpty()) {
            throw new NotFoundValidAipOcrException();
        }
        App app = store.keySet().stream().findFirst().get();
        AipOcr aipOcr = store.get(app);
        // 判断路径类型
        JSONObject res;
        if(pathOrUrl.startsWith("http") || pathOrUrl.startsWith("https")) {
            res = aipOcr.basicGeneralUrl(pathOrUrl, new HashMap<>());
        } else {
            res = aipOcr.basicGeneral(pathOrUrl, new HashMap<>());
        }
        String key = "error_code";
        String text = "";
        if(res.has(key)) {
            int errorCode = res.getInt(key);
            if(errorCode == 17) {
                // 17每天请求量超限额
                store.remove(app);
            } else if (errorCode == 19) {
                // 19请求总量超限额
                store.remove(app);
            } else if (errorCode == 14) {
                // IAM 鉴权失败
                store.remove(app);
            }
            log.warn(res.toString() + "," + app.toString());
            text = ocr(pathOrUrl);
        } else {
            text = getWordsResult(res);
        }
        return text;
    }

    /**
     * 获取字符串结果
     * @param res
     * @return
     */
    public static String getWordsResult(JSONObject res) {
        StringBuilder stringBuilder = new StringBuilder();
        if(res.has("words_result")) {
            JSONArray wordsResult = res.getJSONArray("words_result");
            for(int i = 0; i < wordsResult.length(); i++) {
                String words = wordsResult.getJSONObject(i).getString("words");
                stringBuilder.append(words + "\n");
            }
        }
        return stringBuilder.toString();
    }

}
