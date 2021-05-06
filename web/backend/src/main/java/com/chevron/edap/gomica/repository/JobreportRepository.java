package com.chevron.edap.gomica.repository;

import com.chevron.edap.gomica.model.Jobreport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobreportRepository extends JpaRepository<Jobreport, String> {
 Jobreport findByIdrec(String idrec);
}