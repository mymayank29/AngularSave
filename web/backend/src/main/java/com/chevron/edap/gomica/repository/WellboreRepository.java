package com.chevron.edap.gomica.repository;

import com.chevron.edap.gomica.model.Wellbore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WellboreRepository extends JpaRepository<Wellbore, String> {
 Wellbore findByIdrec(String idrec);
}