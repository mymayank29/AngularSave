package com.chevron.edap.gomica.dto;

import com.chevron.edap.gomica.model.Wellheader;

import static com.chevron.edap.gomica.util.StringEscapeTool.replaceXMLSpecialChars;

public class WellheaderDto {
    public String idwell;
    public String wellname;
    public String wellidd;
    public String lease;
    public String fieldname;
    public String division;
    public String stateprov;
    public String wellida;
    public String wellidc;
    public String elvorigkb;
    public String waterdepth;
    public String kbtomudcalc;
    public String kbtocascalc;
    public String kbtotubcalc;

    public WellheaderDto() {  }

    public WellheaderDto(Wellheader wvwellheader) {
        this.idwell = replaceXMLSpecialChars(wvwellheader.getIdwell());
        this.wellname = replaceXMLSpecialChars(wvwellheader.getWellname());
        this.wellidd = replaceXMLSpecialChars(wvwellheader.getWellidd());
        this.lease = replaceXMLSpecialChars(wvwellheader.getLease());
        this.fieldname = replaceXMLSpecialChars(wvwellheader.getFieldname());
        this.division = replaceXMLSpecialChars(wvwellheader.getDivision());
        this.stateprov = replaceXMLSpecialChars(wvwellheader.getStateprov());
        this.wellida = replaceXMLSpecialChars(wvwellheader.getWellida());
        this.wellidc = replaceXMLSpecialChars(wvwellheader.getWellidc());
        this.elvorigkb = replaceXMLSpecialChars(wvwellheader.getElvorigkb());
        this.waterdepth = replaceXMLSpecialChars(wvwellheader.getWaterdepth());
        this.kbtomudcalc = replaceXMLSpecialChars(wvwellheader.getKbtomudcalc());
        this.kbtocascalc = replaceXMLSpecialChars(wvwellheader.getKbtocascalc());
        this.kbtotubcalc = replaceXMLSpecialChars(wvwellheader.getKbtotubcalc());
    }

    public String getIdwell() {
        return idwell;
    }

    public void setIdwell(String idwell) {
        this.idwell = idwell;
    }

    public String getWellname() {
        return wellname;
    }

    public void setWellname(String wellname) {
        this.wellname = wellname;
    }

    public String getWellidd() {
        return wellidd;
    }

    public void setWellidd(String wellidd) {
        this.wellidd = wellidd;
    }

    public String getLease() {
        return lease;
    }

    public void setLease(String lease) {
        this.lease = lease;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getStateprov() {
        return stateprov;
    }

    public void setStateprov(String stateprov) {
        this.stateprov = stateprov;
    }

    public String getWellida() {
        return wellida;
    }

    public void setWellida(String wellida) {
        this.wellida = wellida;
    }

    public String getWellidc() {
        return wellidc;
    }

    public void setWellidc(String wellidc) {
        this.wellidc = wellidc;
    }

    public String getElvorigkb() {
        return elvorigkb;
    }

    public void setElvorigkb(String elvorigkb) {
        this.elvorigkb = elvorigkb;
    }

    public String getWaterdepth() {
        return waterdepth;
    }

    public void setWaterdepth(String waterdepth) {
        this.waterdepth = waterdepth;
    }

    public String getKbtomudcalc() {
        return kbtomudcalc;
    }

    public void setKbtomudcalc(String kbtomudcalc) {
        this.kbtomudcalc = kbtomudcalc;
    }

    public String getKbtocascalc() {
        return kbtocascalc;
    }

    public void setKbtocascalc(String kbtocascalc) {
        this.kbtocascalc = kbtocascalc;
    }

    public String getKbtotubcalc() {
        return kbtotubcalc;
    }

    public void setKbtotubcalc(String kbtotubcalc) {
        this.kbtotubcalc = kbtotubcalc;
    }

    @Override
    public String toString() {
        return "Wellheader{" +
                "idwell='" + idwell + '\'' +
                ", wellname='" + wellname + '\'' +
                ", wellidd='" + wellidd + '\'' +
                ", lease='" + lease + '\'' +
                ", fieldname='" + fieldname + '\'' +
                ", division='" + division + '\'' +
                ", stateprov='" + stateprov + '\'' +
                ", wellida='" + wellida + '\'' +
                ", wellidc='" + wellidc + '\'' +
                ", elvorigkb='" + elvorigkb + '\'' +
                ", waterdepth='" + waterdepth + '\'' +
                ", kbtomudcalc='" + kbtomudcalc + '\'' +
                ", kbtocascalc='" + kbtocascalc + '\'' +
                ", kbtotubcalc='" + kbtotubcalc + '\'' +
                '}';
    }
}