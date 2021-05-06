package com.chevron.edap.gomica.dto;

import java.util.List;

public class DrillingReportDto {

	private InvoiceDto invoice;
    private WellheaderDto wellheader;
    private List<JobintervalproblemDto> wvjobintervalproblems;
    private List<JobreporttimelogDto> wvjobreporttimelog;
    private JobreportDto wvjobreport;
    private WellboreDto wvwellbore;
    private JobDto wvjob;
    private List<JobsafetychkDto> jobsafetychkDtoList;


    public DrillingReportDto(InvoiceDto invoice, WellheaderDto wellheader, JobreportDto wvjobreport, WellboreDto wvwellbore, JobDto wvjob,
                             List<JobsafetychkDto> jobsafetychkDtoList, List<JobintervalproblemDto> wvjobintervalproblem,
                             List<JobreporttimelogDto> wvjobreporttimelog) {
    	this.invoice = invoice;
        this.wvjobreport = wvjobreport;
        this.wvwellbore = wvwellbore;
        this.wvjob = wvjob;
        this.jobsafetychkDtoList = jobsafetychkDtoList;
        this.wellheader = wellheader;
        this.wvjobintervalproblems = wvjobintervalproblem;
        this.wvjobreporttimelog = wvjobreporttimelog;
    }

    public InvoiceDto getInvoice() {
		return invoice;
	}

	public void setInvoice(InvoiceDto invoice) {
		this.invoice = invoice;
	}

	public WellheaderDto getWellheader() {
        return wellheader;
    }

    public void setWellheader(WellheaderDto wellheader) {
        this.wellheader = wellheader;
    }

    public List<JobintervalproblemDto> getWvjobintervalproblems() {
        return wvjobintervalproblems;
    }

    public void setWvjobintervalproblems(List<JobintervalproblemDto> wvjobintervalproblem) {
        this.wvjobintervalproblems = wvjobintervalproblem;
    }

    public List<JobreporttimelogDto> getWvjobreporttimelog() {
        return wvjobreporttimelog;
    }

    public void setWvjobreporttimelog(List<JobreporttimelogDto> wvjobreporttimelog) {
        this.wvjobreporttimelog = wvjobreporttimelog;
    }

    public JobreportDto getWvjobreport() {
        return wvjobreport;
    }

    public void setWvjobreport(JobreportDto wvjobreport) {
        this.wvjobreport = wvjobreport;
    }

    public WellboreDto getWvwellbore() {
        return wvwellbore;
    }

    public void setWvwellbore(WellboreDto wvwellbore) {
        this.wvwellbore = wvwellbore;
    }

    public JobDto getWvjob() {
        return wvjob;
    }

    public void setWvjob(JobDto wvjob) {
        this.wvjob = wvjob;
    }

    public List<JobsafetychkDto> getJobsafetychkDtoList() {
        return jobsafetychkDtoList;
    }

    public void setJobsafetychkDtoList(List<JobsafetychkDto> jobsafetychkDtoList) {
        this.jobsafetychkDtoList = jobsafetychkDtoList;
    }
}
