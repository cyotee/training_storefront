package io.osg.training.storefront.respositories;


import io.osg.training.storefront.model.entities.ClassSessionEntity;
import org.joda.time.DateTime;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Date;
import java.util.Set;

@RepositoryRestResource(collectionResourceRel = "class-sessions", path = "class-sessions")
public interface ClassSessionRepository extends PagingAndSortingRepository<ClassSessionEntity, Integer> {
    /*Set<ClassSessionEntity> findByClassSkuVendorSkuCode(String vendorSkuCodeSearchCriterion);
    Set<ClassSessionEntity> findByClassSkuSkuNameContainsIgnoreCase(String vendorSkuNameSearchCriterion);
    Set<ClassSessionEntity> findByClassSkuSkuVendorVendorNameContainsIgnoreCase(String vendorSkuNameSearchCriterion);
    Set<ClassSessionEntity> findByClassSessionInstructorInstructorNameContainsIgnoreCase(String classSessionInstrsutorNameSearchCriterion);
    Set<ClassSessionEntity> findByClassSessionStartDateBetween(DateTime classSessionStartDateBeginSearchCriterion, DateTime classSessionStartDateEndSearchCriterion);*/
}
