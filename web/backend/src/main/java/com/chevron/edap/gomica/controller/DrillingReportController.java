package com.chevron.edap.gomica.controller;

import com.chevron.edap.gomica.service.DrillingReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class DrillingReportController {

    @Autowired
    DrillingReportService drillingReportService;

    @PreAuthorize("hasPermission('ACCESS', '')")
    @RequestMapping(value = "/api/drilling", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> createDrillingReportZIP(@RequestParam(value = "ids") List<String> ids, HttpServletResponse response) throws IOException {
        byte[] documentBody = drillingReportService.createDrillingReportZIP(ids);
        documentBody = documentBody == null ? "Error generating report".getBytes() : documentBody;
        ByteArrayResource resource = new ByteArrayResource(documentBody);
        String todaysDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String reportName = todaysDate + "_drilling_report";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + reportName + ".zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(documentBody.length)
                .body(resource);
    }

}
