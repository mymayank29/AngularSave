package com.chevron.edap.gomica.dto;

import com.chevron.edap.gomica.model.Jobreport;
import com.chevron.edap.gomica.model.JobreportNptRelated;

public class JobreportNptRelatedDto {
    public String id;
    public String idrec;
    public String linkId;

    public JobreportNptRelatedDto() {  }

    public JobreportNptRelatedDto(JobreportNptRelated jobreportNptRelated) {
        this.id = jobreportNptRelated.getId();
        this.idrec = jobreportNptRelated.getIdrec();
        this.linkId = jobreportNptRelated.getLinkId();
    }

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