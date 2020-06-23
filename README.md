# simple-ocr




## Baidu-OCR

### Config

```yaml
apps:
  - name: 1
    appId: 111
    apiKey: 11111
    secretKey: 111111111
  - name: 2
    appId: 222
    apiKey: 222222
    secretKey: 222222222
```

### Start

```java
    @Test
    public void testURL() {
        String url = "http://bxkc.oss-cn-shanghai.aliyuncs.com/swf/1591962046774.jpg";
        String text = null;
        try {
            text = BaiduOcrUtils.ocr(url);
        } catch (NotFoundValidAipOcrException e) {
            e.printStackTrace();
        } catch (OcrAccountInvalidException e) {
            e.printStackTrace();
        }
        System.out.println(text);
    }

    @Test
    public void testFile() {
        File file = new File("D:\\Test\\image\\1591962046774.jpg");
        String text = null;
        try {
            text = BaiduOcrUtils.ocr(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OcrAccountInvalidException e) {
            e.printStackTrace();
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
        } catch (OcrAccountInvalidException e) {
            e.printStackTrace();
        } catch (NotFoundValidAipOcrException e) {
            e.printStackTrace();
        }
        System.out.println(text);
    }
```

