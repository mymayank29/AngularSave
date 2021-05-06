package com.chevron.edap.gomica.repository;

import com.chevron.edap.gomica.model.IntervalProblem;
import com.chevron.edap.gomica.model.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntervalProblemRepository extends JpaRepository<IntervalProblem, Long> {
    List<IntervalProblem> findByAribaDocId(String aribaDocId);
    List<IntervalProblem> findByAribaDocIdIn(List<String> aribaDocId);
}
