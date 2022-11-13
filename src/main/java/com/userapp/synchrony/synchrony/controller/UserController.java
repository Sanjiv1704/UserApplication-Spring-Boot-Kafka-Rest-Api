package com.userapp.synchrony.synchrony.controller;

import com.userapp.synchrony.synchrony.dto.UserDetailsDTO;
import com.userapp.synchrony.synchrony.persistence.document.UserDetailsDocument;
import com.userapp.synchrony.synchrony.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import javax.print.ServiceUI;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
@RequestMapping("/userApplication")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    KafkaTemplate kafkaTemplate;

    private static final String TOPIC="USER_DETAILS";
    private static final Logger logger = (Logger) LoggerFactory.getLogger(UserService.class);


    @PostMapping("/createUserDetails")
    ResponseEntity<UserDetailsDocument>createUserDetail(@RequestBody UserDetailsDTO userDetailsDTO){
        UserDetailsDocument requestDoc = new UserDetailsDocument();
        BeanUtils.copyProperties(userDetailsDTO,requestDoc);
        UserDetailsDocument userDetailsDocument =userService.createUserDetails(requestDoc);
        if(Objects.nonNull(userDetailsDocument)) {
            kafkaTemplate.send(TOPIC, userDetailsDocument);
            logger.info("Successfully published");
        }
        return new ResponseEntity<>(userDetailsDocument, HttpStatus.CREATED);
    }

    @GetMapping("/getUserByUserName/{userName}")
    ResponseEntity<UserDetailsDocument>getUserDetails(@PathVariable String userName){
        UserDetailsDocument userDetailsDocument =userService.getUserByUserName(userName);
        if(Objects.nonNull(userDetailsDocument)){
            return new ResponseEntity<>(userDetailsDocument,HttpStatus.OK);
        }else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
