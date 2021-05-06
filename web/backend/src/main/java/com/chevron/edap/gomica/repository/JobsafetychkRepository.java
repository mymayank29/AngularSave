package com.chevron.edap.gomica.repository;

import com.chevron.edap.gomica.model.Jobsafetychk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface JobsafetychkRepository extends JpaRepository<Jobsafetychk, String> {
// List<Jobsafetychk> findByIdrecparent(String idrecparent);
 List<Jobsafetychk> findByIdrecparentAndDttmdate(String idrecparent, Date dttmdate);
}