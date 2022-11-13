package com.userapp.synchrony.synchrony.service;

import com.userapp.synchrony.synchrony.dto.ImageResponseDoc;
import com.userapp.synchrony.synchrony.persistence.document.ImageDocument;
import com.userapp.synchrony.synchrony.persistence.document.UserDetailsDocument;
import com.userapp.synchrony.synchrony.persistence.repositories.ImageRepository;
import com.userapp.synchrony.synchrony.persistence.repositories.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ImageRepository imageRepository;

    private static final String IMGUR_CLIENT_ID = "33cfb47eb320ec8";


    public UserDetailsDocument createUserDetails(UserDetailsDocument userDetailsDocument) {
        if (Objects.nonNull(userDetailsDocument)) {
            UserDetailsDocument checkDocument = userRepository.findByUserName(userDetailsDocument.getUserName());
            if (!Objects.nonNull(checkDocument)) {
                return userRepository.save(userDetailsDocument);

            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Document already present with Username:" + userDetailsDocument.getUserName());
            }

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Document Provided Is Null");
        }
    }

    public UserDetailsDocument getUserByUserName(String userName) {
        if (Objects.nonNull(userName) || userName.isEmpty()) {
            UserDetailsDocument resDoc = userRepository.findByUserName(userName);
            return resDoc;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserName is Null or Empty");
        }
    }

    public UserDetailsDocument updateUserDetails(UserDetailsDocument userDetailsDocument) {
        UserDetailsDocument olderDocument = userRepository.findByUserName(userDetailsDocument.getUserName());
        if (Objects.nonNull(olderDocument)) {
            BeanUtils.copyProperties(userDetailsDocument, olderDocument);
            return userRepository.save(olderDocument);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
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
        body.add("image", new FileSystemResource(fileToSend));
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<ImageResponseDoc> response = restTemplate.exchange("https://api.imgur.com/3/upload", HttpMethod.POST, entity, ImageResponseDoc.class);
        return response.getBody();
    }

    public ImageDocument getImageDetails(String imageId) {
        return imageRepository.findByImageId(imageId);
    }

    public UserDetailsDocument deleteImageFromUser(String userName, String deleteHashId) {
        UserDetailsDocument document = userRepository.findByUserName(userName);
        String status = null;
        if (Objects.nonNull(document)) {
            status = deleteImageFromImgur(deleteHashId);
        }
        if (status.equalsIgnoreCase("success")) {
            for (ImageDocument doc : document.getImageDocuments()) {
                if (doc.getDeleteHashId().equalsIgnoreCase(deleteHashId)) {
                    document.getImageDocuments().remove(doc);
                    imageRepository.deleteById(doc.getId());
                }
            }
            return userRepository.save(document);
        } else {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
    }

    public String deleteImageFromImgur(String deleteHashId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Client-ID " + IMGUR_CLIENT_ID);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Object> response = restTemplate.exchange("https://api.imgur.com/3/image" + "/" + deleteHashId, HttpMethod.DELETE, entity, Object.class);
        Integer status = response.getStatusCodeValue();
        if (status == 200) {
            return "success";
        } else {
            return "failure";
        }
    }

    public UserDetailsDocument deleteUser(String userName) {
        UserDetailsDocument userDetailsDocument = userRepository.findByUserName(userName);
        if (Objects.nonNull(userDetailsDocument)) {
            userRepository.deleteById(userDetailsDocument.getUserId());
            return userDetailsDocument;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
