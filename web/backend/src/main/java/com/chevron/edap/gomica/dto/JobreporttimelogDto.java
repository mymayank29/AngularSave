package com.chevron.edap.gomica.dto;

import static com.chevron.edap.gomica.util.StringEscapeTool.replaceXMLSpecialChars;

import com.chevron.edap.gomica.model.Jobreporttimelog;
import com.chevron.edap.gomica.util.UnixDateTimeConverter;

public class JobreporttimelogDto {
    public String idrec;
    public String idrecparent;
    public String dttmstartcalc;
    public String durationnoprobtimecalc;
    public String phase;
    public String code1;
    public String com;
    public String refnoproblemcalc;
    public String durationproblemtimecalc;

    public JobreporttimelogDto() {  }

    public JobreporttimelogDto(Jobreporttimelog wvjobreporttimelog) {
        this.idrec = replaceXMLSpecialChars(wvjobreporttimelog.getIdrec());
        this.idrecparent = replaceXMLSpecialChars(wvjobreporttimelog.getIdrecparent());
        this.dttmstartcalc = replaceXMLSpecialChars(wvjobreporttimelog.getDttmstartcalc());
        this.durationnoprobtimecalc = replaceXMLSpecialChars(wvjobreporttimelog.getDurationnoprobtimecalc());
        this.phase = replaceXMLSpecialChars(wvjobreporttimelog.getPhase());
        this.code1 = replaceXMLSpecialChars(wvjobreporttimelog.getCode1());
        this.com = replaceXMLSpecialChars(wvjobreporttimelog.getCom());
        this.refnoproblemcalc = replaceXMLSpecialChars(wvjobreporttimelog.getRefnoproblemcalc());
        this.durationproblemtimecalc = replaceXMLSpecialChars(wvjobreporttimelog.getDurationproblemtimecalc());
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

    public String getDttmstartcalc() {
        return dttmstartcalc;
    }

    public void setDttmstartcalc(String dttmstartcalc) {
        this.dttmstartcalc = dttmstartcalc;
    }
    
    public String getDttmstartcalcdate() {
    	return UnixDateTimeConverter.epochMilliToDateTimeString(getDttmstartcalc());
    }

    public String getDurationnoprobtimecalc() {
        return durationnoprobtimecalc;
    }

    public void setDurationnoprobtimecalc(String durationnoprobtimecalc) {
        this.durationnoprobtimecalc = durationnoprobtimecalc;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getCode1() {
        return code1;
    }

    public void setCode1(String code1) {
        this.code1 = code1;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getRefnoproblemcalc() {
        return refnoproblemcalc;
    }

    public void setRefnoproblemcalc(String refnoproblemcalc) {
        this.refnoproblemcalc = refnoproblemcalc;
    }

    public String getDurationproblemtimecalc() {
        return durationproblemtimecalc;
    }

    public void setDurationproblemtimecalc(String durationproblemtimecalc) {
        this.durationproblemtimecalc = durationproblemtimecalc;
    }
    
    public String getFullDuration() {
    	if (this.durationproblemtimecalc != null) {
    		Float totalDuration = Float.parseFloat(this.durationproblemtimecalc) + Float.parseFloat(this.durationnoprobtimecalc);
    		return totalDuration.toString().replaceAll("\\.?0*$", "");
    	}
    	
    	return this.durationnoprobtimecalc;
    }
}