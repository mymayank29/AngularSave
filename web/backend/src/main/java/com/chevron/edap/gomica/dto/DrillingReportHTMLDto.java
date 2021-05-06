package com.chevron.edap.gomica.dto;

import java.util.List;

public class DrillingReportHTMLDto {
    private String fileName;
    private String content;

    public DrillingReportHTMLDto(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContent() {
        return content;
    }
}
