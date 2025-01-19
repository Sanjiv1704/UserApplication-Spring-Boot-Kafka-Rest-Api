package com.userapp.synchrony.synchrony.controller;

import com.userapp.synchrony.synchrony.dto.ImageResponseDoc;
import com.userapp.synchrony.synchrony.dto.UserDetailsDTO;
import com.userapp.synchrony.synchrony.dto.UserImageTemplate;
import com.userapp.synchrony.synchrony.persistence.document.ImageDocument;
import com.userapp.synchrony.synchrony.persistence.document.UserDetailsDocument;
import com.userapp.synchrony.synchrony.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/userApplication")
public class UserController {
    private static final String TOPIC = "USER_DETAILS";
    @Autowired
    UserService userService;
    @Autowired
    KafkaTemplate kafkaTemplate;

    /**
     * @param userDetailsDTO
     * @param file
     * @return endpoint will return userDetails and image related to user.
     */
    @PostMapping("/createUserDetails")
    ResponseEntity<UserDetailsDocument> createUserDetail(@RequestPart("json") UserDetailsDTO userDetailsDTO, @RequestPart("image") MultipartFile file) {
        UserDetailsDocument requestDoc = new UserDetailsDocument();
        BeanUtils.copyProperties(userDetailsDTO, requestDoc);
        UserImageTemplate template = new UserImageTemplate();
        try {
            ImageResponseDoc image = userService.uploadImageImgur(file);
            if (Objects.nonNull(image.getData())) {
                Set<ImageDocument> imageDocumentSet = new HashSet<>();
                ImageDocument doc = new ImageDocument();
                doc.setImageId(image.getData().getId());
                doc.setName(file.getName());
                doc.setType(file.getContentType());
                doc.setImageUrl(image.getData().getLink());
                doc.setDeleteHashId(image.getData().getDeletehash());
                imageDocumentSet.add(doc);
                requestDoc.setImageDocuments(imageDocumentSet);

                template.setUserName(userDetailsDTO.getUserName());
                template.setImageName(file.getName());
                template.setImageUrl(doc.getImageUrl());
                template.setOpType("I");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        UserDetailsDocument userDetailsDocument = userService.createUserDetails(requestDoc);

        if (Objects.nonNull(userDetailsDocument)) {
            kafkaTemplate.send(TOPIC, template);
        }
        return new ResponseEntity<>(userDetailsDocument, HttpStatus.CREATED);
    }


    /**
     * @param userName
     * @return userDetailsDocument by userName
     */
    @GetMapping("/getUserByUserName/{userName}")
    ResponseEntity<UserDetailsDocument> getUserDetails(@PathVariable String userName) {
        UserDetailsDocument userDetailsDocument = userService.getUserByUserName(userName);
        if (Objects.nonNull(userDetailsDocument)) {
            return new ResponseEntity<>(userDetailsDocument, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new UserDetailsDocument(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * @param userDetailsDocument
     * @param file
     * @return updates the all the details of user
     */
    @PostMapping("/updateUserDetails")
    ResponseEntity<UserDetailsDocument> updateUserDetails(@RequestPart("json") UserDetailsDocument userDetailsDocument, @RequestPart("image") MultipartFile file) {
        UserImageTemplate template = new UserImageTemplate();
        try {
            ImageResponseDoc image = userService.uploadImageImgur(file);
            if (Objects.nonNull(image.getData())) {
                Set<ImageDocument> imageDocumentSet = new HashSet<>();
                ImageDocument doc = new ImageDocument();
                doc.setImageId(image.getData().getId());
                doc.setName(file.getName());
                doc.setType(file.getContentType());
                doc.setImageUrl(image.getData().getLink());
                doc.setDeleteHashId(image.getData().getDeletehash());
                imageDocumentSet.addAll(userDetailsDocument.getImageDocuments());
                imageDocumentSet.add(doc);
                userDetailsDocument.setImageDocuments(imageDocumentSet);

                template.setUserName(userDetailsDocument.getUserName());
                template.setImageName(file.getName());
                template.setImageUrl(doc.getImageUrl());
                template.setOpType("U");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        UserDetailsDocument responseDoc = userService.updateUserDetails(userDetailsDocument);
        if (Objects.nonNull(responseDoc)) {
            kafkaTemplate.send(TOPIC, template);
            log.info("Successfully published");
        }
        return new ResponseEntity<>(responseDoc, HttpStatus.ACCEPTED);
    }

    /**
     * @param imageId
     */
    @GetMapping("/getImageDetails/{imageId}")
    public ResponseEntity<ImageDocument> getImageDetails(@PathVariable String imageId) {
        ImageDocument responseDoc = userService.getImageDetails(imageId);
        if (Objects.nonNull(responseDoc)) {
            return new ResponseEntity<>(responseDoc, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ImageDocument(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * @param userName
     * @param deleteHashId
     * @return return a updated document after deleting Image Details using deleteHashId from userDocument.
     */
    @PostMapping("/deleteImageFromUser/{userName}/{deleteHashId}")
    public ResponseEntity<UserDetailsDocument> deleteImageForUser(@PathVariable String userName, @PathVariable String deleteHashId) {
        UserDetailsDocument responseDoc = userService.deleteImageFromUser(userName, deleteHashId);
        return new ResponseEntity<>(responseDoc, HttpStatus.OK);
    }

    /**
     * @param userName
     */
    @DeleteMapping("/deleteUser/{userName}")
    public ResponseEntity<UserDetailsDocument> deleteUser(@PathVariable String userName) {
        UserDetailsDocument userDetailsDocument = userService.deleteUser(userName);
        return new ResponseEntity<>(userDetailsDocument, HttpStatus.OK);
    }
}
