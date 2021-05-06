package com.chevron.edap.gomica.dto;


public class LineItemDto {


    private String id;
    private String part_number;
    private String ariba_doc_id;
    private String description;
    private Float unit_price_llf;
    private Float quantity_llf;
    private Float discount;
    private Float invoice_net_amount_usd;
    private String end_date;
    private String start_date;
    private Long duration;
    private String invoice_no;
    private String contract_title;


    public LineItemDto() {  }

    public String getContract_title() {
		return contract_title;
	}

	public void setContract_title(String contract_title) {
		this.contract_title = contract_title;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPart_number() {
        return part_number;
    }

    public void setPart_number(String part_number) {
        this.part_number = part_number;
    }

    public String getAriba_doc_id() {
        return ariba_doc_id;
    }

    public void setAriba_doc_id(String ariba_doc_id) {
        this.ariba_doc_id = ariba_doc_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getUnit_price_llf() {
        return unit_price_llf;
    }

    public void setUnit_price_llf(Float unit_price_llf) {
        this.unit_price_llf = unit_price_llf;
    }

    public Float getQuantity_llf() {
        return quantity_llf;
    }

    public void setQuantity_llf(Float quantity_llf) {
        this.quantity_llf = quantity_llf;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public Float getInvoice_net_amount_usd() {
        return invoice_net_amount_usd;
    }

    public void setInvoice_net_amount_usd(Float invoice_net_amount_usd) {
        this.invoice_net_amount_usd = invoice_net_amount_usd;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }
}
