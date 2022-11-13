package com.userapp.synchrony.synchrony.persistence.repositories;

import com.userapp.synchrony.synchrony.persistence.document.UserDetailsDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDetailsDocument,Integer> {
    UserDetailsDocument findByUserName(String userName);
}
