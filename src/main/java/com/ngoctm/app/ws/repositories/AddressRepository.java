package com.ngoctm.app.ws.repositories;

import com.ngoctm.app.ws.entity.AddressEntity;
import com.ngoctm.app.ws.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
    List<AddressEntity> findAllByUser(UserEntity userEntity);
    AddressEntity findByIdAddress(String addressId);
}
