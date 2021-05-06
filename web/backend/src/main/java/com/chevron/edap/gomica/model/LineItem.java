package com.chevron.edap.gomica.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "LineItem")
@Table(name = "simsmart_vw_dwep_po_and_se_line_item", schema = "gomica")
public class LineItem {

    @Id
    @Column(name="business_key")
    private String business_key;

    @Column(name="ariba_doc_id")
    private String aribaDocId;

    @Column(name="part_number")
    private String part_number;

    @Column(name="description")
    private String description;

    @Column(name="unit_price_llf")
    private String unit_price_llf;

    @Column(name="quantity_llf")
    private String quantity_llf;

    @Column(name="discount")
    private String discount;

    @Column(name="invoice_net_amount_usd")
    private String invoice_net_amount_usd;

    @Column(name="end_date")
    private Integer end_date;

    @Column(name="start_date")
    private Integer start_date;

    @Column(name="contract_name")
    private String contractName;

    public LineItem() { }

    public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public String getBusiness_key() {
        return business_key;
    }

    public void setBusiness_key(String business_key) {
        this.business_key = business_key;
    }

    public String getPart_number() {
        return part_number;
    }

    public void setPart_number(String part_number) {
        this.part_number = part_number;
    }

    public String getAribaDocId() {
        return aribaDocId;
    }

    public void setAribaDocId(String aribaDocId) {
        this.aribaDocId = aribaDocId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit_price_llf() {
        return unit_price_llf;
    }

    public void setUnit_price_llf(String unit_price_llf) {
        this.unit_price_llf = unit_price_llf;
    }

    public String getQuantity_llf() {
        return quantity_llf;
    }

    public void setQuantity_llf(String quantity_llf) {
        this.quantity_llf = quantity_llf;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getInvoice_net_amount_usd() {
        return invoice_net_amount_usd;
    }

    public void setInvoice_net_amount_usd(String invoice_net_amount_usd) {
        this.invoice_net_amount_usd = invoice_net_amount_usd;
    }

    public Integer getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Integer end_date) {
        this.end_date = end_date;
    }

    public Integer getStart_date() {
        return start_date;
    }

    public void setStart_date(Integer start_date) {
        this.start_date = start_date;
    }
}
