import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import site.dunhanson.ocr.baidu.exception.NotFoundValidAipOcrException;
import site.dunhanson.ocr.baidu.utils.BaiduOcrUtils;

public class DemoTest {
    @Test
    public void test() throws NotFoundValidAipOcrException {
        for(int i = 0; i < 100; i++) {
            System.out.println(BaiduOcrUtils.getApp().getName());
        }
    }
}
