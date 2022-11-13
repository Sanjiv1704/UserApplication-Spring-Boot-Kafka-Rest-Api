package com.userapp.synchrony.synchrony.controller;

import com.userapp.synchrony.synchrony.dto.ImageResponseDoc;
import com.userapp.synchrony.synchrony.dto.UserDetailsDTO;
import com.userapp.synchrony.synchrony.persistence.document.UserDetailsDocument;
import com.userapp.synchrony.synchrony.service.UserService;
import org.apache.catalina.User;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.print.ServiceUI;
import java.io.IOException;
import java.util.Base64;
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
    private static final String IMGUR_CLIENT_ID ="33cfb47eb320ec8";
//    private static final Logger logger = (Logger) LoggerFactory.getLogger(UserController.class);


    @PostMapping("/createUserDetails")
    ResponseEntity<UserDetailsDocument>createUserDetail(@RequestPart("product") UserDetailsDTO userDetailsDTO, @RequestPart("image") MultipartFile file){
        UserDetailsDocument requestDoc = new UserDetailsDocument();
        BeanUtils.copyProperties(userDetailsDTO,requestDoc);
        UserDetailsDocument userDetailsDocument =userService.createUserDetails(requestDoc);

        if(Objects.nonNull(userDetailsDocument)) {
            kafkaTemplate.send(TOPIC, userDetailsDocument);
//            logger.info("Successfully published");
        }
        return new ResponseEntity<>(userDetailsDocument, HttpStatus.CREATED);
    }


    public ImageResponseDoc uploadImageImgur(MultipartFile file) throws IOException {
        byte[] encode = Base64.getEncoder().encode(file.getBytes());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Client-ID " + IMGUR_CLIENT_ID);
        HttpEntity<MultipartFile> entity = new HttpEntity<>(encode, headers);
        ResponseEntity<ImageResponseDoc> response = restTemplate.exchange("https://api.imgur.com/3/image", HttpMethod.POST, entity, ImageResponseDoc.class);
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

//    @PostMapping("/updateUserDetails")
//    ResponseEntity<UserDetailsDocument>updateUserDetails(@RequestBody UserDetailsDocument userDetailsDocument){
//    }
}
