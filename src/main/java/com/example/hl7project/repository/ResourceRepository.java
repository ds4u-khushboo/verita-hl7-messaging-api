package com.example.hl7project.repository;

import com.example.hl7project.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Resource findByResourceId(String resourceId);

    Resource findByResourceType(String resourceType);

}
