package com.ngoctm.app.ws.service;

import com.ngoctm.app.ws.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDto create(UserDto userDto);
    UserDto getUserByEmail(String email);
    UserDto getUserByUserId(String id);
}
