package com.chevron.edap.gomica.repository;

import com.chevron.edap.gomica.model.Invoice;
import com.chevron.edap.gomica.model.Invoice2;
import com.chevron.edap.gomica.model.InvoiceWriteback;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class InvoiceSpecification {
	
	public static Specification<Invoice2> coreInvoice2Filters(Invoice2 filter, Float nptDurationLow, Float nptDurationHigh) {
        return (root, criteriaQuery, cb) -> {
            Predicate p = cb.conjunction();
            if (filter.getIswether() != null) {				
            	p.getExpressions().add(cb.equal(root.get("iswether"), filter.getIswether()));
			}
//            p.getExpressions().add(cb.greaterThanOrEqualTo(root.get("nptDuration"), filter.getNptDuration()));
            p.getExpressions().add(cb.between(root.get("nptDuration"), nptDurationLow, nptDurationHigh));
            if(filter.getByDate()) {
                p.getExpressions().add(cb.equal(root.get("byDate"), filter.getByDate()));
            }

            if(filter.getByTitle()) {
                p.getExpressions().add(cb.equal(root.get("byTitle"), filter.getByTitle()));
            }
            return p;
        };
    }

    public static Specification<Invoice> coreInvoiceFilters(Invoice filter, Float nptDurationLow, Float nptDurationHigh) {
        return (root, criteriaQuery, cb) -> {
            Predicate p = cb.conjunction();
            if (filter.getIswether() != null) {				
            	p.getExpressions().add(cb.equal(root.get("iswether"), filter.getIswether()));
			}
//            p.getExpressions().add(cb.greaterThanOrEqualTo(root.get("nptDuration"), filter.getNptDuration()));
            p.getExpressions().add(cb.between(root.get("nptDuration"), nptDurationLow, nptDurationHigh));
            if(filter.getByDate()) {
                p.getExpressions().add(cb.equal(root.get("byDate"), filter.getByDate()));
            }

            if(filter.getByTitle()) {
                p.getExpressions().add(cb.equal(root.get("byTitle"), filter.getByTitle()));
            }
            return p;
        };
    }

    public static Specification<Invoice> flaggedForReview(String status) {
        return (root, criteriaQuery, cb) -> {
                Join<Invoice, InvoiceWriteback> writeback = root.join("invoiceWriteback");
                return cb.equal(writeback.get("invoiceStatus"), status);
        };
    }

    public static Specification<Invoice> doesMatchByContractTitle() {
        return new Specification<Invoice>() {
            @Override
            public Predicate toPredicate(Root<Invoice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("byTitle"), Boolean.TRUE);
            }
        };
    }

    public static Specification<Invoice> isWeatherNpt(Boolean isWeather) {
        return new Specification<Invoice>() {
            @Override
            public Predicate toPredicate(Root<Invoice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("iswether"), isWeather);
            }
        };
    }
}

