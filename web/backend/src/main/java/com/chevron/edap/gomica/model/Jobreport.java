package com.chevron.edap.gomica.model;

import javax.persistence.*;
import java.sql.Date;

@Entity(name = "Jobreport")
@Table(name = "wvjobreport", schema = "gomica")
public class Jobreport {

 @Id
 @Column(name = "idrec")
 private String idrec;

 @Column(name = "idrecparent")
 private String idrecparent;

 @Column(name = "idrecwellborecalc")
 private String idrecwellborecalc;

 @Column(name = "dttmstart")
 private String dttmstart;

 @Column(name = "reportnocalc")
 private String reportnocalc;

 @Column(name = "durationtimelogcumspudcalc")
 private String durationtimelogcumspudcalc;

 @Column(name = "durationtimelogtotcumcalc")
 private String durationtimelogtotcumcalc;

 @Column(name = "depthprogressdpcalc")
 private String depthprogressdpcalc;

@Column(name = "dttmstartdate")
private Date dttmstartdate;

    @Column(name = "rpttmactops")
    private String rpttmactops;

    @Column(name = "usertxt1")
    private String usertxt1;

    @Column(name = "durationsincerptinc")
    private String durationsincerptinc;

    @Column(name = "durationsinceltinc")
    private String durationsinceltinc;

    @Column(name = "rigtime")
    private String rigtime;

    @Column(name = "rigtimecumcalc")
    private String rigtimecumcalc;

    @Column(name = "tmrotatingcalc")
    private String tmrotatingcalc;

    @Column(name = "headcountcalc")
    private String headcountcalc;

    @Column(name = "durationpersonneltotcalc")
    private String durationpersonneltotcalc;

    @Column(name = "summaryops")
    private String summaryops;

    @Column(name = "statusend")
    private String statusend;

    @Column(name = "plannextrptops")
    private String plannextrptops;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "gasbackgroundavg")
    private String gasbackgroundavg;

    @Column(name = "gasconnectionavg")
    private String gasconnectionavg;

    @Column(name = "gasdrillavg")
    private String gasdrillavg;

    @Column(name = "gastripavg")
    private String gastripavg;

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

    public Jobreport() { }
}