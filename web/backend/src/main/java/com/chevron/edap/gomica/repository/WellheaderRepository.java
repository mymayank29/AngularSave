package com.chevron.edap.gomica.repository;

import com.chevron.edap.gomica.model.Wellheader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WellheaderRepository extends JpaRepository<Wellheader, String> {
 Wellheader findByIdwell(String idrec);
}