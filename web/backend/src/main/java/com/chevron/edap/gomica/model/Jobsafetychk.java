package com.chevron.edap.gomica.model;

import javax.persistence.*;
import java.sql.Date;

@Entity(name = "Jobsafetychk")
@Table(name = "wvjobsafetychk", schema = "gomica")
public class Jobsafetychk {

 @Id
 @Column(name = "idrec")
 private String idrec;

 @Column(name = "idrecparent")
 private String idrecparent;

 @Column(name = "des")
 private String des;

 @Column(name = "typ")
 private String typ;

 @Column(name = "inspector")
 private String inspector;

 @Column(name = "result")
 private String result;

 @Column(name = "com")
 private String com;

    @Column(name = "dttmdate")
    private Date dttmdate;

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

    public Date getDttmdate() {
        return dttmdate;
    }

    public void setDttmdate(Date dttmdate) {
        this.dttmdate = dttmdate;
    }

    public Jobsafetychk() { }
}