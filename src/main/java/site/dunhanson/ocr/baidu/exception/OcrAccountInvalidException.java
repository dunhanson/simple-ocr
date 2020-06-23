package site.dunhanson.ocr.baidu.exception;

/**
 * @author dunhanson
 * 2020-06-20
 * OCR账号无效异常
 */
public class OcrAccountInvalidException extends OcrException {
    public OcrAccountInvalidException(String message) {
        super(message);
    }

    public OcrAccountInvalidException() {
        super("ocr account invalid, please replace with a new one.");
    }
}
