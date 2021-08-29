package com.ngoctm.app.ws.service;

import com.ngoctm.app.ws.entity.AddressEntity;
import com.ngoctm.app.ws.entity.UserEntity;
import com.ngoctm.app.ws.exceptions.UserServiceException;
import com.ngoctm.app.ws.model.response.ErrorMessages;
import com.ngoctm.app.ws.repositories.AddressRepository;
import com.ngoctm.app.ws.repositories.UserRepository;
import com.ngoctm.app.ws.shared.dto.AddressDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AddressServiceImpl implements AddressService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public List<AddressDto> getAddresses(String userId) {
        ModelMapper modelMapper = new ModelMapper();

        List<AddressDto> addressDtos = new ArrayList<>();
        UserEntity userEntity = userRepository.findByUserId(userId);

        if(userEntity == null){
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getMessage());
        }

        List<AddressEntity> addressEntities = addressRepository.findAllByUser(userEntity);

        if(addressEntities.isEmpty()){
            return addressDtos;
        }

        for(AddressEntity addressEntity : addressEntities){
            AddressDto addressDto = modelMapper.map(addressEntity, AddressDto.class);
            addressDtos.add(addressDto);
        }

        return addressDtos;
    }

    @Override
    public AddressDto getAddress(String addressId) {
        ModelMapper modelMapper = new ModelMapper();
        AddressEntity addressEntity = addressRepository.findByIdAddress(addressId);
        if(addressEntity != null){
            AddressDto addressDto = modelMapper.map(addressEntity, AddressDto.class);
            return addressDto;
        }
        return null;
    }
}
