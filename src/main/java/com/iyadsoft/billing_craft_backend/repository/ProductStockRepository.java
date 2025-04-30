package com.iyadsoft.billing_craft_backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iyadsoft.billing_craft_backend.dto.ProductDetailDTO;
import com.iyadsoft.billing_craft_backend.dto.ProductEntryDto;
import com.iyadsoft.billing_craft_backend.dto.ProductStockCountDTO;
import com.iyadsoft.billing_craft_backend.dto.SaleReturnDto;
import com.iyadsoft.billing_craft_backend.dto.SupplierDetailsDto;
import com.iyadsoft.billing_craft_backend.dto.SupplierProductDto;
import com.iyadsoft.billing_craft_backend.dto.UpdateableStock;
import com.iyadsoft.billing_craft_backend.entity.ProductStock;

@Repository
public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {

      @Query("SELECT ps FROM ProductStock ps LEFT JOIN ps.productSale psale WHERE ps.username=:username AND psale IS NULL")
      List<ProductStock> getProductsStockByUsername(String username);

      @Query("SELECT new com.iyadsoft.billing_craft_backend.dto.SaleReturnDto(ps.productStock.category, ps.productStock.brand, ps.productStock.productName, ps.productStock.color, ps.productStock.productno, ps.productStock.supplier, ps.productStock.supplierInvoice, ps.productStock.pprice, ps.productStock.sprice, ps.productStock.date, ps.productStock.time) " +
      "FROM ProductSale ps " +
      "JOIN ps.productStock p " +
      "WHERE ps.saleType = 'returned' AND ps.username = :username")
      List<SaleReturnDto> getReturnedsStockByUsername(String username);

      @Query("SELECT new com.iyadsoft.billing_craft_backend.dto.ProductEntryDto(ps.category, ps.brand, ps.productName, ps.pprice, ps.sprice, ps.color, ps.supplier, ps.supplierInvoice, ps.productno, ps.date, ps.time) FROM ProductStock ps WHERE ps.username=:username AND MONTH(ps.date) = MONTH(CURRENT_DATE) AND YEAR(ps.date) = YEAR(CURRENT_DATE)")
      List<ProductEntryDto> getProductsStockByUsernameForCurrentMonth(@Param("username") String username);

      @Query("SELECT new com.iyadsoft.billing_craft_backend.dto.ProductEntryDto(ps.category, ps.brand, ps.productName, ps.pprice, ps.sprice, ps.color, ps.supplier, ps.supplierInvoice, ps.productno, ps.date, ps.time) FROM ProductStock ps WHERE ps.username=:username AND ps.date BETWEEN :startDate AND :endDate")
      List<ProductEntryDto> getDatewiseProductsStockByUsername(String username, LocalDate startDate, LocalDate endDate);

      @Query("SELECT new com.iyadsoft.billing_craft_backend.dto.UpdateableStock(ps.supplier, ps.productName, ps.pprice, COUNT(ps.productno)) FROM ProductStock ps LEFT JOIN ps.productSale psale WHERE ps.username=:username AND psale IS NULL GROUP BY ps.supplier, ps.productName, ps.pprice")
      List<UpdateableStock> getProductsStockByUsernameAndSupplier(String username);

           @Query("SELECT CASE WHEN COUNT(ps) > 0 THEN TRUE ELSE FALSE END " +
           "FROM ProductStock ps " +
           "WHERE ps.username = :username " +
           "AND ps.productno = :productno " +
           "AND ps.proId NOT IN (SELECT psale.productStock.proId FROM ProductSale psale)")
    boolean existsByUsernameAndProductnoNotInProductSale(String username, String productno);

   
@Query("SELECT new com.iyadsoft.billing_craft_backend.dto.ProductStockCountDTO(" +
       "p.category, p.brand, p.productName, p.color, p.pprice, p.sprice, " +
       "SUM(CASE WHEN p.date < :today THEN 1 ELSE 0 END)-SUM(CASE WHEN p.date < :today AND ps.date < :today THEN 1 ELSE 0 END), " +                    // Total qty entered before today
       "SUM(CASE WHEN p.date = :today THEN 1 ELSE 0 END), " +      
       "SUM(CASE WHEN ps.date = :today THEN 1 ELSE 0 END)) " + 
       "FROM ProductStock p " +
       "LEFT JOIN p.productSale ps " +
       "WHERE p.username = :username " +
       "GROUP BY p.category, p.brand, p.productName, p.color, p.pprice, p.sprice")
List<ProductStockCountDTO> countProductByUsernameGroupByCategoryBrandProductName(
        @Param("username") String username,
        @Param("today") LocalDate today);



      @Query(value = "SELECT DISTINCT ps.supplier AS supplierName " +
                  "FROM product_stock ps " +
                  "WHERE ps.username=:username AND ps.supplier IS NOT NULL " +
                  "UNION " +
                  "SELECT DISTINCT c.c_name AS supplierName " +
                  "FROM customer c " +
                  "JOIN product_sale psale ON psale.cid = c.cid " +
                  "WHERE psale.username=:username AND psale.sale_type = 'vendor' AND c.c_name IS NOT NULL " +
                  "UNION " +
                  "SELECT DISTINCT sp.supplier_name AS supplierName " +
                  "FROM supplier_payment sp " +
                  "WHERE sp.username=:username AND sp.supplier_name IS NOT NULL", nativeQuery = true)
      List<String> findAllDistinctSupplierNames(@Param("username") String username);

      @Query("SELECT SUM(COALESCE(ps.pprice, 0.0)) AS totalProductValue " +
                  "FROM ProductStock ps " +
                  "WHERE ps.username = :username AND ps.supplier = :supplier")
      Double findTotalProductValueByUsernameAndSupplier(@Param("username") String username,
                  @Param("supplier") String supplier);

      @Query("SELECT new com.iyadsoft.billing_craft_backend.dto.SupplierDetailsDto(ps.date, ps.supplierInvoice, COUNT(ps.productno), SUM(ps.pprice), 0.0, 0.0, 0.0, 'No') FROM ProductStock ps WHERE ps.username = :username AND ps.supplier = :supplierName AND ps.date <= :date GROUP BY ps.date, ps.supplierInvoice")
      List<SupplierDetailsDto> findProductDetailsByUsernameAndSupplierName(String username, String supplierName, LocalDate date);

      @Query("SELECT ps FROM ProductStock ps LEFT JOIN ps.productSale psale WHERE ps.username=:username AND ps.supplier=:supplier AND ps.productName=:productName AND ps.pprice=:pprice AND psale IS NULL")
      List<ProductStock> findByUsernameAndSupplierAndProductNameAndPprice(String username, String supplier, String productName, Double pprice);

      @Query("SELECT ps FROM ProductStock ps WHERE ps.username = :username AND ps.productno = :productno AND ps.proId NOT IN (SELECT DISTINCT ps.proId FROM ProductSale psale JOIN psale.productStock ps)")
      List<ProductStock> findProductsNotInSalesStock(@Param("username") String username, @Param("productno") String productno);

      @Query("SELECT new com.iyadsoft.billing_craft_backend.dto.ProductDetailDTO( " +
           "ps.category, ps.brand, ps.productName, ps.productno, ps.color, ps.pprice, ps.sprice, ps.supplier, ps.supplierInvoice, ps.date, ps.time, " +
           "CASE WHEN psale IS NOT NULL THEN 'sold' ELSE 'stored' END, " +
           "psale.saleType, psale.sprice, psale.discount, psale.offer, psale.date, psale.time, c.cid, c.cName, c.phoneNumber, c.soldby) " +
           "FROM ProductStock ps " +
           "LEFT JOIN ProductSale psale ON ps.proId = psale.productStock.proId " +
           "LEFT JOIN Customer c ON psale.customer.cid = c.cid " +
           "WHERE ps.username = :username AND ps.productno = :productno")
    List<ProductDetailDTO> findAllProductOccurrences(@Param("username") String username, @Param("productno") String productno);


    boolean existsByProductnoAndProIdNot(String productno, Long proId);

    Optional<ProductStock> findTopByUsernameAndBrandAndProductNameOrderByProIdDesc(String username, String brand, String productName);

            @Query("SELECT new com.iyadsoft.billing_craft_backend.dto.SupplierProductDto(" +
            "ps.productName, ps.color, ps.pprice, COUNT(ps.productno)) " +
            "FROM ProductStock ps " +
            "WHERE ps.username = :username AND ps.supplierInvoice = :supplierInvoice " +
            "GROUP BY ps.productName, ps.color, ps.pprice")
     List<SupplierProductDto> findProductInfoByInvoiceAndUser(@Param("username") String username, @Param("supplierInvoice") String supplierInvoice);
     
}
