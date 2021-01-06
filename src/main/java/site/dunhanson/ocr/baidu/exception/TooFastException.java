package site.dunhanson.ocr.baidu.exception;

/**
 * @author dunhanson
 * 2020-06-20
 * 不能获取有效的AipOcr异常
 */
public class TooFastException extends OcrException {
    public TooFastException(String msg) {
        super(msg);
    }

    public TooFastException() {
        super("Open api qps request limit reached");
    }
}
