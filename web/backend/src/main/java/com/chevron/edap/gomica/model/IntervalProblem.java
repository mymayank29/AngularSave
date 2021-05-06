package com.chevron.edap.gomica.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity(name = "IntervalProblem")
@Table(name = "interval_problem_full_with_link_sf", schema = "gomica")
public class IntervalProblem {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "ariba_doc_id")
    private String aribaDocId;

    @Column(name = "npt_event_no")
    private String npt_event_no;

    @Column(name = "typ")
    private String typ;

    @Column(name = "npt_date_start")
    private String npt_date_start;

    @Column(name = "npt_date_end")
    private String npt_date_end;

    @Column(name = "duration")
    private Float duration;

    @Column(name = "npt_type_detail_description")
    private String npt_type_detail_description;

    @Column(name = "com")
    private String com;
    
    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "id", nullable = false)
    @MapsId
    private Invoice invoice;

    public IntervalProblem() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAribaDocId() {
        return aribaDocId;
    }

    public void setAribaDocId(String aribaDocId) {
        this.aribaDocId = aribaDocId;
    }

    public String getNpt_event_no() {
        return npt_event_no;
    }

    public void setNpt_event_no(String npt_event_no) {
        this.npt_event_no = npt_event_no;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
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

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public String getNpt_type_detail_description() {
        return npt_type_detail_description;
    }

    public void setNpt_type_detail_description(String npt_type_detail_description) {
        this.npt_type_detail_description = npt_type_detail_description;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
	public String getRefNo() {
		return invoice.getNpt_ref_no();
	}
    
    
}
