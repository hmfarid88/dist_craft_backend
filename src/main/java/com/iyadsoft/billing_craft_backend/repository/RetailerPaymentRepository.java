package com.iyadsoft.billing_craft_backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iyadsoft.billing_craft_backend.dto.ReceiveDto;
import com.iyadsoft.billing_craft_backend.dto.RetailerDetailsDto;
import com.iyadsoft.billing_craft_backend.entity.RetailerPayment;

public interface RetailerPaymentRepository extends JpaRepository <RetailerPayment, Long>{
     @Query("SELECT o FROM RetailerPayment o WHERE YEAR(o.date) = :year AND MONTH(o.date) = :month AND o.username = :username ORDER BY o.date")
       List<RetailerPayment> findRetailerPayByMonth(@Param("year") int year, @Param("month") int month, @Param("username") String username);

       @Query("SELECT ps FROM RetailerPayment ps WHERE  ps.username=:username AND ps.date BETWEEN :startDate AND :endDate ORDER BY ps.date")
       List<RetailerPayment> findDatewiseRetailerPaymentByUsername(String username, LocalDate startDate, LocalDate endDate);

       @Query("SELECT SUM(sp.amount) FROM RetailerPayment sp WHERE sp.retailerName = :cName AND sp.username = :username")
       Optional<Double> findTotalPaidByCustomerName(@Param("cName") String cName, @Param("username") String username);

       @Query("SELECT SUM(p.amount) FROM RetailerPayment p WHERE p.username=:username ")
       Double findRetailerPaySum(@Param("username") String username);

       @Query("SELECT new com.iyadsoft.billing_craft_backend.dto.ReceiveDto(s.date, s.retailerName, s.note, s.amount) "
      + "FROM RetailerPayment s WHERE s.username = :username AND s.date = :date")
      List<ReceiveDto> findRetailerReceivesForToday(@Param("username") String username, @Param("date") LocalDate date);

      @Query("""
    SELECT rp.retailerName,
           COALESCE(SUM(CASE WHEN rp.paymentType = 'current' AND rp.date = :date THEN rp.amount ELSE 0 END), 0),
           COALESCE(SUM(CASE WHEN rp.paymentType = 'current' AND rp.date < :date THEN rp.amount ELSE 0 END), 0)+
           COALESCE(SUM(CASE WHEN rp.paymentType = 'previous' AND rp.date < :date THEN rp.amount ELSE 0 END), 0),
           COALESCE(SUM(CASE WHEN rp.paymentType = 'previous' AND rp.date = :date THEN rp.amount ELSE 0 END), 0) 
           FROM RetailerPayment rp 
    WHERE rp.username = :username GROUP BY rp.retailerName
""")
List<Object[]> getPaymentsByRetailer(@Param("username") String username, @Param("date") LocalDate date);

@Query("SELECT COALESCE(SUM(CASE WHEN rp.paymentType = 'current' THEN rp.amount ELSE 0 END), 0) + " +
"COALESCE(SUM(CASE WHEN rp.paymentType = 'previous' THEN rp.amount ELSE 0 END), 0) " +
"FROM RetailerPayment rp " +
"WHERE rp.username = :username")
Optional<Double> getTotalPayment(@Param("username") String username);


     @Query("SELECT new com.iyadsoft.billing_craft_backend.dto.RetailerDetailsDto(sp.date, sp.paymentType, 0.0, 0.0, sp.amount) " +
      "FROM RetailerPayment sp " +
      "WHERE sp.username = :username AND sp.retailerName = :retailerName")
      List<RetailerDetailsDto> findDetailsPaymentByUsernameAndRetailerName(
      @Param("username") String username,
      @Param("retailerName") String retailerName);
      

}
