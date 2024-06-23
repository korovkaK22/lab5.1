package com.example.taxidriverrestapplication.repositories;

import com.example.taxidriverrestapplication.entity.TaxiDriver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxiDriverRepository extends JpaRepository<TaxiDriver, Integer> {


    Page<TaxiDriver> findByCompanyAndAge(@Param("companyId") Integer companyId,
                                         @Param("minAge") Integer minAge,
                                         @Param("maxAge") Integer maxAge,
                                         Pageable pageable);


    List<TaxiDriver> findByCompanyAndAge(@Param("companyId") Integer companyId,
                                         @Param("minAge") Integer minAge,
                                         @Param("maxAge") Integer maxAge);

}
