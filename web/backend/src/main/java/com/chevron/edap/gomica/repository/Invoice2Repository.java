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
public interface Invoice2Repository extends JpaRepository<Invoice2, Long>, JpaSpecificationExecutor {
    Invoice2 findById(String id);
     
    //Implementation of paging to return subsets of payloads while making concurrent client api calls
    Page<Invoice2> findAll(Pageable pageable);
    List<Invoice2> findByIdIn(List<String> ids);
    
}
