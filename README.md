# simple-ocr
OCR



## Start

```java
public class OcrTest {
    @Test
    public void start() {
        String url = "http://bxkc.oss-cn-shanghai.aliyuncs.com/swf/1591962046774.jpg";
        String text = null;
        try {
            text = OcrUtils.ocr(url);
        } catch (NotFoundValidAipOcrException e) {
            // 处理操作
            e.printStackTrace();
        }
        System.out.println(text);
    }
}
```

