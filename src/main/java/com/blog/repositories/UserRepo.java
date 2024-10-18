package com.blog.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blog.entities.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

	// to get the user using userName i.e 'email'

	Optional<User> findByEmail(String email);

}