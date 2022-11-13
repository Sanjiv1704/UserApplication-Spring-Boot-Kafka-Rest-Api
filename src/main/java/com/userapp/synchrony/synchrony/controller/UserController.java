package com.userapp.synchrony.synchrony.controller;

import com.userapp.synchrony.synchrony.dto.ImageResponseDoc;
import com.userapp.synchrony.synchrony.dto.UserDetailsDTO;
import com.userapp.synchrony.synchrony.persistence.document.ImageDocument;
import com.userapp.synchrony.synchrony.persistence.document.UserDetailsDocument;
import com.userapp.synchrony.synchrony.service.UserService;
import org.apache.catalina.User;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.print.ServiceUI;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
    ResponseEntity<UserDetailsDocument>createUserDetail(@RequestPart("json") UserDetailsDTO userDetailsDTO, @RequestPart("image") MultipartFile file){
        UserDetailsDocument requestDoc = new UserDetailsDocument();
        BeanUtils.copyProperties(userDetailsDTO,requestDoc);
        try {
            ImageResponseDoc image =uploadImageImgur(file);
            if(Objects.nonNull(image.getData())) {
                Set<ImageDocument> imageDocumentSet = new HashSet<>();
                ImageDocument doc = new ImageDocument();
                doc.setImageId(image.getData().getId());
                doc.setName(file.getName());
                doc.setType(file.getContentType());
                doc.setImageUrl(image.getData().getLink());
                doc.setDeleteHashId(image.getData().getDeletehash());
                imageDocumentSet.add(doc);
                requestDoc.setImageDocuments(imageDocumentSet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        UserDetailsDocument userDetailsDocument =userService.createUserDetails(requestDoc);

        if(Objects.nonNull(userDetailsDocument)) {
            kafkaTemplate.send(TOPIC, userDetailsDocument);
//            logger.info("Successfully published");
        }
        return new ResponseEntity<>(userDetailsDocument, HttpStatus.CREATED);
    }


    public ImageResponseDoc uploadImageImgur(MultipartFile file) throws IOException {
        Path tempFile = Files.createTempFile(null, null);

        Files.write(tempFile, file.getBytes());
        File fileToSend = tempFile.toFile();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization", "Client-ID " + IMGUR_CLIENT_ID);
        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        body.add("image",new FileSystemResource(fileToSend));
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<ImageResponseDoc> response = restTemplate.exchange("https://api.imgur.com/3/upload", HttpMethod.POST, entity, ImageResponseDoc.class);
        return response.getBody();
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
