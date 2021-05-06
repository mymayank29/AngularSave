package com.chevron.edap.gomica.dto;

import static java.time.temporal.ChronoUnit.DAYS;
import static com.chevron.edap.gomica.util.StringEscapeTool.replaceXMLSpecialChars;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.chevron.edap.gomica.model.Invoice;
import com.chevron.edap.gomica.model.Invoice2;
import com.chevron.edap.gomica.model.InvoiceWriteback;
import com.chevron.edap.gomica.util.CustomDateDeserializer;
import com.chevron.edap.gomica.util.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class InvoiceDto {

    private String contract_id;
    private String ariba_doc_id;
    private String wellname;
    private String afe;
    private String jobtyp;
    private String npt_event_no;
    private String parent_vendor;
    private String submit_date;
    private Float price;
    private Float pt_spent_leakage_npt;
    private String npt_date_start;
    private String npt_date_end;
    private Float npt_duration;
    private String npt_ref_no;
    private String servicetyp;
    private String npt_type_detail_desc;
    private String npt_desc;
    private String npt_type;
    private String npt_type_detail;
    private String contract_title;
    private String work_start_date;
    private String work_end_date;
    private String supplier_name_without;
    private String contract_owner_name;
    private Boolean by_date;
    private Boolean is_weather;
    private String rig_no;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date approved_date;

    // WRITEBACK PART
    private String id;
    private String status;
    private Float spent_leakage_confirmed;
    private Float recovered;
    private String comment;
    private String modified_by;
    private String modified_date;
    private String wbs_element;

    // CALCULATED FIELDS
    private Long days_since_npt;

    public InvoiceDto() {  }

    public InvoiceDto(Invoice invoice) {
        this.contract_id = invoice.getContract_id();
        this.ariba_doc_id = invoice.getAriba_doc_id();
        this.wellname = invoice.getWellname();
        this.afe = invoice.getAfe();
        this.wbs_element = invoice.getWbs_element();
        this.jobtyp = invoice.getJobtyp();
        this.npt_event_no = invoice.getNpt_event_no();
        this.parent_vendor = invoice.getParent_vendor();
        this.submit_date = invoice.getSubmit_date();
        this.price = invoice.getPrice();
        this.pt_spent_leakage_npt = invoice.getPt_spent_leakage_npt();
        this.npt_date_start = invoice.getNpt_date_start();
        this.npt_date_end = invoice.getNpt_date_end();
        this.npt_duration = invoice.getNptDuration();
        this.npt_ref_no = invoice.getNpt_ref_no();
        this.servicetyp = invoice.getServicetyp();
        this.npt_type_detail_desc = invoice.getNptTypeDetailDescription();
        this.npt_desc = invoice.getNptDescription();
        this.npt_type = invoice.getNptTypeTypeDetail(); // add column in spark!
        this.npt_type_detail = invoice.getNptTypeTypeDetail();
        this.contract_title = invoice.getContractTitle();
        this.work_start_date = invoice.getInvoiceStartDate();
        this.work_end_date = invoice.getInvoiceEndDate();
        this.contract_owner_name = invoice.getContract_owner_name() == null ? "-" : invoice.getContract_owner_name();
        this.by_date = invoice.getByDate();
        this.is_weather = invoice.getIswether();
        this.rig_no = invoice.getRigNo();

        setApproved_date(invoice.getApprovedDate());

        this.id = invoice.getId();
        InvoiceWriteback invoiceWriteback = invoice.getInvoiceWriteback();
        if(invoiceWriteback != null) {
            this.recovered = invoiceWriteback.getRecovered();
            this.status = invoiceWriteback.getInvoiceStatus();
            this.spent_leakage_confirmed = invoiceWriteback.getSpent_leakage_confirmed();
            this.comment = invoiceWriteback.getComment() == null ? "" : invoiceWriteback.getComment();
            this.modified_by = invoiceWriteback.getModifyed_by()== null ? "" : invoiceWriteback.getModifyed_by();
            this.modified_date = invoiceWriteback.getModifyed_date() == null ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(invoiceWriteback.getModifyed_date());
        } else {
            this.status = InvoiceWriteback.Status.BLANK.toString();
            this.spent_leakage_confirmed = null;
            this.recovered = null;
            this.comment = "";
            this.modified_by = "";
            this.modified_date = "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate npt_date_end_parsed = LocalDate.parse(npt_date_end, formatter);
        LocalDate today = LocalDate.now();
        this.days_since_npt = DAYS.between(npt_date_end_parsed,today);
    }
    
    public InvoiceDto(Invoice2 invoice) {
        this.contract_id = invoice.getContract_id();
        this.ariba_doc_id = invoice.getAriba_doc_id();
        this.wellname = invoice.getWellname();
        this.afe = invoice.getAfe();
        this.wbs_element = invoice.getWbs_element();
        this.jobtyp = invoice.getJobtyp();
        this.npt_event_no = invoice.getNpt_event_no();
        this.parent_vendor = invoice.getParent_vendor();
        this.submit_date = invoice.getSubmit_date();
        this.price = invoice.getPrice();
        this.pt_spent_leakage_npt = invoice.getPt_spent_leakage_npt();
        this.npt_date_start = invoice.getNpt_date_start();
        this.npt_date_end = invoice.getNpt_date_end();
        this.npt_duration = invoice.getNptDuration();
        this.npt_ref_no = invoice.getNpt_ref_no();
        this.servicetyp = invoice.getServicetyp();
        this.npt_type_detail_desc = invoice.getNptTypeDetailDescription();
        this.npt_desc = invoice.getNptDescription();
        this.npt_type = invoice.getNptTypeTypeDetail(); // add column in spark!
        this.npt_type_detail = invoice.getNptTypeTypeDetail();
        this.contract_title = invoice.getContractTitle();
        this.work_start_date = invoice.getInvoiceStartDate();
        this.work_end_date = invoice.getInvoiceEndDate();
        this.contract_owner_name = invoice.getContract_owner_name() == null ? "-" : invoice.getContract_owner_name();
        this.by_date = invoice.getByDate();
        this.is_weather = invoice.getIswether();

        setApproved_date(invoice.getApprovedDate());

        this.id = invoice.getId();
		this.recovered = invoice.getRecovered();
		this.status = invoice.getInvoiceStatus() == null ? InvoiceWriteback.Status.BLANK.toString() : invoice.getInvoiceStatus();
		this.spent_leakage_confirmed = invoice.getSpent_leakage_confirmed();
		this.comment = invoice.getComment() == null ? "" : invoice.getComment();
		this.modified_by = invoice.getModifyed_by()== null ? "" : invoice.getModifyed_by();
		this.modified_date = invoice.getModifyed_date() == null ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(invoice.getModifyed_date());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate npt_date_end_parsed = LocalDate.parse(npt_date_end, formatter);
        LocalDate today = LocalDate.now();
        this.days_since_npt = DAYS.between(npt_date_end_parsed,today);
    }

    public Boolean getBy_date() {
		return by_date;
	}

	public void setBy_date(Boolean by_date) {
		this.by_date = by_date;
	}

	public Boolean getIs_weather() {
		return is_weather;
	}

	public void setIs_weather(Boolean is_weather) {
		this.is_weather = is_weather;
	}

	public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getJobtyp() {
        return jobtyp;
    }

    public void setJobtyp(String jobtyp) {
        this.jobtyp = jobtyp;
    }

    public String getNpt_date_start() {
        return npt_date_start;
    }

    public void setNpt_date_start(String npt_date_start) {
        this.npt_date_start = npt_date_start;
    }

    public String getNpt_date_end() {
        return npt_date_end;
    }

    public void setNpt_date_end(String npt_date_end) {
        this.npt_date_end = npt_date_end;
    }

    public Float getNpt_duration() {
        return npt_duration;
    }

    public void setNpt_duration(Float npt_duration) {
        this.npt_duration = npt_duration;
    }

    public Float getSpent_leakage_confirmed() {
        return spent_leakage_confirmed;
    }

    public void setSpent_leakage_confirmed(Float spent_leakage_confirmed) {
        this.spent_leakage_confirmed = spent_leakage_confirmed;
    }

    public String getAriba_doc_id() {
        return ariba_doc_id;
    }

    public void setAriba_doc_id(String ariba_doc_id) {
        this.ariba_doc_id = ariba_doc_id;
    }

    public String getContract_id() {
        return contract_id;
    }

    public void setContract_id(String contract_id) {
        this.contract_id = contract_id;
    }

    public String getWellname() {
        return wellname;
    }

    public void setWellname(String wellname) {
        this.wellname = wellname;
    }

    public String getAfe() {
        return afe;
    }

    public void setAfe(String afe) {
        this.afe = afe;
    }

    public String getNpt_event_no() {
        return npt_event_no;
    }

    public void setNpt_event_no(String npt_event_no) {
        this.npt_event_no = npt_event_no;
    }

    public String getParent_vendor() {
        return parent_vendor;
    }

    public void setParent_vendor(String parent_vendor) {
        this.parent_vendor = parent_vendor;
    }
    
    public String getParent_vendor_html() {
        return replaceXMLSpecialChars(parent_vendor);
    }

    public String getSubmit_date() {
        return submit_date;
    }

    public void setSubmit_date(String submit_date) {
        this.submit_date = submit_date;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getPt_spent_leakage_npt() {
        return pt_spent_leakage_npt;
    }

    public void setPt_spent_leakage_npt(Float pt_spent_leakage_npt) {
        this.pt_spent_leakage_npt = pt_spent_leakage_npt;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Float getRecovered() {
        return recovered;
    }

    public void setRecovered(Float recovered) {
        this.recovered = recovered;
    }

    public String getNpt_ref_no() {
        return npt_ref_no;
    }

    public void setNpt_ref_no(String npt_ref_no) {
        this.npt_ref_no = npt_ref_no;
    }

    public String getServicetyp() {
        return servicetyp;
    }

    public void setServicetyp(String servicetyp) {
        this.servicetyp = servicetyp;
    }

    public String getNpt_type_detail_desc() {
        return npt_type_detail_desc;
    }

    public void setNpt_type_detail_desc(String npt_type_detail_desc) {
        this.npt_type_detail_desc = npt_type_detail_desc;
    }

    public String getNpt_desc() {
        return npt_desc;
    }

    public void setNpt_desc(String npt_desc) {
        this.npt_desc = npt_desc;
    }

    public String getNpt_type() {
        return npt_type;
    }

    public void setNpt_type(String npt_type) {
        this.npt_type = npt_type;
    }

    public String getNpt_type_detail() {
        return npt_type_detail;
    }

    public void setNpt_type_detail(String npt_type_detail) {
        this.npt_type_detail = npt_type_detail;
    }

    public String getContract_title() {
        return contract_title;
    }

    public void setContract_title(String contract_title) {
        this.contract_title = contract_title;
    }

    public String getWork_start_date() {
        return work_start_date;
    }

    public void setWork_start_date(String work_start_date) {
        this.work_start_date = work_start_date;
    }

    public String getWork_end_date() {
        return work_end_date;
    }

    public void setWork_end_date(String work_end_date) {
        this.work_end_date = work_end_date;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public Date getApproved_date() {
        return approved_date;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setApproved_date(Date approved_date) {
        this.approved_date = approved_date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModified_by() {
        return modified_by;
    }

    public void setModified_by(String modified_by) {
        this.modified_by = modified_by;
    }

    public String getWbs_element() {
        return wbs_element;
    }

    public void setWbs_element(String wbs_element) {
        this.wbs_element = wbs_element;
    }

    public String getSupplier_name_without() {
        return supplier_name_without;
    }

    public void setSupplier_name_without(String supplier_name_without) {
        this.supplier_name_without = supplier_name_without;
    }

    public String getContract_owner_name() {
        return contract_owner_name;
    }

    public void setContract_owner_name(String contract_owner_name) {
        this.contract_owner_name = contract_owner_name;
    }

	public String getRig_no() {
		return rig_no;
	}

	public void setRig_no(String rig_no) {
		this.rig_no = rig_no;
	}
}
