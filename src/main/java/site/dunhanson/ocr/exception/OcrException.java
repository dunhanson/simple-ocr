package site.dunhanson.ocr.exception;

/**
 * @author dunhanson
 * 2020-06-20
 * OCR异常对象
 */
public abstract class OcrException extends Exception{
    public OcrException(String message) {
        super(message);
    }
}
