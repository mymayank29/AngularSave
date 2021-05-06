package com.chevron.edap.gomica.controller;

import com.chevron.edap.gomica.service.IntervalProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class IntervalProblemController {

    @Autowired
    IntervalProblemService intervalProblemService;

//    @PreAuthorize("hasPermission('ACCESS', '')")
//    @RequestMapping(value = "/api/npt/{ariba_doc_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public String getLineItemsByAribaDoc(@PathVariable String ariba_doc_id){
//        return intervalProblemService.getNptsByAribaDocId(ariba_doc_id);
//    }

    @PreAuthorize("hasPermission('ACCESS', '')")
    @RequestMapping(value = "/api/npt/{ariba_doc_ids}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getLineItemsByAribaDocIds(@PathVariable List<String> ariba_doc_ids, @RequestParam Boolean isMatchByDate, @RequestParam String isWeather){
        return intervalProblemService.getNptsByAribaDocIds(ariba_doc_ids, isMatchByDate, isWeather);
    }
}
