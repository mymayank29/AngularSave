package com.chevron.edap.gomica.model;

import com.chevron.edap.gomica.util.CustomDateDeserializer;
import com.chevron.edap.gomica.util.CustomDateSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Date;

@Entity(name = "Invoice")
@Table(name = "invoice_for_full_sf", schema = "gomica")
public class Invoice {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "ariba_doc_id")
    private String ariba_doc_id;

    @Column(name = "contract_id")
    private String contract_id;

    @Column(name = "wellname")
    private String wellname;

    @Column(name = "supplier_name_without")
    private String supplier_name_without;

    @Column(name = "afe")
    private String afe;

    @Column(name = "wbs_element")
    private String wbs_element;

    @Column(name = "jobtyp")
    private String jobtyp;

    @Column(name = "npt_event_no")
    private String npt_event_no;

    @Column(name = "parent_vendor")
    private String parent_vendor;

    @Column(name = "submit_date")
    private String submit_date;

    @Column(name = "price")
    private Float price;

    @Column(name = "pt_spent_leakage_npt")
    private Float pt_spent_leakage_npt;

    @Column(name = "npt_date_start")
    private String npt_date_start;

    @Column(name = "npt_date_end")
    private String npt_date_end;

    @Column(name = "npt_duration")
    private Float nptDuration;

    @Column(name = "refno")
    private String npt_ref_no;

    @Column(name = "contract_owner_name")
    private String contract_owner_name;

    @Column(name = "iswether")
    private Boolean iswether;

    @Column(name = "bydate")
    private Boolean byDate;

    @Column(name = "bytitle")
    private Boolean byTitle;

    @Column(name = "contract_title")
    private String contractTitle;

    @Column(name = "inv_start_date") //work_start_date
    private String invoiceStartDate;

    @Column(name = "inv_end_date") // work_end_date
    private String invoiceEndDate;

    @Column(name = "approved_date")
//    @JsonDeserialize(using = CustomDateDeserializer.class)
//    @JsonSerialize(using = CustomDateSerializer.class)
    private Date approvedDate;

    @Column(name = "npt_type_typedetail_concat")
    private String nptTypeTypeDetail;

    @Column(name = "servicetyp")
    private String servicetyp;

    @Column(name = "npt_type_detail_description")
    private String nptTypeDetailDescription;

    @Column(name = "npt_com") //com
    private String nptDescription;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "invoice")
    private InvoiceWriteback invoiceWriteback;
    
    @Column(name = "rigno")
    private String rigNo;
    
//    @OneToOne(cascade = CascadeType.ALL, mappedBy = "invoice")
//    private IntervalProblem intervalProblem;

    public Invoice() { }

    public String getRigNo() {
		return rigNo;
	}

	public void setRigNo(String rigNo) {
		this.rigNo = rigNo;
	}

	public String getContractTitle() {
        return contractTitle;
    }

    public void setContractTitle(String contractTitle) {
        this.contractTitle = contractTitle;
    }

    public String getInvoiceStartDate() {
        return invoiceStartDate;
    }

    public void setInvoiceStartDate(String invoiceStartDate) {
        this.invoiceStartDate = invoiceStartDate;
    }

    public String getInvoiceEndDate() {
        return invoiceEndDate;
    }

    public void setInvoiceEndDate(String invoiceEndDate) {
        this.invoiceEndDate = invoiceEndDate;
    }

//    @JsonSerialize(using = CustomDateSerializer.class)
    public Date getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getNptTypeTypeDetail() {
        return nptTypeTypeDetail;
    }

    public void setNptTypeTypeDetail(String nptTypeTypeDetail) {
        this.nptTypeTypeDetail = nptTypeTypeDetail;
    }

    public String getServicetyp() {
        return servicetyp;
    }

    public void setServicetyp(String servicetyp) {
        this.servicetyp = servicetyp;
    }

    public String getNptTypeDetailDescription() {
        return nptTypeDetailDescription;
    }

    public void setNptTypeDetailDescription(String nptTypeDetailDescription) {
        this.nptTypeDetailDescription = nptTypeDetailDescription;
    }

    public String getNptDescription() {
        return nptDescription;
    }

    public void setNptDescription(String nptDescription) {
        this.nptDescription = nptDescription;
    }

    public InvoiceWriteback getInvoiceWriteback() {
        return invoiceWriteback;
    }

    public void setInvoiceWriteback(InvoiceWriteback invoiceWriteback) {
        this.invoiceWriteback = invoiceWriteback;
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

    public Float getNptDuration() {
        return nptDuration;
    }

    public void setNptDuration(Float nptDuration) {
        this.nptDuration = nptDuration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIswether() {
        return iswether;
    }

    public void setIswether(Boolean iswether) {
        this.iswether = iswether;
    }

    public String getSubmit_date() {
        return submit_date;
    }

    public void setSubmit_date(String submit_date) {
        this.submit_date = submit_date;
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

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getNpt_ref_no() {
        return npt_ref_no;
    }

    public void setNpt_ref_no(String npt_ref_no) {
        this.npt_ref_no = npt_ref_no;
    }

    public Float getPt_spent_leakage_npt() {
        return pt_spent_leakage_npt;
    }

    public void setPt_spent_leakage_npt(Float pt_spent_leakage_npt) {
        this.pt_spent_leakage_npt = pt_spent_leakage_npt;
    }

    public Boolean getByTitle() {
        return byTitle;
    }

    public void setByTitle(Boolean byTitle) {
        this.byTitle = byTitle;
    }

    public Boolean getByDate() {
        return byDate;
    }

    public void setByDate(Boolean byDate) {
        this.byDate = byDate;
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

	@Override
	public String toString() {
		return "Invoice [id=" + id + ", ariba_doc_id=" + ariba_doc_id + ", contract_id=" + contract_id + ", wellname="
				+ wellname + ", supplier_name_without=" + supplier_name_without + ", afe=" + afe + ", wbs_element="
				+ wbs_element + ", jobtyp=" + jobtyp + ", npt_event_no=" + npt_event_no + ", parent_vendor="
				+ parent_vendor + ", submit_date=" + submit_date + ", price=" + price + ", pt_spent_leakage_npt="
				+ pt_spent_leakage_npt + ", npt_date_start=" + npt_date_start + ", npt_date_end=" + npt_date_end
				+ ", nptDuration=" + nptDuration + ", npt_ref_no=" + npt_ref_no + ", contract_owner_name="
				+ contract_owner_name + ", iswether=" + iswether + ", byDate=" + byDate + ", byTitle=" + byTitle
				+ ", contractTitle=" + contractTitle + ", invoiceStartDate=" + invoiceStartDate + ", invoiceEndDate="
				+ invoiceEndDate + ", approvedDate=" + approvedDate + ", nptTypeTypeDetail=" + nptTypeTypeDetail
				+ ", servicetyp=" + servicetyp + ", nptTypeDetailDescription=" + nptTypeDetailDescription
				+ ", nptDescription=" + nptDescription + ", invoiceWriteback=" + invoiceWriteback + /*", intervalProblem(refNo)="
				+ intervalProblem.getRefNo() + */"]";
	}
    
    

//    @Override
//    public String toString() {
//        return "Invoice{" +
//                "id=" + id +
//                ", ariba_doc_id='" + ariba_doc_id + '\'' +
//                ", contract_id='" + contract_id + '\'' +
//                ", wellname='" + wellname + '\'' +
//                ", afe='" + afe + '\'' +
//                ", jobtyp='" + jobtyp + '\'' +
//                ", npt_event_no='" + npt_event_no + '\'' +
//                ", parent_vendor='" + parent_vendor + '\'' +
//                ", submit_date='" + submit_date + '\'' +
//                ", price=" + price +
//                ", pt_spent_leakage_npt=" + pt_spent_leakage_npt +
//                ", npt_date_start='" + npt_date_start + '\'' +
//                ", npt_date_end='" + npt_date_end + '\'' +
//                ", nptDuration=" + nptDuration +
//                ", npt_ref_no='" + npt_ref_no + '\'' +
//                ", iswether=" + iswether +
//                ", byDate=" + byDate +
//                ", byTitle=" + byTitle +
//                ", contractTitle='" + contractTitle + '\'' +
//                ", invoiceStartDate='" + invoiceStartDate + '\'' +
//                ", invoiceEndDate='" + invoiceEndDate + '\'' +
//                ", approvedDate=" + approvedDate +
//                ", nptTypeTypeDetail='" + nptTypeTypeDetail + '\'' +
//                ", servicetyp='" + servicetyp + '\'' +
//                ", nptTypeDetailDescription='" + nptTypeDetailDescription + '\'' +
//                ", nptDescription='" + nptDescription + '\'' +
//                ", invoiceWriteback=" + invoiceWriteback +
//                '}';
//    }
}
