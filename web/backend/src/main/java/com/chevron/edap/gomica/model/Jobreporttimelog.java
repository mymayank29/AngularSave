package com.chevron.edap.gomica.model;

import javax.persistence.*;

@Entity(name = "Jobreporttimelog")
@Table(name = "wvjobreporttimelog", schema = "gomica")
public class Jobreporttimelog {

 @Id
 @Column(name = "idrec")
 private String idrec;

 @Column(name = "idrecparent")
 private String idrecparent;

 @Column(name = "dttmstartcalc")
 private String dttmstartcalc;

 @Column(name = "durationnoprobtimecalc")
 private String durationnoprobtimecalc;

 @Column(name = "phase")
 private String phase;

 @Column(name = "code1")
 private String code1;

 @Column(name = "com")
 private String com;

 @Column(name = "refnoproblemcalc")
 private String refnoproblemcalc;

 @Column(name = "durationproblemtimecalc")
 private String durationproblemtimecalc;


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

public Jobreporttimelog() { }
}