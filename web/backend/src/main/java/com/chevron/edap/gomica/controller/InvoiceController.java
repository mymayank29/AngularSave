package com.chevron.edap.gomica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chevron.edap.gomica.dto.InvoiceDto;
import com.chevron.edap.gomica.model.InvoiceWriteback;
import com.chevron.edap.gomica.security.AuthService;
import com.chevron.edap.gomica.service.InvoiceService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.json.JsonSanitizer;

@RestController
public class InvoiceController {

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    AuthService authService;

//    @PreAuthorize("hasPermission('ACCESS', '')")
//    @RequestMapping(value = "/api/invoices", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public  String getAllInvoices(@RequestParam(value = "isweather") Boolean is_weather,
//                                  @RequestParam(value = "islowimpact") Boolean is_low_impact,
//                                  @RequestParam(value = "ismatchbydate") Boolean matchByDate,
//                                  @RequestParam(value = "ismatchbycontract") Boolean matchByContract) {
//        return  invoiceService.getInvoices(is_weather, is_low_impact, matchByDate, matchByContract);
//    }
    
    @PreAuthorize("hasPermission('ACCESS', '')")
    @RequestMapping(value = "/api/invoices/getinvoicecount", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getInvoiceCount(@RequestParam(value = "isweather") String is_weather,
                                   @RequestParam(value = "ismatchbydate") Boolean matchByDate,
                                   @RequestParam(value = "ismatchbycontract") Boolean matchByContract,
                                   @RequestParam(value = "nptdurationlow") float nptdurationlow,
                                   @RequestParam(value = "nptdurationhigh") float nptdurationhigh) {
        return  invoiceService.getInvoiceCount(is_weather, matchByDate, matchByContract, nptdurationlow, nptdurationhigh);

    }

    @PreAuthorize("hasPermission('ACCESS', '')")
    @RequestMapping(value = "/api/invoices", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public  String getAllInvoices2(@RequestParam(value = "isweather") String is_weather,
                                   @RequestParam(value = "ismatchbydate") Boolean matchByDate,
                                   @RequestParam(value = "ismatchbycontract") Boolean matchByContract,
                                   @RequestParam(value = "nptdurationlow") float nptdurationlow,
                                   @RequestParam(value = "pageno") int pageparm,
                                   @RequestParam(value = "pagesize") int pagesizeparm,
                                   @RequestParam(value = "nptdurationhigh") float nptdurationhigh) {
        return  invoiceService.getInvoices2(is_weather, matchByDate, matchByContract, nptdurationlow, pageparm, pagesizeparm, nptdurationhigh);
    }

    @PreAuthorize("hasPermission('ACCESS', '')")
    @RequestMapping(value = "/api/flagged-invoices", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getFlaggedInvoices(@RequestParam(value = "isweather") String is_weather,
                                     @RequestParam(value = "ismatchbydate") Boolean matchByDate,
                                     @RequestParam(value = "ismatchbycontract") Boolean matchByContract,
                                     @RequestParam(value = "nptdurationlow") float nptdurationlow,
                                     @RequestParam(value = "nptdurationhigh") float nptdurationhigh) {
        return invoiceService.getInvoicesFlaggedForReview(is_weather, matchByDate, matchByContract, nptdurationlow, nptdurationhigh);
    }

    @PreAuthorize("hasPermission('EDIT', '')")
    @RequestMapping(value = "/api/invoices/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public  String updateInvoiceFeedback(@PathVariable String id, @RequestBody InvoiceDto invoiceDto){
            return  invoiceService.updateInvoiceFeedbackById(id, invoiceDto);
    }

    @PreAuthorize("hasPermission('ACCESS', '')")
    @RequestMapping(value = "/api/invoices/update", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public  String updateInvoicesFeedback(	@RequestParam(value = "ids") String idsStr, 
    										@RequestParam(value = "invoices") String invoicesDtoStr ){

//    	LOGGER.debug(String.format("idsStr ==>%s", idsStr));
//    	LOGGER.debug(String.format("invoicesDtoStr ==>%s", invoicesDtoStr));

    	Gson gsonmapper = new Gson();
    	
    	String sanitizedInvoiceDtos = JsonSanitizer.sanitize(invoicesDtoStr);
    	String sanitizedIdStr = JsonSanitizer.sanitize(idsStr);
    	List<InvoiceDto> invoicesDto = gsonmapper.fromJson(sanitizedInvoiceDtos, new TypeToken<List<InvoiceDto>>(){}.getType());
    	List<String> ids = gsonmapper.fromJson(sanitizedIdStr, new TypeToken<List<String>>(){}.getType());

        return  invoiceService.updateInvoicesFeedbackById(ids, invoicesDto);
    }   

    @PreAuthorize("hasPermission('EDIT', '')")
    @RequestMapping(value = "/api/flagged-invoices/out-of-scope", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public  String updateInvoiceFeedback(@RequestBody List<InvoiceDto> invoiceDtoList){
    public  String updateInvoiceFeedback(@RequestParam(value = "flaggedInvoices") String invoiceDtoListStr ){
    	Gson gsonmapper = new Gson();
    	String sanitizedString = JsonSanitizer.sanitize(invoiceDtoListStr);
    	List<InvoiceDto> invoiceDtoList = gsonmapper.fromJson(sanitizedString, new TypeToken<List<InvoiceDto>>(){}.getType());

        return  invoiceService.updateInvoicesFeedbackToOutOfScope(invoiceDtoList);
    }

    @PreAuthorize("hasPermission('EDIT', '')")
    @RequestMapping(value = "/api/flagged-invoices/supplier-review", method = RequestMethod.PUT)
    public String updateInvoiceFeedbackToSupplierReview(@RequestBody List<String> ids) {
        invoiceService.changeInvoicesWritebackSatus(ids, InvoiceWriteback.Status.SUPPLIER_REVIEW.toString());
        String str = "";
        return str;
    }

    @InitBinder("invoiceDto")
    protected void initBinder(WebDataBinder binder){
        binder.setAllowedFields("id", "status", "spent_leakage_confirmed",
                "recovered", "comment", "modified_by",
                "modified_date", "wbs_element");
    }
}
