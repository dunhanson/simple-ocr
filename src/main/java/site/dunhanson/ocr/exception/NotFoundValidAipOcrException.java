package site.dunhanson.ocr.exception;

/**
 * @author dunhanson
 * 2020-06-20
 * 不能获取有效的AipOcr异常
 */
public class NotFoundValidAipOcrException extends OcrException {
    public NotFoundValidAipOcrException(String msg) {
        super(msg);
    }

    public NotFoundValidAipOcrException() {
        super("without available AipOcr Object");
    }
}
