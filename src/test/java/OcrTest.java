import org.apache.commons.io.IOUtils;
import org.junit.Test;
import site.dunhanson.ocr.baidu.exception.NotFoundValidAipOcrException;
import site.dunhanson.ocr.baidu.exception.OcrAccountInvalidException;
import site.dunhanson.ocr.baidu.utils.BaiduOcrUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class OcrTest {
    @Test
    public void testURL() {
        String url = "http://bxkc.oss-cn-shanghai.aliyuncs.com/swf/1591962046774.jpg";
        String text = null;
        try {
            text = BaiduOcrUtils.ocr(url);
        } catch (NotFoundValidAipOcrException e) {
            e.printStackTrace();
        }
        System.out.println(text);
    }

    @Test
    public void testFile() {
        File file = new File("D:\\test\\1591962046774.jpg");
        String text = null;
        try {
            text = BaiduOcrUtils.ocr(file);
        } catch (NotFoundValidAipOcrException e) {
            e.printStackTrace();
        }
        System.out.println(text);
    }

    @Test
    public void TestBytes() {
        File file = new File("D:\\Test\\image\\1591962046774.jpg");
        String text = null;
        try (InputStream input = new FileInputStream(file)){
            text = BaiduOcrUtils.ocr(IOUtils.toByteArray(input));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotFoundValidAipOcrException e) {
            e.printStackTrace();
        }
        System.out.println(text);
    }

    @Test
    public void test() throws NotFoundValidAipOcrException {
        System.out.println(BaiduOcrUtils.getApp().getAppId());
        System.out.println(BaiduOcrUtils.getApp().getAppId());
        System.out.println(BaiduOcrUtils.getApp().getAppId());
        System.out.println(BaiduOcrUtils.getApp().getAppId());
        System.out.println(BaiduOcrUtils.getApp().getAppId());
        System.out.println(BaiduOcrUtils.getApp().getAppId());
    }
}
