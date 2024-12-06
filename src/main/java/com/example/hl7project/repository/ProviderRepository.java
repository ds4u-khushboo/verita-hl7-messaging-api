package com.example.hl7project.repository;

import com.example.hl7project.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, String> {
    List<Provider> findBySpecialty(String specialty);

    Provider findByProviderId(String providerId);

    Provider findByProviderName(String providerName);

}

