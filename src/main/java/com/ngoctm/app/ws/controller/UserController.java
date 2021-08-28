package com.ngoctm.app.ws.controller;

import com.ngoctm.app.ws.model.request.UserDetailRequestModel;
import com.ngoctm.app.ws.model.response.UserRest;
import com.ngoctm.app.ws.service.UserService;
import com.ngoctm.app.ws.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserRest> getUser(@PathVariable("id") String id){
        UserDto userDto = userService.getUserByUserId(id);

        UserRest userRest = new UserRest();
        BeanUtils.copyProperties(userDto, userRest);
        return new ResponseEntity<UserRest>(userRest, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserRest> createUser(@RequestBody UserDetailRequestModel user){
        UserRest returnValue = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);

        UserDto createUser = userService.create(userDto);
        BeanUtils.copyProperties(createUser, returnValue);

        return new ResponseEntity<UserRest>(returnValue, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public String updateUser(@PathVariable String id){
        return null;
    }
}
