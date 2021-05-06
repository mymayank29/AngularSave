package com.chevron.edap.gomica.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chevron.edap.gomica.dto.DrillingReportDto;
import com.chevron.edap.gomica.dto.DrillingReportHTMLDto;
import com.chevron.edap.gomica.dto.InvoiceDto;
import com.chevron.edap.gomica.dto.JobDto;
import com.chevron.edap.gomica.dto.JobintervalproblemDto;
import com.chevron.edap.gomica.dto.JobreportDto;
import com.chevron.edap.gomica.dto.JobreportNptRelatedDto;
import com.chevron.edap.gomica.dto.JobreporttimelogDto;
import com.chevron.edap.gomica.dto.JobsafetychkDto;
import com.chevron.edap.gomica.dto.WellboreDto;
import com.chevron.edap.gomica.dto.WellheaderDto;
import com.chevron.edap.gomica.repository.InvoiceRepository;
import com.chevron.edap.gomica.repository.JobRepository;
import com.chevron.edap.gomica.repository.JobintervalproblemRepository;
import com.chevron.edap.gomica.repository.JobreportNptRelatedRepository;
import com.chevron.edap.gomica.repository.JobreportRepository;
import com.chevron.edap.gomica.repository.JobreporttimelogRepository;
import com.chevron.edap.gomica.repository.JobsafetychkRepository;
import com.chevron.edap.gomica.repository.WellboreRepository;
import com.chevron.edap.gomica.repository.WellheaderRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class DrillingReportService {
    
    @Autowired
    JobreportRepository jobreportRepository;
    @Autowired
    WellboreRepository wellboreRepository;
    @Autowired
    JobRepository jobRepository;
    @Autowired
    JobsafetychkRepository jobsafetychkRepository;
    @Autowired
    JobreportNptRelatedRepository jobreportNptRelatedRepository;
    @Autowired
    VelocityEngine velocityEngine;
    @Autowired
    InvoiceService invoiceService;
    @Autowired
    JobreporttimelogRepository jobreporttimelogRepository;
    @Autowired
    JobintervalproblemRepository jobintervalproblemRepository;
    @Autowired
    WellheaderRepository wellheaderRepository;
    @Autowired
    InvoiceRepository invoiceRepository;

    public byte[] createDrillingReportZIP(List<String> ids)  {
        List<DrillingReportDto> drillingReportDtoList = retrieveDrillingReportDatafromDB(ids);
        List<DrillingReportHTMLDto> htmlReports = createHTMLReportFromTemplate(drillingReportDtoList);
        byte[] reportBytes = convertAndZipHtpmPages(htmlReports);
        return reportBytes;
    }

    private static byte[] convertHtmlToPdf(String htmlPage) {
        byte[] pdfPagesBytes = null;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlPage, "");
            builder.toStream(os);
            builder.run();
            pdfPagesBytes = os.toByteArray();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        return pdfPagesBytes;
    }

    private static byte[] convertAndZipHtpmPages(List<DrillingReportHTMLDto> htmlReports) {

        ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(byteOS);
        byte[] result = null;

        try {
            for(DrillingReportHTMLDto htmlReport: htmlReports) {
                byte[] pdfPagesBytes = convertHtmlToPdf(htmlReport.getContent());
                zos.putNextEntry(new ZipEntry(htmlReport.getFileName() + ".pdf"));
                System.out.println("ZOS:" + (zos == null));
                System.out.println("pdf bytes:" + (pdfPagesBytes == null));
                zos.write(pdfPagesBytes);
                zos.closeEntry();
            }
            zos.close();
            byteOS.close();
            result = byteOS.toByteArray();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        } finally {
            try{
                zos.close();
                byteOS.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return result;
    }

    public List<DrillingReportHTMLDto> createHTMLReportFromTemplate(List<DrillingReportDto> drillingReportDtoList) {
        List<DrillingReportHTMLDto> htmlReports = new ArrayList<>();
        try{
            for (DrillingReportDto drillingReportDto : drillingReportDtoList) {
                VelocityContext ctx = new VelocityContext();
                ctx.put("drillingReport", drillingReportDto);
                final String templatePath = "templates/DrillingReportv2.vm";
                Writer writer = new StringWriter();
                velocityEngine.mergeTemplate(templatePath,"UTF-8", ctx, writer);
                String reportContent = writer.toString();
                if(reportContent != null) {
                    String reportDate = drillingReportDto.getWvjobreport().getDttmstart();
                    String wellName = drillingReportDto.getWellheader().getWellname();
                    String jobType = drillingReportDto.getWvjob().getJobtyp();
                    String refno = drillingReportDto.getInvoice().getNpt_ref_no();
                    String reportFileName = reportDate + "_" + wellName + "_" + jobType + "_RefNo" + refno;
                    //Filter out duplicate reports
                    if (!htmlReports.stream().anyMatch(x -> reportFileName.equals(x.getFileName()))) {						
                    	htmlReports.add(new DrillingReportHTMLDto(reportFileName, writer.toString()));
					}
                }
                writer.close();
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        return htmlReports;
    }

    public List<DrillingReportDto> retrieveDrillingReportDatafromDB(List<String> ids) {
    	List<InvoiceDto> invoices = getInvoices(ids);
    	List<DrillingReportDto> drillingReportDtoList = new ArrayList<>();
    	for (InvoiceDto invoiceDto : invoices) {
            List<String> idjobreportList = getJobreportNptRelated(invoiceDto.getId()).stream().map(rec -> rec.getIdrec()).distinct().collect(Collectors.toList());
            
            for (String idjobreport : idjobreportList) {
                JobreportDto wvjobreport = getJobreport(idjobreport);
                String wvwellboreid = wvjobreport.getIdrecwellborecalc();
                WellboreDto wvwellbore = null;
                if(wvwellboreid != null) {
                    wvwellbore = getWellbore(wvwellboreid);
                }
                String wvjobid = wvjobreport.getIdrecparent();
                JobDto wvjob = null;
                if(wvjobid != null) {
                    wvjob = getJob(wvjobid);
                }
                List<JobsafetychkDto> wvjobsafetychk = null;
                if(wvjobid != null) {
                    wvjobsafetychk = getJobSafetyCheck(wvjobid, wvjobreport.getDttmstartdate());
                }
                List<JobreporttimelogDto> wvjobreporttimelog = getJobreporttimelog(idjobreport);
                Collections.sort(wvjobreporttimelog, new Comparator<JobreporttimelogDto>() {

					@Override
					public int compare(JobreporttimelogDto o1, JobreporttimelogDto o2) {
						return o2.getDttmstartcalc().compareTo(o1.getDttmstartcalc());
					}
				});
                Collections.reverse(wvjobreporttimelog);
                List<JobintervalproblemDto> wvjobintervalproblems = getJobintervalproblem(wvjobreport.idrecparent, wvjobreport.dttmstartdate, wvjobreport.dttmstartdate);
                if (wvjobintervalproblems == null) {
                	wvjobintervalproblems = new ArrayList<>();
                }
                wvjobintervalproblems = wvjobintervalproblems.stream()
                		.filter(x -> x.getRefno() != null && invoiceDto.getNpt_ref_no().equals(x.getRefno().replaceAll("\\.\\d*", "")))
                		.collect(Collectors.toList());
                Collections.sort(wvjobintervalproblems, new Comparator<JobintervalproblemDto>() {

					@Override
					public int compare(JobintervalproblemDto o1, JobintervalproblemDto o2) {
						return o1.getRefno().compareTo(o2.getRefno());
					}
				});

                WellheaderDto wellheaderDto = null;
                if(wvjob != null) {
                    wellheaderDto = getWellheaderDto(wvjob.idwell);
                }

                drillingReportDtoList.add(new DrillingReportDto(invoiceDto, wellheaderDto, wvjobreport, wvwellbore, wvjob,
                        wvjobsafetychk, wvjobintervalproblems, wvjobreporttimelog));
            }
		}
        
        return drillingReportDtoList;
    }
    
    public List<InvoiceDto> getInvoices(List<String> ids) {
    	return invoiceRepository.findByIdIn(ids)
    			.stream()
    			.map(rec -> new InvoiceDto(rec))
    			.collect(Collectors.toList());
    }

    public JobreportDto getJobreport(String wvjobreportid){
         return new JobreportDto(jobreportRepository.findByIdrec(wvjobreportid));
    }
    
    public List<JobreportNptRelatedDto> getJobreportNptRelated(String linkId){
        return jobreportNptRelatedRepository.findByLinkId(linkId)
                .stream()
                .map(rec -> new JobreportNptRelatedDto(rec))
                .collect(Collectors.toList());
    }

    public List<JobreportNptRelatedDto> getJobreportNptRelatedIn(List<String> linkIds){
        return jobreportNptRelatedRepository.findByLinkIdIn(linkIds)
                .stream()
                .map(rec -> new JobreportNptRelatedDto(rec))
                .collect(Collectors.toList());
    }

    public WellboreDto getWellbore(String wvwellboreid){
         return new WellboreDto(wellboreRepository.findByIdrec(wvwellboreid));
    }

    public JobDto getJob(String wvjobid){
         return new JobDto(jobRepository.findByIdrec(wvjobid));
    }

    public List<JobsafetychkDto> getJobSafetyCheck(String wvjobid, Date wvjobreportDate){
         return jobsafetychkRepository.findByIdrecparentAndDttmdate(wvjobid, wvjobreportDate)
              .stream()
              .map(rec -> new JobsafetychkDto(rec))
              .collect(Collectors.toList());
    }

    public List<JobreporttimelogDto> getJobreporttimelog(String wvjobreportid){
        return jobreporttimelogRepository.findByIdrecparent(wvjobreportid)
                .stream()
                .map(rec -> new JobreporttimelogDto(rec))
                .collect(Collectors.toList());
    }

    public List<JobintervalproblemDto> getJobintervalproblem(String wvjobintervalproblemid, Date dt1, Date dt2){
        List<JobintervalproblemDto> jobintervalproblems = jobintervalproblemRepository
                .findByIdrecparentAndDttmstartdateLessThanEqualAndDttmenddateGreaterThanEqual(wvjobintervalproblemid, dt1, dt2)
                .stream()
                .map(rec -> new JobintervalproblemDto(rec))
                .collect(Collectors.toList());
        if(jobintervalproblems.isEmpty()){
            return null;
        }else{
            return jobintervalproblems;
        }
    }

    public WellheaderDto getWellheaderDto(String idwell){
        return new WellheaderDto(wellheaderRepository.findByIdwell(idwell));
    }

}

