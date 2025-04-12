package com.talhaatif.budgettracker.repositories;


import com.talhaatif.budgettracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    User findByUserName(String userName);
    boolean existsByEmail(String email);
}
