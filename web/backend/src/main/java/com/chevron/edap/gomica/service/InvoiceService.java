package com.chevron.edap.gomica.service;


import static com.chevron.edap.gomica.repository.InvoiceSpecification.coreInvoiceFilters;
import static com.chevron.edap.gomica.repository.InvoiceSpecification.coreInvoice2Filters;
import static com.chevron.edap.gomica.repository.InvoiceSpecification.flaggedForReview;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.springframework.data.jpa.domain.Specifications.where;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chevron.edap.gomica.dto.InvoiceDto;
import com.chevron.edap.gomica.model.Invoice;
import com.chevron.edap.gomica.model.Invoice2;
import com.chevron.edap.gomica.model.InvoiceWriteback;
import com.chevron.edap.gomica.repository.Invoice2Repository;
import com.chevron.edap.gomica.repository.InvoiceRepository;
import com.chevron.edap.gomica.repository.InvoiceWritebackRepository;
import com.chevron.edap.gomica.security.AuthService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;



@Service
public class InvoiceService {

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    Invoice2Repository invoice2Repository;

    @Autowired
    AuthService authService;

    @Autowired
    InvoiceWritebackRepository invoiceWritebackRepository;
    
    private static Logger LOGGER = LoggerFactory.getLogger(InvoiceService.class);
    
    public String getInvoiceCount(String is_weather_npt, Boolean matchByDate, Boolean matchByTitle, Float nptDurationLow, Float nptDurationHigh) {
    	
    	
    	Invoice filter = new Invoice();
         filter.setByDate(matchByDate);
         filter.setByTitle(matchByTitle);
         if (Arrays.asList("Y", "N").contains(is_weather_npt)) {			
         	filter.setIswether("Y".equals(is_weather_npt));
 		}
         
         JsonObject object = new JsonObject();
         object.addProperty("count",invoiceRepository.count(where(coreInvoiceFilters(filter, nptDurationLow, nptDurationHigh))));
         
//         LOGGER.debug("COUNT==>"+invoiceRepository.count(where(coreInvoiceFilters(filter, nptDurationLow, nptDurationHigh))));
//         LOGGER.debug("COUNT OBJ==>"+object.toString());

         return object.toString();
    }

    public String getInvoices2(String is_weather_npt, Boolean matchByDate, Boolean matchByTitle, Float nptDurationLow, int pageparm, int pagesizeparm,  Float nptDurationHigh) {

        Invoice2 filter = new Invoice2();
        filter.setByDate(matchByDate);
        filter.setByTitle(matchByTitle);
        if (Arrays.asList("Y", "N").contains(is_weather_npt)) {			
        	filter.setIswether("Y".equals(is_weather_npt));
		}
        
        Pageable paging = new PageRequest(pageparm, pagesizeparm);
        
        Page<Invoice2> invoicesPage = invoice2Repository.findAll(where(coreInvoice2Filters(filter, nptDurationLow, nptDurationHigh)),paging);
        
        List<Invoice2> invoices = invoicesPage.getContent();
                
		List<InvoiceDto> invoicesDto = invoices
		.stream()
		.map(invoice -> new InvoiceDto(invoice))
		.collect(Collectors.toList());

        Gson gson =new Gson();
        JsonArray arr = gson.toJsonTree(invoicesDto, new TypeToken<List<InvoiceDto>>(){}.getType())
                .getAsJsonArray();
        JsonObject object = new JsonObject();
        object.add("payload", arr);
        
//        LOGGER.info(String.format("PAYLOAD==>%s", object.toString()));


        return object.toString();

    }

    public String updateInvoicesFeedbackToOutOfScope(List<InvoiceDto> invoiceDtoList) {
        List<Invoice> invoices = invoiceRepository.findByIdIn(invoiceDtoList
                .stream()
                .map(dto -> dto.getId())
                .collect(Collectors.toList()))
                .stream()
                .map(inv -> {
                        InvoiceWriteback invoiceWriteback = inv.getInvoiceWriteback() == null ? new InvoiceWriteback()
                                : inv.getInvoiceWriteback();
                        invoiceWriteback.setInvoiceStatus(InvoiceWriteback.Status.NPT_OUT_OF_SCOPE.toString());
                        invoiceWriteback.setRecovered(0F);
                        invoiceWriteback.setSpent_leakage_confirmed(0F);
                        invoiceWriteback.setModifyed_by(authService.getUserDisplayName());
                        invoiceWriteback.setModifyed_date(new Timestamp(System.currentTimeMillis()));
                        inv.setInvoiceWriteback(invoiceWriteback);
                        return inv;
                }).collect(Collectors.toList());

        List<InvoiceDto> savedInvoices = invoiceRepository.save(invoices).stream().map(inv -> new InvoiceDto(inv)).collect(Collectors.toList());
        Gson gson =new Gson();
        JsonArray result = gson.toJsonTree(savedInvoices, new TypeToken<List<InvoiceDto>>(){}.getType()).getAsJsonArray();
        JsonObject response = new JsonObject();
        response.add("payload", result);

        //save writeback info to view
        //invoice2Repositorinvoice2Repositoryy.save(invoiceDtoToInvoice2List(savedInvoices));

        return response.toString();
    }

    public String updateInvoiceFeedbackById(String id, InvoiceDto invoiceDto) {

        Invoice invoice  = invoiceRepository.findById(id);
        InvoiceWriteback invoiceWriteback = invoice.getInvoiceWriteback() == null ? new InvoiceWriteback()
                : invoice.getInvoiceWriteback();
        invoiceWriteback.setComment(invoiceDto.getComment());
        invoiceWriteback.setInvoiceStatus(invoiceDto.getStatus());
        invoiceWriteback.setRecovered(invoiceDto.getRecovered());
        invoiceWriteback.setSpent_leakage_confirmed(invoiceDto.getSpent_leakage_confirmed());
        invoiceWriteback.setModifyed_by(authService.getUserDisplayName());
        invoiceWriteback.setModifyed_date(new Timestamp(System.currentTimeMillis()));
        invoice.setInvoiceWriteback(invoiceWriteback);
        invoiceWriteback.setInvoice(invoice);
        Invoice saved_invoice = invoiceRepository.save(invoice);
        Gson gson = new Gson();
        JsonObject result = gson.toJsonTree(new InvoiceDto(saved_invoice), new TypeToken<InvoiceDto>() {
        }.getType()).getAsJsonObject();
        JsonObject response = new JsonObject();
        response.add("payload", result);

        //save writeback info to view
        //invoice2Repository.save(invoiceDtoToInvoice2(invoiceDto));

        return response.toString();
    }

    public String updateInvoicesFeedbackById(List<String> ids, List<InvoiceDto> invoicesDto) {

        Map<String, InvoiceDto> dtoMap = invoicesDto.stream().collect(Collectors.toMap(InvoiceDto::getId, inv -> inv));
        List<Invoice> invoices  = invoiceRepository.findByIdIn(ids);
        for (Invoice invoice : invoices) {
            InvoiceDto invoiceDto = dtoMap.get(invoice.getId());
            InvoiceWriteback invoiceWriteback = invoice.getInvoiceWriteback() == null ? new InvoiceWriteback()
                    : invoice.getInvoiceWriteback();
            invoiceWriteback.setComment(invoiceDto.getComment());
            invoiceWriteback.setInvoiceStatus(invoiceDto.getStatus());
            invoiceWriteback.setRecovered(invoiceDto.getRecovered());
            invoiceWriteback.setSpent_leakage_confirmed(invoiceDto.getSpent_leakage_confirmed());
            invoiceWriteback.setModifyed_by(authService.getUserDisplayName());
            invoiceWriteback.setModifyed_date(new Timestamp(System.currentTimeMillis()));
            invoice.setInvoiceWriteback(invoiceWriteback);
            invoiceWriteback.setInvoice(invoice);
        }

        List<Invoice> savedInvoices = invoiceRepository.save(invoices);
        invoicesDto = savedInvoices.stream().map(inv -> new InvoiceDto(inv)).collect(Collectors.toList());
        Gson gson =new Gson();
        JsonArray result = gson.toJsonTree(invoicesDto, new TypeToken<List<InvoiceDto>>(){}.getType()).getAsJsonArray();
        JsonObject response = new JsonObject();
        response.add("payload", result);
        
        //update invoices2 view with writeback info
        //invoice2Repository.save(invoiceDtoToInvoice2List(invoicesDto));
        
        

        return response.toString();
    }

    public List<InvoiceWriteback> changeInvoicesWritebackSatus(List<String> ids, String status) {
        List<InvoiceWriteback> invoiceWritebacks  = invoiceWritebackRepository.findByIdIn(ids);

        for(InvoiceWriteback invoiceWriteback: invoiceWritebacks){
            if(invoiceWriteback.getSpent_leakage_confirmed() == null || invoiceWriteback.getSpent_leakage_confirmed() == 0) {
                Invoice invoice = invoiceRepository.findById(invoiceWriteback.getId());
                invoiceWriteback.setSpent_leakage_confirmed(invoice.getPt_spent_leakage_npt());
            }
            invoiceWriteback.setInvoiceStatus(status);
            invoiceWriteback.setModifyed_by(authService.getUserDisplayName());
            invoiceWriteback.setModifyed_date(new Timestamp(System.currentTimeMillis()));
        }

        invoiceWritebackRepository.save(invoiceWritebacks);
        return invoiceWritebacks;
    }


    public String getInvoicesFlaggedForReview(String is_weather_npt, Boolean matchByDate, Boolean matchByTitle, Float nptDurationLow, Float nptDurationHigh) {
        Invoice filter = new Invoice();
        filter.setByDate(matchByDate);
        filter.setByTitle(matchByTitle);
        if (Arrays.asList("Y", "N").contains(is_weather_npt)) {			
        	filter.setIswether("Y".equals(is_weather_npt));
		}
        
        List<Invoice> invoices = invoiceRepository
                .findAll(where(coreInvoiceFilters(filter, nptDurationLow, nptDurationHigh))
                        .and(flaggedForReview(InvoiceWriteback.Status.FLAGGED_FOR_REVIEW.toString())));

        List<InvoiceDto> invoicesDto = invoices.stream()
                .map(invoice -> new InvoiceDto(invoice))
                .collect(Collectors.toList());

        Gson gson =new Gson();
        JsonArray arr = gson.toJsonTree(invoicesDto, new TypeToken<List<InvoiceDto>>(){}.getType())
                .getAsJsonArray();
        JsonObject object = new JsonObject();
        object.add("payload", arr);

        return object.toString();
    }

    private List<Invoice2> invoiceDtoToInvoice2List(List<InvoiceDto> invoicedtoList) {
    	return invoicedtoList.stream()
        			.map(invdto -> invoiceDtoToInvoice2(invdto))
        			.collect(Collectors.toList());
    }
    
    private Invoice2 invoiceDtoToInvoice2(InvoiceDto invoicedto) {
    	Invoice2 invoice2 = new Invoice2();
    	invoice2.setContract_id(invoicedto.getContract_id());
        invoice2.setAriba_doc_id(invoicedto.getAriba_doc_id());
        invoice2.setWellname(invoicedto.getWellname());
        invoice2.setAfe(invoicedto.getAfe());
        invoice2.setWbs_element(invoicedto.getWbs_element());
        invoice2.setJobtyp(invoicedto.getJobtyp());
        invoice2.setNpt_date_end(invoicedto.getNpt_event_no());
        invoice2.setParent_vendor(invoicedto.getParent_vendor());
        invoice2.setSubmit_date(invoicedto.getSubmit_date());
        invoice2.setPrice(invoicedto.getPrice());
        invoice2.setPt_spent_leakage_npt(invoicedto.getPt_spent_leakage_npt());
        invoice2.setNpt_date_start(invoicedto.getNpt_date_start());
        invoice2.setNpt_date_end(invoicedto.getNpt_date_end());
        invoice2.setNptDuration(invoicedto.getNpt_duration());
        invoice2.setNpt_ref_no(invoicedto.getNpt_ref_no());
        invoice2.setServicetyp(invoicedto.getServicetyp());
        invoice2.setNptTypeDetailDescription(invoicedto.getNpt_type_detail_desc());
        invoice2.setNptDescription(invoicedto.getNpt_desc());
        //invoicedto.getNpt_type()); // add column in spark!
        invoice2.setNptTypeTypeDetail(invoicedto.getNpt_type_detail());
        invoice2.setContractTitle(invoicedto.getContract_title());
        invoice2.setInvoiceStartDate(invoicedto.getWork_start_date());
        invoice2.setInvoiceEndDate(invoicedto.getWork_end_date());
        invoice2.setContract_owner_name(invoicedto.getContract_owner_name() == null ? "-" : invoicedto.getContract_owner_name());
        invoice2.setByDate(invoicedto.getBy_date());
        invoice2.setIswether(invoicedto.getIs_weather());

        invoice2.setApprovedDate(invoice2.getApprovedDate());

        invoice2.setId(invoicedto.getId());
        invoice2.setRecovered(invoicedto.getRecovered());
		invoice2.setInvoiceStatus(invoicedto.getStatus() == null ? InvoiceWriteback.Status.BLANK.toString() : invoicedto.getStatus());
		invoice2.setSpent_leakage_confirmed(invoicedto.getSpent_leakage_confirmed());
		invoice2.setComment(invoicedto.getComment() == null ? "" : invoicedto.getComment());
		invoice2.setModifyed_by(invoicedto.getModified_by() == null ? "" : invoicedto.getModified_by());
		invoice2.setModifyed_date(new Timestamp(System.currentTimeMillis()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate npt_date_end_parsed = LocalDate.parse(invoice2.getNpt_date_end(), formatter);
        LocalDate today = LocalDate.now();
       
//        invoice2.setNptDuration((float) DAYS.between(npt_date_end_parsed,today));


    	return invoice2;
    }

}
