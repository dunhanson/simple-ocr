package site.dunhanson.ocr.baidu.utils;

import com.baidu.aip.ocr.AipOcr;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import site.dunhanson.ocr.baidu.entity.App;
import site.dunhanson.ocr.baidu.exception.NotFoundValidAipOcrException;
import site.dunhanson.ocr.baidu.exception.OcrAccountInvalidException;
import site.dunhanson.ocr.baidu.exception.TooFastException;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dunhanson
 * 2020-06-18
 * 百度OCR工具类
 */
@Slf4j
public class BaiduOcrUtils {
    // OCR配置集合
    private static Map<App, AipOcr> store = Collections.synchronizedMap(new HashMap<>());
    private static Map<String, String> dateStore = Collections.synchronizedMap(new HashMap<>());

    /**
     * 初始化
     */
    public static void init() {
        // 获取时间，如果为空，说明是第二天，则进行初始化store
        // 当天则不处理
        String nowDate = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        if(dateStore.get(nowDate) == null || store.isEmpty()) {
            store.clear();
            Type type = new TypeToken<List<App>>(){}.getType();
            List<App> apps = YamlUtils.load("baidu-ocr.yaml", type, "apps");
            for(App app : apps) {
                AipOcr aipOcr = new AipOcr(app.getAppId(), app.getApiKey(), app.getSecretKey());
                store.put(app, aipOcr);
            }
            dateStore.put(nowDate, nowDate);
        }
    }

    /**
     * 获取APP
     * @return App
     */
    public static App getApp() throws NotFoundValidAipOcrException {
        // 初始化相关
        init();
        // 如果store为空，说明没有可用的ocr账号了
        if(store.isEmpty()) {
            throw new NotFoundValidAipOcrException();
        }
        Set<App> appSet = store.keySet();
        int index = RandomUtils.nextInt(0, appSet.size());
        return appSet.stream().collect(Collectors.toList()).get(index);
    }

    /**
     * 文本识别
     * @param pathOrUrl
     * @return 识别内容
     * @throws NotFoundValidAipOcrException
     */
    public static String ocr(String pathOrUrl) throws NotFoundValidAipOcrException {
        App app = getApp();
        AipOcr aipOcr = store.get(app);
        // 判断路径类型
        JSONObject res;
        if(pathOrUrl.startsWith("http") || pathOrUrl.startsWith("https")) {
            res = aipOcr.basicGeneralUrl(pathOrUrl, new HashMap<>());
        } else {
            res = aipOcr.basicGeneral(pathOrUrl, new HashMap<>());
        }
        String result = "";
        try {
            result = handleResponse(res, app);
        } catch (OcrAccountInvalidException e) {
            // ocr账号无效，删除对应，重新回调
            log.warn(e.getMessage());
            log.warn("invalid account:{}", app.getName());
            store.remove(app);
            result = ocr(pathOrUrl);
        } catch (TooFastException e) {
            log.warn(e.getMessage());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            result = ocr(pathOrUrl);
        }
        return result;
    }

    /**
     * 识别二进制
     * @param image
     * @return 识别内容
     * @throws NotFoundValidAipOcrException
     */
    public static String ocr(byte[] image) throws NotFoundValidAipOcrException {
        App app = getApp();
        AipOcr aipOcr = store.get(app);
        // 判断路径类型
        JSONObject res = aipOcr.basicGeneral(image, new HashMap<>());
        String result = "";
        try {
            result = handleResponse(res, app);
        } catch (OcrAccountInvalidException e) {
            // ocr账号无效，删除对应，重新回调
            log.warn(e.getMessage());
            store.remove(app);
            result = ocr(image);
        } catch (TooFastException e) {
            log.warn(e.getMessage());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            result = ocr(image);
        }
        return result;
    }

    /**
     * 识别File
     * @param file
     * @return 识别内容
     * @throws NotFoundValidAipOcrException
     * @throws IOException
     */
    public static String ocr(File file) throws NotFoundValidAipOcrException {
        if(!file.exists()) {
            log.warn("file not found, path={}", file.getAbsoluteFile());
            return "";
        }
        App app = getApp();
        AipOcr aipOcr = store.get(app);
        // 判断路径类型
        JSONObject res;
        try {
            try(InputStream input = new FileInputStream(file)) {
                res = aipOcr.basicGeneral(IOUtils.toByteArray(input), new HashMap<>());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String result = "";
        try {
            result = handleResponse(res, app);
        } catch (OcrAccountInvalidException e) {
            // ocr账号无效，删除对应，重新回调
            log.warn(e.getMessage());
            store.remove(app);
            result = ocr(file);
        } catch (TooFastException e) {
            log.warn(e.getMessage());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            result = ocr(file);
        }
        return result;
    }

    /**
     * 处理返回结果
     * @param res
     * @param app
     * @return 识别内容
     * @throws OcrAccountInvalidException
     */
    public static String handleResponse(JSONObject res, App app) throws OcrAccountInvalidException, TooFastException {
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
            } else if(errorCode == 18) {
                throw new TooFastException();
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
