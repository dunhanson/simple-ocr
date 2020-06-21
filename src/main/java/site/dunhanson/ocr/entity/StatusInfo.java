package site.dunhanson.ocr.entity;

import com.baidu.aip.ocr.AipOcr;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusInfo {
    private AipOcr aipOcr;
    private App app;
    private Boolean available;
}
