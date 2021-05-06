package com.chevron.edap.gomica.dto;

import com.chevron.edap.gomica.model.Jobintervalproblem;

import static com.chevron.edap.gomica.util.StringEscapeTool.replaceXMLSpecialChars;
import java.sql.Date;

public class JobintervalproblemDto {
    public String idrec;
    public String idrecparent;
    public String refno;
    public String status;
    public String typ;
    public String typdetail;
    public Date dttmstartdate;
    public String depthstart;
    public String accountablepty;
    public Date dttmenddate;
    public String depthend;
    public String durationnetcalc;
    public String estcostoverride;
    public String des;
    public String com;

    public JobintervalproblemDto() {  }

    public JobintervalproblemDto(Jobintervalproblem wvjobintervalproblem) {
        this.idrec = replaceXMLSpecialChars(wvjobintervalproblem.getIdrec());
        this.idrecparent = replaceXMLSpecialChars(wvjobintervalproblem.getIdrecparent());
        this.refno = replaceXMLSpecialChars(wvjobintervalproblem.getRefno());
        this.status = replaceXMLSpecialChars(wvjobintervalproblem.getStatus());
        this.typ = replaceXMLSpecialChars(wvjobintervalproblem.getTyp());
        this.typdetail = replaceXMLSpecialChars(wvjobintervalproblem.getTypdetail());
        this.dttmstartdate = wvjobintervalproblem.getDttmstartdate();
        this.dttmenddate = wvjobintervalproblem.getDttmenddate();
        this.depthstart = replaceXMLSpecialChars(wvjobintervalproblem.getDepthstart());
        this.accountablepty = replaceXMLSpecialChars(wvjobintervalproblem.getAccountablepty());
        this.depthend = replaceXMLSpecialChars(wvjobintervalproblem.getDepthend());
        this.durationnetcalc = replaceXMLSpecialChars(wvjobintervalproblem.getDurationnetcalc());
        this.estcostoverride = replaceXMLSpecialChars(wvjobintervalproblem.getEstcostoverride());
        this.des = replaceXMLSpecialChars(wvjobintervalproblem.getDes());
        this.com = replaceXMLSpecialChars(wvjobintervalproblem.getCom());
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

    public String getRefno() {
        return refno;
    }

    public void setRefno(String refno) {
        this.refno = refno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getTypdetail() {
        return typdetail;
    }

    public void setTypdetail(String typdetail) {
        this.typdetail = typdetail;
    }

    public Date getDttmstartdate() {
        return dttmstartdate;
    }

    public void setDttmstartdate(Date dttmstartdate) {
        this.dttmstartdate = dttmstartdate;
    }

    public String getDepthstart() {
        return depthstart;
    }

    public void setDepthstart(String depthstart) {
        this.depthstart = depthstart;
    }

    public String getAccountablepty() {
        return accountablepty;
    }

    public void setAccountablepty(String accountablepty) {
        this.accountablepty = accountablepty;
    }

    public Date getDttmenddate() {
        return dttmenddate;
    }

    public void setDttmenddate(Date dttmenddate) {
        this.dttmenddate = dttmenddate;
    }

    public String getDepthend() {
        return depthend;
    }

    public void setDepthend(String depthend) {
        this.depthend = depthend;
    }

    public String getDurationnetcalc() {
        return durationnetcalc;
    }

    public void setDurationnetcalc(String durationnetcalc) {
        this.durationnetcalc = durationnetcalc;
    }

    public String getEstcostoverride() {
        return estcostoverride;
    }

    public void setEstcostoverride(String estcostoverride) {
        this.estcostoverride = estcostoverride;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

	public String getCom() {
		return com;
	}

	public void setCom(String com) {
		this.com = com;
	}
	
}