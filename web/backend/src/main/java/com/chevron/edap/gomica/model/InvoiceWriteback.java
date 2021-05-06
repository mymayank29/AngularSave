package com.chevron.edap.gomica.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity(name = "InvoiceWriteback")
@Table(name = "writeback_invoice_spent_leakage", schema = "gomica")
public class InvoiceWriteback {
    public enum Status {
        NPT_OUT_OF_SCOPE("NPT Out of Scope"),
        SUPPLIER_REVIEW("Supplier Review"),
        FLAGGED_FOR_REVIEW("Flagged for Review"),
        BLANK("Not Reviewed");

        String stringValue;

        Status(String stringValue){
            this.stringValue = stringValue;
        }

        public String toString(){
            return stringValue;
        }
    }

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "invoiceStatus")
    private String invoiceStatus = Status.BLANK.toString();

    @Column(name = "comment")
    private String comment = "";

    @Column(name = "modifyed_by")
    private String modifyed_by = "";

    @Column(name = "modifyed_date")
    private Timestamp modifyed_date;

    @Column(name = "spent_leakage_confirmed")
    private Float spent_leakage_confirmed;

    @Column(name = "recovered")
    private Float recovered;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "id", nullable = false)
    @MapsId
    private Invoice invoice;


    public InvoiceWriteback() {

    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getModifyed_by() {
        return modifyed_by;
    }

    public void setModifyed_by(String modifyed_by) {
        this.modifyed_by = modifyed_by;
    }

    public Timestamp getModifyed_date() {
        return modifyed_date;
    }

    public void setModifyed_date(Timestamp modifyed_date) {
        this.modifyed_date = modifyed_date;
    }

    public Float getSpent_leakage_confirmed() {
        return spent_leakage_confirmed;
    }

    public void setSpent_leakage_confirmed(Float spent_leakage_confirmed) {
        this.spent_leakage_confirmed = spent_leakage_confirmed;
    }

    public Float getRecovered() {
        return recovered;
    }

    public void setRecovered(Float recovered) {
        this.recovered = recovered;
    }
}
