package com.chevron.edap.gomica.dto;

import com.chevron.edap.gomica.model.Jobsafetychk;

import java.sql.Date;

import static com.chevron.edap.gomica.util.StringEscapeTool.replaceXMLSpecialChars;


public class JobsafetychkDto {
    public String idrec;
    public String idrecparent;
    public String des;
    public String typ;
    public String inspector;
    public String result;
    public String com;
    public Date dttmdate;

    public JobsafetychkDto() {  }

    public JobsafetychkDto(Jobsafetychk wvjobsafetychk) {
        this.idrec = replaceXMLSpecialChars(wvjobsafetychk.getIdrec());
        this.idrecparent = replaceXMLSpecialChars(wvjobsafetychk.getIdrecparent());
        this.des = replaceXMLSpecialChars(wvjobsafetychk.getDes());
        this.typ = replaceXMLSpecialChars(wvjobsafetychk.getTyp());
        this.inspector = replaceXMLSpecialChars(wvjobsafetychk.getInspector());
        this.result = replaceXMLSpecialChars(wvjobsafetychk.getResult());
        this.com = replaceXMLSpecialChars(wvjobsafetychk.getCom());
        this.dttmdate = wvjobsafetychk.getDttmdate();
    }

    public String getIdrec() {
        return idrec;
    }

    public void setIdrec(String idrec) {
        this.idrec = idrec;
    }

    public String getIdrecparent() {
        return idrecparent;
    }

    public void setIdrecparent(String idrecparent) {
        this.idrecparent = idrecparent;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getInspector() {
        return inspector;
    }

    public void setInspector(String inspector) {
        this.inspector = inspector;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }
}