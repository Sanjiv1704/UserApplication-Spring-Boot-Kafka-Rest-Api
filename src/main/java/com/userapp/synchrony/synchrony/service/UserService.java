package com.userapp.synchrony.synchrony.service;

import com.userapp.synchrony.synchrony.dto.UserDetailsDTO;
import com.userapp.synchrony.synchrony.persistence.document.UserDetailsDocument;
import com.userapp.synchrony.synchrony.persistence.repositories.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.Objects;
import java.util.logging.Logger;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;


    private static final Logger logger = (Logger) LoggerFactory.getLogger(UserService.class);

    public UserDetailsDocument createUserDetails(UserDetailsDocument userDetailsDocument){
        if(Objects.nonNull(userDetailsDocument)){
            UserDetailsDocument checkDocument =userRepository.findByUserName(userDetailsDocument.getUserName());
            if(Objects.nonNull(checkDocument)){
                return userRepository.save(userDetailsDocument);
            }else{
                throw new RestClientException("Document already present with same UserName-->"+userDetailsDocument.getUserName());
            }

        }else{
            throw new RestClientException("Document is Null");
        }
    }

    public UserDetailsDocument getUserByUserName(String userName){
        if(Objects.nonNull(userName) || userName.isEmpty()){
            UserDetailsDocument resDoc= userRepository.findByUserName(userName);
            return resDoc;
        }else{
            throw new RestClientException("UserName is Null or Empty");
        }
    }

}
