package com.chevron.edap.gomica.repository;

import com.chevron.edap.gomica.model.Invoice;
import com.chevron.edap.gomica.model.InvoiceWriteback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceWritebackRepository extends JpaRepository<InvoiceWriteback, String>, JpaSpecificationExecutor {
    InvoiceWriteback findById(String id);
    List<InvoiceWriteback> findByIdIn(List<String> ids);
}
