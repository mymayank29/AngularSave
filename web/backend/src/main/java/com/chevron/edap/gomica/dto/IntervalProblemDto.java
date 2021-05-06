package com.chevron.edap.gomica.dto;

import com.chevron.edap.gomica.model.IntervalProblem;

public class IntervalProblemDto {

    private String id;
    private String npt_event_no;
    private String typ;
    private String npt_date_start;
    private String npt_date_end;
    private Float duration;
    private String npt_type_detail_description;
    private String com;
    private String invoice_no;
    public String ariba_doc_id;
    private String ref_no;
    private String rig_no;
    private String well_name;
    private String service_typ;
    private Boolean by_date;
    private Boolean is_weather;

    public IntervalProblemDto() {  }

    public IntervalProblemDto(IntervalProblem intervalProblem) {
        this.id = intervalProblem.getId();
        this.npt_event_no = intervalProblem.getNpt_event_no();
        this.typ = intervalProblem.getTyp();
        this.npt_date_start = intervalProblem.getNpt_date_start();
        this.npt_date_end = intervalProblem.getNpt_date_end();
        this.duration = intervalProblem.getDuration();
        this.npt_type_detail_description = intervalProblem.getNpt_type_detail_description();
        this.com = intervalProblem.getCom();
        this.invoice_no = intervalProblem.getAribaDocId();
        this.ariba_doc_id = intervalProblem.getAribaDocId();
        this.ref_no = intervalProblem.getInvoice().getNpt_ref_no();
        this.rig_no = intervalProblem.getInvoice().getRigNo();
        this.well_name = intervalProblem.getInvoice().getWellname();
        this.service_typ = intervalProblem.getInvoice().getServicetyp();
        this.by_date = intervalProblem.getInvoice().getByDate();
        this.is_weather = intervalProblem.getInvoice().getIswether();
    }

	public Boolean getBy_date() {
		return by_date;
	}

	public Boolean getIs_weather() {
		return is_weather;
	}
}
