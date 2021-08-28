package com.ngoctm.app.ws.controller;

import com.ngoctm.app.ws.model.request.UserDetailRequestModel;
import com.ngoctm.app.ws.model.response.UserRest;
import com.ngoctm.app.ws.service.UserService;
import com.ngoctm.app.ws.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserRest> getUser(@PathVariable("id") String id) {
        UserDto userDto = userService.getUserByUserId(id);

        UserRest userRest = new UserRest();
        BeanUtils.copyProperties(userDto, userRest);
        return new ResponseEntity<>(userRest, HttpStatus.OK);
    }

    @PostMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserRest> createUser(@RequestBody UserDetailRequestModel user) {
        UserRest returnValue = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);

        UserDto createUser = userService.create(userDto);
        BeanUtils.copyProperties(createUser, returnValue);

        return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserRest> updateUser(@PathVariable String id, @RequestBody UserDetailRequestModel requestModel) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(requestModel, userDto);

        UserDto updatedUser = userService.updateUser(id, userDto);
        UserRest userRest = new UserRest();
        BeanUtils.copyProperties(updatedUser, userRest);
        
        return new ResponseEntity<>(userRest, HttpStatus.OK);
    }
}
