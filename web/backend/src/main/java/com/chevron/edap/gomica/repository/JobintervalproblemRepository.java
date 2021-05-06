package com.chevron.edap.gomica.repository;

import com.chevron.edap.gomica.model.Jobintervalproblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface JobintervalproblemRepository extends JpaRepository<Jobintervalproblem, String> {
 Jobintervalproblem findByIdrec(String idrec);
 List<Jobintervalproblem> findByIdrecparentAndDttmstartdateLessThanEqualAndDttmenddateGreaterThanEqual(String idrecparent, Date dt1, Date dt2);
}