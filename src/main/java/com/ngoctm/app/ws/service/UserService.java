package com.ngoctm.app.ws.service;

import com.ngoctm.app.ws.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    List<UserDto> getUsers(int page, int limit);
    UserDto create(UserDto userDto);
    UserDto getUserByEmail(String email);
    UserDto getUserByUserId(String id);
    UserDto updateUser(String id, UserDto userDto);
    void DeleteUser(String id);
    boolean verifyEmailToken(String token);
    boolean requestPasswordReset(String email);
    boolean resetPassword(String password, String token);
}
