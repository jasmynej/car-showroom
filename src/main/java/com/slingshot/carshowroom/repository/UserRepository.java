package com.slingshot.carshowroom.repository;

import com.slingshot.carshowroom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository&lt;User, Integer&gt; {

    Optional&lt;User&gt; findByEmail(String email);
    
    Optional&lt;User&gt; findByUserId(String userId);
}
