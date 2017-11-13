package io.osg.training.storefront.respositories;


import io.osg.training.storefront.model.entities.AddressEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Set;

@RepositoryRestResource(collectionResourceRel = "address", path = "address")
public interface AddressEntityRepository extends PagingAndSortingRepository<AddressEntity, String> {
    Set<AddressEntity> findByAddressLine1ContainsIgnoreCase(@Param("addressLine1") String addressLine1);
    Set<AddressEntity> findByAddressLine2ContainsIgnoreCase(@Param("addressLine2") String addressLine2);
    Set<AddressEntity> findByCityContainsIgnoreCase(@Param("city") String countrySubdivision);
    Set<AddressEntity> findByCountrySubdivisionContainsIgnoreCase(@Param("countrySubdivision") String countrySubdivision);
    Set<AddressEntity> findByPostalCode(@Param("postalCode") String postalCode);
}