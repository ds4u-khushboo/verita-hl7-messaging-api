package com.example.hl7project.repository;

import com.example.hl7project.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, String> {
    List<Provider> findBySpecialty(String specialty);

    Provider findByProviderName(String providerName);

}

