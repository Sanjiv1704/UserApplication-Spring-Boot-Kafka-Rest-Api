package com.userapp.synchrony.synchrony.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="user_detail")
public class UserDetailsDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userId;
    private String userName;
    private String password;
    private String userEmail;
    private String designation;
//    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
//    @JoinTable(name="user_images",
//            joinColumns = {
//            @JoinColumn(name = "user_id")
//            },
//            inverseJoinColumns = {
//            @JoinColumn(name = "image_id")
//            }
//    )
//    private Set<ImageDocument> imageDocuments;
}
