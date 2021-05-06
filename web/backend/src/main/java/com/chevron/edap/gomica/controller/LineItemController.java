package com.chevron.edap.gomica.controller;

import com.chevron.edap.gomica.service.LineItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LineItemController {

    @Autowired
    LineItemService lineItemService;

//    @PreAuthorize("hasPermission('ACCESS', '')")
//    @RequestMapping(value = "/api/line-items/{ariba_doc_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public String getLineItemsByAribaDoc(@PathVariable String ariba_doc_id){
//        return lineItemService.getLineItemsByAribaDocId(ariba_doc_id);
//    }

    @PreAuthorize("hasPermission('ACCESS', '')")
    @RequestMapping(value = "/api/line-items/{ariba_doc_ids}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getLineItemsByAribaDocIds(@PathVariable List<String> ariba_doc_ids){
        return lineItemService.getLineItemsByAribaDocIds(ariba_doc_ids);
    }
}
