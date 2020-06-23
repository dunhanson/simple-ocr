package site.dunhanson.ocr.baidu.entity;

import com.baidu.aip.ocr.AipOcr;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusInfoStatusInfo {
    private AipOcr aipOcr;
    private App app;
    private Boolean available;
}
