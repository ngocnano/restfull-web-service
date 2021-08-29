package com.ngoctm.app.ws.controller;

import com.ngoctm.app.ws.model.request.PasswordResetModel;
import com.ngoctm.app.ws.model.request.PasswordResetRequestModel;
import com.ngoctm.app.ws.model.request.UserDetailRequestModel;
import com.ngoctm.app.ws.model.response.AddressRest;
import com.ngoctm.app.ws.model.response.UserRest;
import com.ngoctm.app.ws.service.AddressService;
import com.ngoctm.app.ws.service.UserService;
import com.ngoctm.app.ws.shared.dto.AddressDto;
import com.ngoctm.app.ws.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<UserRest>> getUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                                                   @RequestParam(value = "limit", defaultValue = "10") int limit) {

        ModelMapper modelMapper = new ModelMapper();
        if (page > 0) {
            page = page - 1;
        }
        List<UserDto> userDtos = userService.getUsers(page, limit);
        List<UserRest> userRests = new ArrayList<>();
        for (UserDto userDto : userDtos) {
            UserRest userRest = modelMapper.map(userDto, UserRest.class);
            userRests.add(userRest);
        }

        return new ResponseEntity<>(userRests, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserRest> getUser(@PathVariable("id") String id) {

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = userService.getUserByUserId(id);

        UserRest userRest = modelMapper.map(userDto, UserRest.class);

        for (AddressRest addressRest : userRest.getAddresses()){
            Link addressLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                    .getUserAddress(id, addressRest.getAddressId())).withRel("address");
            addressRest.add(addressLink);
        }

        return new ResponseEntity<>(userRest, HttpStatus.OK);
    }

    @PostMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserRest> createUser(@RequestBody UserDetailRequestModel user) {

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(user, UserDto.class);

        UserDto createUser = userService.create(userDto);
        UserRest returnValue = modelMapper.map(createUser, UserRest.class);

        return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserRest> updateUser(@PathVariable String id, @RequestBody UserDetailRequestModel requestModel) {
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(requestModel, UserDto.class);

        UserDto updatedUser = userService.updateUser(id, userDto);
        UserRest userRest = modelMapper.map(updatedUser, UserRest.class);

        return new ResponseEntity<>(userRest, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity deleteUser(@PathVariable String id) {
        userService.DeleteUser(id);

        return new ResponseEntity(id, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/address",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<AddressRest>> getUserAddresses(@PathVariable("id") String id) {

        ModelMapper modelMapper = new ModelMapper();
        List<AddressDto> addressDtos = addressService.getAddresses(id);

        List<AddressRest> addressRests = new ArrayList<>();
        for (AddressDto addressDto : addressDtos){
            AddressRest addressRest = modelMapper.map(addressDto, AddressRest.class);

            Link userLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                    .getUser(id)).withRel("user");
            Link addressLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                    .getUserAddress(id, addressRest.getAddressId())).withRel("address");
            addressRest.add(userLink);
            addressRest.add(addressLink);

            addressRests.add(addressRest);
        }

        return new ResponseEntity<>(addressRests, HttpStatus.OK);
    }

    @GetMapping(value = "/{userId}/address/{addressId}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AddressRest> getUserAddress(@PathVariable String addressId,
                                                      @PathVariable String userId) {

        ModelMapper modelMapper = new ModelMapper();
        AddressDto addressDto = addressService.getAddress(addressId);

        AddressRest addressRest = modelMapper.map(addressDto, AddressRest.class);

        Link addressLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                .getUserAddress(addressId, userId)).withSelfRel();
        Link userLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                .getUser(userId)).withRel("user");
        Link addressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                .getUserAddresses(userId)).withRel("addresses");

        addressRest.add(addressLink);
        addressRest.add(userLink);
        addressRest.add(addressesLink);

        return new ResponseEntity<>(addressRest, HttpStatus.OK);
    }

    @GetMapping(value = "/email",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> verifyEmailToken(@RequestParam("token") String token) {

        boolean verification = userService.verifyEmailToken(token);
        HttpStatus httpStatus;
        String content;
        if(verification){
            httpStatus = HttpStatus.OK;
            content = "Successful";
        } else {
            httpStatus = HttpStatus.FORBIDDEN;
            content = "fail";
        }
        return new ResponseEntity<>(content, httpStatus);
    }

    @PostMapping(value = "/request-reset-password",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> requestResetPassword(@RequestBody PasswordResetRequestModel requestModel){
        boolean returnValue = userService.requestPasswordReset(requestModel.getEmail());
        HttpStatus httpStatus;
        String content;
        if(returnValue){
            httpStatus = HttpStatus.OK;
            content = "Successful";
        } else {
            httpStatus = HttpStatus.FORBIDDEN;
            content = "fail";
        }
        return new ResponseEntity<>(content, httpStatus);
    }

    @PostMapping(value = "/reset-password",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetModel passwordResetModel){
        boolean returnValue = userService.resetPassword(passwordResetModel.getNewPassword(), passwordResetModel.getToken());
        HttpStatus httpStatus;
        String content;
        if(returnValue){
            httpStatus = HttpStatus.OK;
            content = "Successful";
        } else {
            httpStatus = HttpStatus.FORBIDDEN;
            content = "fail";
        }
        return new ResponseEntity<>(content, httpStatus);
    }

}
