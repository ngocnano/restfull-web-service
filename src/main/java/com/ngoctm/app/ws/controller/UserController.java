package com.ngoctm.app.ws.controller;

import com.ngoctm.app.ws.model.request.UserDetailRequestModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public String getUsers(){
        return null;
    }

    @PostMapping
    public String createUser(@RequestBody UserDetailRequestModel user){

        return "";
    }
}
