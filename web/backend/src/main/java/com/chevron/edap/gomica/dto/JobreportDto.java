package com.chevron.edap.gomica.dto;

import static com.chevron.edap.gomica.util.StringEscapeTool.replaceXMLSpecialChars;

import java.sql.Date;
import java.text.SimpleDateFormat;

import com.chevron.edap.gomica.model.Jobreport;
import com.chevron.edap.gomica.util.UnixDateTimeConverter;


public class JobreportDto {
    public String idrec;
    public String idrecparent;
    public String idrecwellborecalc;
    public String dttmstart;
    public String reportnocalc;
    public String durationtimelogcumspudcalc;
    public String durationtimelogtotcumcalc;
    public String depthprogressdpcalc;
    public Date dttmstartdate;
    public String rpttmactops;
    public String usertxt1;
    public String durationsincerptinc;
    public String durationsinceltinc;
    public String rigtime;
    public String rigtimecumcalc;
    public String tmrotatingcalc;
    public String headcountcalc;
    public String durationpersonneltotcalc;
    public String summaryops;
    public String statusend;
    public String plannextrptops;
    public String remarks;
    public String gasbackgroundavg;
    public String gasconnectionavg;
    public String gasdrillavg;
    public String gastripavg;

    public JobreportDto() {  }

    public JobreportDto(Jobreport wvjobreport) {
        this.idrec = replaceXMLSpecialChars(wvjobreport.getIdrec());
        this.idrecparent = replaceXMLSpecialChars(wvjobreport.getIdrecparent());
        this.idrecwellborecalc = replaceXMLSpecialChars(wvjobreport.getIdrecwellborecalc());
        SimpleDateFormat dataFormater = new SimpleDateFormat("yyyy-MM-dd");
        this.dttmstart = wvjobreport.getDttmstart() == null ? null : replaceXMLSpecialChars(UnixDateTimeConverter.epochMilliToDateString(wvjobreport.getDttmstart()));
        this.reportnocalc = replaceXMLSpecialChars(wvjobreport.getReportnocalc());
        this.durationtimelogcumspudcalc = replaceXMLSpecialChars(wvjobreport.getDurationtimelogcumspudcalc());
        this.durationtimelogtotcumcalc = replaceXMLSpecialChars(wvjobreport.getDurationtimelogtotcumcalc());
        this.depthprogressdpcalc = replaceXMLSpecialChars(wvjobreport.getDepthprogressdpcalc());
        this.dttmstartdate = wvjobreport.getDttmstartdate();

        this.rpttmactops = replaceXMLSpecialChars(wvjobreport.getRpttmactops());
        this.usertxt1 = replaceXMLSpecialChars(wvjobreport.getUsertxt1());
        this.durationsincerptinc = replaceXMLSpecialChars(wvjobreport.getDurationsincerptinc());
        this.durationsinceltinc = replaceXMLSpecialChars(wvjobreport.getDurationsinceltinc());
        this.rigtime = replaceXMLSpecialChars(wvjobreport.getRigtime());
        this.rigtimecumcalc = replaceXMLSpecialChars(wvjobreport.getRigtimecumcalc());
        this.tmrotatingcalc = replaceXMLSpecialChars(wvjobreport.getTmrotatingcalc());
        this.headcountcalc = replaceXMLSpecialChars(wvjobreport.getHeadcountcalc());
        this.durationpersonneltotcalc = replaceXMLSpecialChars(wvjobreport.getDurationpersonneltotcalc());
        this.summaryops = replaceXMLSpecialChars(wvjobreport.getSummaryops());
        this.statusend = replaceXMLSpecialChars(wvjobreport.getStatusend());
        this.plannextrptops = replaceXMLSpecialChars(wvjobreport.getPlannextrptops());
        this.remarks = replaceXMLSpecialChars(wvjobreport.getRemarks());
        this.gasbackgroundavg = replaceXMLSpecialChars(wvjobreport.getGasbackgroundavg());
        this.gasconnectionavg = replaceXMLSpecialChars(wvjobreport.getGasconnectionavg());
        this.gasdrillavg = replaceXMLSpecialChars(wvjobreport.getGasdrillavg());
        this.gastripavg = replaceXMLSpecialChars(wvjobreport.getGastripavg());
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

    public String getIdrecwellborecalc() {
        return idrecwellborecalc;
    }

    public void setIdrecwellborecalc(String idrecwellborecalc) {
        this.idrecwellborecalc = idrecwellborecalc;
    }

    public String getDttmstart() {
        return dttmstart;
    }

    public void setDttmstart(String dttmstart) {
        this.dttmstart = dttmstart;
    }

    public String getReportnocalc() {
        return reportnocalc;
    }

    public void setReportnocalc(String reportnocalc) {
        this.reportnocalc = reportnocalc;
    }

    public String getDurationtimelogcumspudcalc() {
        return durationtimelogcumspudcalc;
    }

    public void setDurationtimelogcumspudcalc(String durationtimelogcumspudcalc) {
        this.durationtimelogcumspudcalc = durationtimelogcumspudcalc;
    }

    public String getDurationtimelogtotcumcalc() {
        return durationtimelogtotcumcalc;
    }

    public void setDurationtimelogtotcumcalc(String durationtimelogtotcumcalc) {
        this.durationtimelogtotcumcalc = durationtimelogtotcumcalc;
    }

    public String getDepthprogressdpcalc() {
        return depthprogressdpcalc;
    }

    public void setDepthprogressdpcalc(String depthprogressdpcalc) {
        this.depthprogressdpcalc = depthprogressdpcalc;
    }

    public Date getDttmstartdate() {
        return dttmstartdate;
    }

    public void setDttmstartdate(Date dttmstartdate) {
        this.dttmstartdate = dttmstartdate;
    }

    public String getRpttmactops() {
        return rpttmactops;
    }

    public void setRpttmactops(String rpttmactops) {
        this.rpttmactops = rpttmactops;
    }

    public String getUsertxt1() {
        return usertxt1;
    }

    public void setUsertxt1(String usertxt1) {
        this.usertxt1 = usertxt1;
    }

    public String getDurationsincerptinc() {
        return durationsincerptinc;
    }

    public void setDurationsincerptinc(String durationsincerptinc) {
        this.durationsincerptinc = durationsincerptinc;
    }

    public String getDurationsinceltinc() {
        return durationsinceltinc;
    }

    public void setDurationsinceltinc(String durationsinceltinc) {
        this.durationsinceltinc = durationsinceltinc;
    }

    public String getRigtime() {
        return rigtime;
    }

    public void setRigtime(String rigtime) {
        this.rigtime = rigtime;
    }

    public String getRigtimecumcalc() {
        return rigtimecumcalc;
    }

    public void setRigtimecumcalc(String rigtimecumcalc) {
        this.rigtimecumcalc = rigtimecumcalc;
    }

    public String getTmrotatingcalc() {
        return tmrotatingcalc;
    }

    public void setTmrotatingcalc(String tmrotatingcalc) {
        this.tmrotatingcalc = tmrotatingcalc;
    }

    public String getHeadcountcalc() {
        return headcountcalc;
    }

    public void setHeadcountcalc(String headcountcalc) {
        this.headcountcalc = headcountcalc;
    }

    public String getDurationpersonneltotcalc() {
        return durationpersonneltotcalc;
    }

    public void setDurationpersonneltotcalc(String durationpersonneltotcalc) {
        this.durationpersonneltotcalc = durationpersonneltotcalc;
    }

    public String getSummaryops() {
        return summaryops;
    }

    public void setSummaryops(String summaryops) {
        this.summaryops = summaryops;
    }

    public String getStatusend() {
        return statusend;
    }

    public void setStatusend(String statusend) {
        this.statusend = statusend;
    }

    public String getPlannextrptops() {
        return plannextrptops;
    }

    public void setPlannextrptops(String plannextrptops) {
        this.plannextrptops = plannextrptops;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getGasbackgroundavg() {
        return gasbackgroundavg;
    }

    public void setGasbackgroundavg(String gasbackgroundavg) {
        this.gasbackgroundavg = gasbackgroundavg;
    }

    public String getGasconnectionavg() {
        return gasconnectionavg;
    }

    public void setGasconnectionavg(String gasconnectionavg) {
        this.gasconnectionavg = gasconnectionavg;
    }

    public String getGasdrillavg() {
        return gasdrillavg;
    }

    public void setGasdrillavg(String gasdrillavg) {
        this.gasdrillavg = gasdrillavg;
    }

    public String getGastripavg() {
        return gastripavg;
    }

    public void setGastripavg(String gastripavg) {
        this.gastripavg = gastripavg;
    }
}