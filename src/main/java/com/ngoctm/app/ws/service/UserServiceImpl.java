package com.ngoctm.app.ws.service;

import com.ngoctm.app.ws.entity.AddressEntity;
import com.ngoctm.app.ws.entity.PasswordResetTokenEntity;
import com.ngoctm.app.ws.entity.UserEntity;
import com.ngoctm.app.ws.exceptions.UserServiceException;
import com.ngoctm.app.ws.model.response.ErrorMessages;
import com.ngoctm.app.ws.repositories.PasswordResetTokenRepository;
import com.ngoctm.app.ws.repositories.UserRepository;
import com.ngoctm.app.ws.shared.AmazonSES;
import com.ngoctm.app.ws.shared.Utils;
import com.ngoctm.app.ws.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        ModelMapper modelMapper = new ModelMapper();
        Pageable pageable = PageRequest.of(page, limit);
        Page<UserEntity> userEntitiesPage = userRepository.findAll(pageable);

        List<UserEntity> userEntities = userEntitiesPage.getContent();
        List<UserDto> userDtos = new ArrayList<>();

        for (UserEntity userEntity : userEntities){
            UserDto userDto = modelMapper.map(userEntity, UserDto.class);
            userDtos.add(userDto);
        }

        return userDtos;
    }

    @Override
    public UserDto create(UserDto userDto) {

        ModelMapper modelMapper = new ModelMapper();
        if(userRepository.findByEmail(userDto.getEmail()) != null){
            throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getMessage());
        }

        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setUserId(UUID.randomUUID().toString());
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userEntity.setEmailVerificationToken(Utils.generateEmailVerificationToken(userEntity.getUserId()));
        userEntity.setEmailVerificationStatus(false);

        for(AddressEntity addressEntity : userEntity.getAddresses()){
            addressEntity.setIdAddress(UUID.randomUUID().toString());
            addressEntity.setUser(userEntity);
        }

        UserEntity storedUser = userRepository.save(userEntity);

        UserDto returnValue = modelMapper.map(storedUser, UserDto.class);

        //send email
        AmazonSES amazonSES = new AmazonSES();
        amazonSES.sendVerifyEmail(returnValue);

        return returnValue;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getMessage());

        UserDto userDto = modelMapper.map(userEntity, UserDto.class);

        return userDto;
    }

    @Override
    public UserDto getUserByUserId(String id) {
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = userRepository.findByUserId(id);
        if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getMessage());

        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        return userDto;
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = userRepository.findByUserId(id);
        if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getMessage());

        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());

        UserEntity updatedUser = userRepository.save(userEntity);
        UserDto returnValue = modelMapper.map(updatedUser, UserDto.class);

        return returnValue;
    }

    @Override
    public void DeleteUser(String id) {
        UserEntity userEntity = userRepository.findByUserId(id);
        if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getMessage());

        userRepository.delete(userEntity);
    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean returnValue = false;
        UserEntity userEntity = userRepository.findByEmailVerificationToken(token);
        if(userEntity != null){
            boolean hasTokenExpire = Utils.hasTokenExpired(token);
            if(!hasTokenExpire){
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                return returnValue;
            }
        }

        return returnValue;
    }

    @Override
    public boolean requestPasswordReset(String email) {
        boolean returnValue = false;
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity != null){
            String token = Utils.generatePasswordResetToken(userEntity.getUserId());
            PasswordResetTokenEntity resetTokenEntity = new PasswordResetTokenEntity();
            resetTokenEntity.setToken(token);
            resetTokenEntity.setUserEntity(userEntity);
            passwordResetTokenRepository.save(resetTokenEntity);
            AmazonSES amazonSES = new AmazonSES();
            amazonSES.sendResetPassword(userEntity.getEmail(), token, userEntity.getFirstName());
            returnValue = true;
            return returnValue;
        }

        return returnValue;
    }

    @Override
    public boolean resetPassword(String password, String token) {
        boolean returnValue = false;

        if(Utils.hasTokenExpired(token)){
            return returnValue;
        }

        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);
        String encodePassword = bCryptPasswordEncoder.encode(password);

        UserEntity userEntity = passwordResetTokenEntity.getUserEntity();
        userEntity.setEncryptedPassword(encodePassword);
        UserEntity saveUser = userRepository.save(userEntity);
        if(saveUser != null){
            passwordResetTokenRepository.delete(passwordResetTokenEntity);
            returnValue = true;
        }
        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getMessage());
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
                userEntity.getEmailVerificationStatus(), true, true, true, new ArrayList<>());
    }
}
