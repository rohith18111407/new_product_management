package com.ProductManagement.beststore.repository;

import com.ProductManagement.beststore.models.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<MyUser, Integer> {

	MyUser findByName(String username);
	
}

