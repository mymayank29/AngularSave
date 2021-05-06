package com.chevron.edap.gomica.repository;

import com.chevron.edap.gomica.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {
 Job findByIdrec(String idrec);
}