package site.dunhanson.ocr.entity;

import lombok.Data;

@Data
public class App {
    private String name;
    private String appId;
    private String apiKey;
    private String secretKey;
}
