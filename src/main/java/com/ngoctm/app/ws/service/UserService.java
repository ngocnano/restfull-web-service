package com.ngoctm.app.ws.service;

import com.ngoctm.app.ws.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    public List<UserDto> getUsers(int page, int limit);
    public UserDto create(UserDto userDto);
    public UserDto getUserByEmail(String email);
    public UserDto getUserByUserId(String id);
    public UserDto updateUser(String id, UserDto userDto);
    public void DeleteUser(String id);
}
