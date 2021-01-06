import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import site.dunhanson.ocr.baidu.entity.App;
import site.dunhanson.ocr.baidu.utils.YamlUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class YamlUtilsTest {
    @Test
    public void start() {
        String path = "redis.yaml";
        Map<String, Object> map = YamlUtils.load(path);
        System.out.println(map);
    }

    @Test
    public void testByType() {
        String path = "baidu-ocr.yaml";
        Type type = new TypeToken<List<App>>(){}.getType();
        List<App> list = YamlUtils.load(path, type, "apps");
        System.out.println(list);
    }

}
