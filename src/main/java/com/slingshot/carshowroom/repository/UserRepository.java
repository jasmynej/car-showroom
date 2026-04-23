package com.slingshot.carshowroom.repository;

import com.slingshot.carshowroom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository&lt;User, Integer&gt; {
    Optional&lt;User&gt; findByEmail(String email);
    boolean existsByEmail(String email);
}
