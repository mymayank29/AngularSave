package com.chevron.edap.gomica.repository;

import com.chevron.edap.gomica.model.Invoice;
import com.chevron.edap.gomica.model.Invoice2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;


 
import java.util.List;
import java.util.stream.Stream;	

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor {
    Invoice findById(String id);
    List<Invoice> findByIswetherAndNptDurationGreaterThan(Boolean iswether, Float nptDuration);
    List<Invoice> findByIswetherAndByDateAndNptDurationGreaterThan(Boolean iswether, Boolean byDate, Float nptDuration);

    // TODO - REMOVE UNUSED METHODS
    List<Invoice> findByIswether(Boolean iswether);
    List<Invoice> findByIswetherAndByDateAndNptDurationGreaterThanAndInvoiceWriteback_InvoiceStatus(Boolean iswether, Boolean byDate, Float nptDuration, String invoiceStatus);
    List<Invoice> findByIswetherAndNptDurationGreaterThanAndInvoiceWriteback_InvoiceStatus(Boolean iswether, Float nptDuration, String invoiceStatus);
    List<Invoice> findByIswetherAndByDate(Boolean iswether, Boolean byDate);
    List<Invoice> findByIdIn(List<String> ids);
     
    //Implementation of paging to return subsets of payloads while making concurrent client api calls
    Page<Invoice> findAll(Pageable pageable);
    
    
}
