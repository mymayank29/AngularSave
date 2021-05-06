package com.chevron.edap.gomica.repository;

import com.chevron.edap.gomica.dto.JobreporttimelogDto;
import com.chevron.edap.gomica.model.Jobreporttimelog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobreporttimelogRepository extends JpaRepository<Jobreporttimelog, String> {
 List<Jobreporttimelog> findByIdrecparent(String idrecparent);
}