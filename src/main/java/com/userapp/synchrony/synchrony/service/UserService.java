package com.userapp.synchrony.synchrony.service;

import com.userapp.synchrony.synchrony.dto.UserDetailsDTO;
import com.userapp.synchrony.synchrony.persistence.document.UserDetailsDocument;
import com.userapp.synchrony.synchrony.persistence.repositories.UserRepository;
import org.apache.catalina.User;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.logging.Logger;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;


    public UserDetailsDocument createUserDetails(UserDetailsDocument userDetailsDocument){
        if(Objects.nonNull(userDetailsDocument)){
            UserDetailsDocument checkDocument =userRepository.findByUserName(userDetailsDocument.getUserName());
            if(!Objects.nonNull(checkDocument)){
                return userRepository.save(userDetailsDocument);

            }else{
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Document already present with Username:"+userDetailsDocument.getUserName());
            }

        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Document Provided Is Null");
        }
    }

    public UserDetailsDocument getUserByUserName(String userName){
        if(Objects.nonNull(userName) || userName.isEmpty()){
            UserDetailsDocument resDoc= userRepository.findByUserName(userName);
            return resDoc;
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"UserName is Null or Empty");
        }
    }

//    public UserDetailsDocument updateUserDetails(UserDetailsDocument userDetailsDocument){
//    }

}
