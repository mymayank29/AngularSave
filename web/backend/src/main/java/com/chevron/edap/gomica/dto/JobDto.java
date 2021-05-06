package com.chevron.edap.gomica.dto;

import com.chevron.edap.gomica.model.Job;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.chevron.edap.gomica.util.StringEscapeTool.replaceXMLSpecialChars;

public class JobDto {
    public String idrec;
    public String idwell;
    public String wvtyp;
    public String jobtyp;
    public String dttmstart;
    public String dttmend;

    public JobDto() {  }

    public JobDto(Job wvjob) {
        this.idrec = replaceXMLSpecialChars(wvjob.getIdrec());
        this.idwell = replaceXMLSpecialChars(wvjob.getIdwell());
        this.wvtyp = replaceXMLSpecialChars(wvjob.getWvtyp());
        this.jobtyp = replaceXMLSpecialChars(wvjob.getJobtyp());
            SimpleDateFormat dataFormater = new SimpleDateFormat("yyyy-MM-dd");
            this.dttmstart = wvjob.getDttmstart() == null ? null : replaceXMLSpecialChars(dataFormater.format(new Date(Long.parseLong(wvjob.getDttmstart()))));
            this.dttmend = wvjob.getDttmend() == null ? null : replaceXMLSpecialChars(dataFormater.format(new Date(Long.parseLong(wvjob.getDttmend()))));
    }

    public String getIdrec() {
        return idrec;
    }

    public void setIdrec(String idrec) {
        this.idrec = idrec;
    }

    public String getIdwell() {
        return idwell;
    }

    public void setIdwell(String idwell) {
        this.idwell = idwell;
    }

    public String getWvtyp() {
        return wvtyp;
    }

    public void setWvtyp(String wvtyp) {
        this.wvtyp = wvtyp;
    }

    public String getJobtyp() {
        return jobtyp;
    }

    public void setJobtyp(String jobtyp) {
        this.jobtyp = jobtyp;
    }

    public String getDttmstart() {
        return dttmstart;
    }

    public void setDttmstart(String dttmstart) {
        this.dttmstart = dttmstart;
    }

    public String getDttmend() {
        return dttmend;
    }

    public void setDttmend(String dttmend) {
        this.dttmend = dttmend;
    }
}