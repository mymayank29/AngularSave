package com.chevron.edap.gomica.repository;

import com.chevron.edap.gomica.model.JobreportNptRelated;
import com.chevron.edap.gomica.model.Jobsafetychk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobreportNptRelatedRepository extends JpaRepository<JobreportNptRelated, String> {
 List<JobreportNptRelated> findByLinkId(String linkId);
 List<JobreportNptRelated> findByLinkIdIn(List<String> linkIds);
}