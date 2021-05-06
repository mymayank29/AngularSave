package com.chevron.edap.gomica.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "JobreportNptRelated")
@Table(name = "jobreport_npt_related", schema = "gomica")
public class JobreportNptRelated {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "idrec")
    private String idrec;

    @Column(name = "link_id")
    private String linkId;


    public JobreportNptRelated() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdrec() {
        return idrec;
    }

    public void setIdrec(String idrec) {
        this.idrec = idrec;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }
}
