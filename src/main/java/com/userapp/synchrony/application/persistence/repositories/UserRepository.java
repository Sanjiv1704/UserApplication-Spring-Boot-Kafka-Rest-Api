package com.userapp.synchrony.application.persistence.repositories;

import com.userapp.synchrony.application.persistence.document.UserDetailsDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDetailsDocument,Integer> {
    UserDetailsDocument findByUserName(String userName);
}
