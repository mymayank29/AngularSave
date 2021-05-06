package com.chevron.edap.gomica.repository;

import com.chevron.edap.gomica.model.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  LineItemRepository extends JpaRepository<LineItem, String> {
    List<LineItem> findByAribaDocId(String aribaDocId);
    List<LineItem> findByAribaDocIdIn(List<String> aribaDocId);
}
