package com.chevron.edap.gomica.model;

import javax.persistence.*;

@Entity(name = "Wellheader")
@Table(name = "wvwellheader", schema = "gomica")
public class Wellheader {

 @Id
 @Column(name = "idwell")
 private String idwell;

 @Column(name = "wellname")
 private String wellname;

 @Column(name = "wellidd")
 private String wellidd;

 @Column(name = "lease")
 private String lease;

 @Column(name = "fieldname")
 private String fieldname;

 @Column(name = "division")
 private String division;

 @Column(name = "stateprov")
 private String stateprov;

 @Column(name = "wellida")
 private String wellida;

 @Column(name = "wellidc")
 private String wellidc;

 @Column(name = "elvorigkb")
 private String elvorigkb;

 @Column(name = "waterdepth")
 private String waterdepth;

 @Column(name = "kbtomudcalc")
 private String kbtomudcalc;

 @Column(name = "kbtocascalc")
 private String kbtocascalc;

 @Column(name = "kbtotubcalc")
 private String kbtotubcalc;


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



    public Wellheader() { }
}