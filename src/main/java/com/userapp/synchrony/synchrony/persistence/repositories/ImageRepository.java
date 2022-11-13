package com.userapp.synchrony.synchrony.persistence.repositories;

import com.userapp.synchrony.synchrony.persistence.document.ImageDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<ImageDocument,Long> {
    ImageDocument findByImageId(String imageId);
}
