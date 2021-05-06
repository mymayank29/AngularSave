package com.chevron.edap.gomica.dto;

import com.chevron.edap.gomica.model.Wellbore;

import static com.chevron.edap.gomica.util.StringEscapeTool.replaceXMLSpecialChars;

public class WellboreDto {
    public String idrec;
    public String idrecparent;
    public String des;
    public String profiletyp;
    public String wellboreida;
    public String wellboreidb;

    public WellboreDto() {  }

    public WellboreDto(Wellbore wvwellbore) {
        this.idrec = replaceXMLSpecialChars(wvwellbore.getIdrec());
        this.idrecparent = replaceXMLSpecialChars(wvwellbore.getIdrecparent());
        this.des = replaceXMLSpecialChars(wvwellbore.getDes());
        this.profiletyp = replaceXMLSpecialChars(wvwellbore.getProfiletyp());
        this.wellboreida = replaceXMLSpecialChars(wvwellbore.getWellboreida());
        this.wellboreidb = replaceXMLSpecialChars(wvwellbore.getWellboreidb());
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
}