package site.dunhanson.ocr.baidu.utils;

import com.baidu.aip.ocr.AipOcr;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import site.dunhanson.ocr.baidu.entity.App;
import site.dunhanson.ocr.baidu.exception.NotFoundValidAipOcrException;
import site.dunhanson.ocr.baidu.exception.OcrAccountInvalidException;
import site.dunhanson.utils.basic.YamlUtils;
import java.io.*;
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
public class BaiduOcrUtils {
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
     * 获取APP
     * @return App
     */
    private static App getApp() throws NotFoundValidAipOcrException {
        if(store.isEmpty()) {
            throw new NotFoundValidAipOcrException();
        }
        return store.keySet().stream().findFirst().get();
    }

    /**
     * 文本识别
     * @param pathOrUrl
     * @return 文本
     */
    public static String ocr(String pathOrUrl) throws NotFoundValidAipOcrException, OcrAccountInvalidException {
        App app = getApp();
        AipOcr aipOcr = store.get(app);
        // 判断路径类型
        JSONObject res;
        if(pathOrUrl.startsWith("http") || pathOrUrl.startsWith("https")) {
            res = aipOcr.basicGeneralUrl(pathOrUrl, new HashMap<>());
        } else {
            res = aipOcr.basicGeneral(pathOrUrl, new HashMap<>());
        }
        return handleResponse(res, app);
    }

    /**
     * 识别二进制
     * @param image
     * @return 文本
     * @throws NotFoundValidAipOcrException
     * @throws OcrAccountInvalidException
     */
    public static String ocr(byte[] image) throws NotFoundValidAipOcrException, OcrAccountInvalidException {
        App app = getApp();
        AipOcr aipOcr = store.get(app);
        // 判断路径类型
        JSONObject res = aipOcr.basicGeneral(image, new HashMap<>());
        return handleResponse(res, app);
    }

    /**
     * 识别File
     * @param file
     * @return 文本
     * @throws NotFoundValidAipOcrException
     * @throws OcrAccountInvalidException
     * @throws IOException
     */
    public static String ocr(File file) throws NotFoundValidAipOcrException, OcrAccountInvalidException, IOException {
        if(store.isEmpty()) {
            throw new NotFoundValidAipOcrException();
        }
        App app = store.keySet().stream().findFirst().get();
        AipOcr aipOcr = store.get(app);
        // 判断路径类型
        JSONObject res;
        try(InputStream input = new FileInputStream(file)) {
            res = aipOcr.basicGeneral(IOUtils.toByteArray(input), new HashMap<>());
        }
        return handleResponse(res, app);
    }

    /**
     * 处理返回结果
     * @param res
     * @param app
     * @return 文本内容
     * @throws NotFoundValidAipOcrException
     * @throws OcrAccountInvalidException
     */
    public static String handleResponse(JSONObject res, App app) throws NotFoundValidAipOcrException, OcrAccountInvalidException {
        String key = "error_code";
        String text = "";
        if(res.has(key)) {
            int errorCode = res.getInt(key);
            // 17每天请求量超限额
            // 19请求总量超限额
            // 14IAM 鉴权失败
            if(errorCode == 17 || errorCode == 19 || errorCode == 14) {
                log.warn(res.toString() + "," + app.toString());
                throw new OcrAccountInvalidException();
            } else {
                log.warn(res.toString());
            }
        } else {
            text = getWordsResult(res);
        }
        return text;
    }

    /**
     * 获取字符串结果
     * @param res
     * @return 文本内容
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
