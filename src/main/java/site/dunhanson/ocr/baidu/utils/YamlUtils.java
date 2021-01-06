package site.dunhanson.ocr.baidu.utils;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dunhanson
 * 2020.06.20
 * YAML工具类
 */
@Slf4j
public class YamlUtils {
    // 配置存储库
    public static Map<String, Map<String, Object>> store = Collections.synchronizedMap(new HashMap<>());

    /**
     * 获取子节点Map对象
     * @param map
     * @param childKeys
     * @return 子节点Map指
     */
    public static Map<String, Object> getChildMap(Map<String, Object> map, String...childKeys) {
        Map<String, Object> childMap = map;
        for(String key : childKeys) {
            Object value = childMap.get(key);
            if(value instanceof Map) {
                childMap = (Map<String, Object>) value;
            }
        }
        return childMap;
    }

    /**
     * 获取子节点值
     * @param map
     * @param childKeys
     * @return Object对象
     */
    public static Object getChildValue(Map<String, Object> map, String...childKeys) {
        Object value = null;
        for(String key : childKeys) {
            value = map.get(key);
            if(value instanceof Map) {
                map = (Map<String, Object>) value;
            }
        }
        return value;
    }

    /**
     * 加载YAML文件
     * @param path
     * @return Map对象
     */
    public static Map<String, Object> load(String path) {
        Map<String, Object> value = store.get(path);
        String path2 = "src/main/resources/" + path;
        if(value == null) {
            String localPath = YamlUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            localPath = localPath.substring(1);
            localPath = localPath.substring(0, localPath.lastIndexOf("/") + 1);
            log.info("localPath:" + localPath);
            File localFile = new File(localPath + path);
            if(localFile.exists()) {
                try(InputStream inputStream = new FileInputStream(localFile)) {
                    value = new Yaml().load(inputStream);
                    store.put(path, value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if(new File(path2).exists()){
                try(InputStream inputStream = new FileInputStream(path2)) {
                    value = new Yaml().load(inputStream);
                    store.put(path, value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                try(InputStream inputStream = YamlUtils.class.getClassLoader().getResourceAsStream(path)) {
                    value = new Yaml().load(inputStream);
                    store.put(path, value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return value;
    }

    /**
     * 加载YAML文件,获取实体对象
     * @param path
     * @param type
     * @param <T>
     * @return 实体对象
     */
    public static <T> T load(String path, Class<T> type) {
        Gson gson = GsonUtils.gson;
        return gson.fromJson(gson.toJson(load(path)), type);
    }

    /**
     * 加载YAML文件,获取实体对象
     * @param path
     * @param type
     * @param <T>
     * @return 实体对象
     */
    public static <T> T load(String path, Type type) {
        Gson gson = GsonUtils.gson;
        return gson.fromJson(gson.toJson(load(path)), type);
    }

    /**
     * 加载YAML文件,获取实体对象,通过子节点key
     * @param path
     * @param type
     * @param childKeys
     * @param <T>
     * @return 实体对象
     */
    public static <T> T load(String path, Class<T> type, String...childKeys) {
        Gson gson = GsonUtils.gson;
        Map<String, Object> map = load(path);
        Object value = getChildValue(map, childKeys);
        String json = gson.toJson(value);
        return gson.fromJson(json, type);
    }

    /**
     * 加载YAML文件,获取实体对象,通过子节点key
     * @param path
     * @param type
     * @param childKeys
     * @param <T>
     * @return 实体对象
     */
    public static <T> T load(String path, Type type, String...childKeys) {
        Gson gson = GsonUtils.gson;
        Map<String, Object> map = load(path);
        Object value = getChildValue(map, childKeys);
        String json = gson.toJson(value);
        return gson.fromJson(json, type);
    }

    /**
     * 加载YAML文件,获取子节点值
     * @param path
     * @param childKeys
     * @return 子节点值
     */
    public static Object load(String path, String...childKeys) {
        return getChildValue(load(path), childKeys);
    }

}