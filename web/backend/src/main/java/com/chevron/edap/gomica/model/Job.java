package com.chevron.edap.gomica.model;

import javax.persistence.*;

@Entity(name = "Job")
@Table(name = "wvjob", schema = "gomica")
public class Job {

 @Id
 @Column(name = "idrec")
 private String idrec;

 @Column(name = "idwell")
 private String idwell;

 @Column(name = "wvtyp")
 private String wvtyp;

 @Column(name = "jobtyp")
 private String jobtyp;

 @Column(name = "dttmstart")
 private String dttmstart;

 @Column(name = "dttmend")
 private String dttmend;


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

public Job() { }
}