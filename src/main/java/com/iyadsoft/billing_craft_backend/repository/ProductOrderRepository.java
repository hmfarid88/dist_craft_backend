package com.iyadsoft.billing_craft_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iyadsoft.billing_craft_backend.entity.ProductOrder;

import jakarta.transaction.Transactional;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {

    boolean existsByUsernameAndProductno(String username, String productno);

    List<ProductOrder> findByUsername(String username);

    @Transactional
    @Modifying
    @Query("""
                DELETE FROM ProductOrder po
                WHERE po.username = :username
                  AND po.proId = :proId
            """)
    int deleteByUsernameAndProId(String username, Long proId);

    void deleteByProIdIn(List<Long> soldProIds);

}
