package com.example.hl7project.repository;

import com.example.hl7project.model.Providers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProviderRepository extends JpaRepository<Providers, String> {
    List<Providers> findBySpecialty(String specialty);

    Providers findByProviderCode(String providerCode);


}
