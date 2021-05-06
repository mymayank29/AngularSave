package com.chevron.edap.gomica.model;

import javax.persistence.*;

@Entity(name = "Wellbore")
@Table(name = "wvwellbore", schema = "gomica")
public class Wellbore {

 @Id
 @Column(name = "idrec")
 private String idrec;

 @Column(name = "idrecparent")
 private String idrecparent;

 @Column(name = "des")
 private String des;

 @Column(name = "profiletyp")
 private String profiletyp;

 @Column(name = "wellboreida")
 private String wellboreida;

 @Column(name = "wellboreidb")
 private String wellboreidb;


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

 public String getProfiletyp() {
        return profiletyp;
    }
 public void setProfiletyp(String profiletyp) {
        this.profiletyp = profiletyp;
    }

 public String getWellboreida() {
        return wellboreida;
    }
 public void setWellboreida(String wellboreida) {
        this.wellboreida = wellboreida;
    }

 public String getWellboreidb() {
        return wellboreidb;
    }
 public void setWellboreidb(String wellboreidb) {
        this.wellboreidb = wellboreidb;
    }

public Wellbore() { }
}