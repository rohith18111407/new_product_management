package com.ProductManagement.beststore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ProductManagement.beststore.models.Product;

public interface ProductRepository extends JpaRepository<Product,Integer>{

}
