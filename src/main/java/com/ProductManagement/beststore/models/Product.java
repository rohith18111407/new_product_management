package com.ProductManagement.beststore.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity	//To make a table in H2 database
public class Product {
	
	@Id				// defines the primary key
	@GeneratedValue(strategy=GenerationType.IDENTITY)	// to automatically increment id value (no need to enter manually)
	private Integer id;
	private String name;
	private String brand;
	private String category;
	private double price;
	
	private String description;
	private String imageFileName;
	
	//Use of jpa requires a default constructor
	public Product() {
		
	}

	public Product(Integer id, String name, String brand, String category, double price, String description,
			String imageFileName) {
		super();
		this.id = id;
		this.name = name;
		this.brand = brand;
		this.category = category;
		this.price = price;
		this.description = description;
		this.imageFileName = imageFileName;
	}



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageFileName() {
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", brand=" + brand + ", category=" + category + ", price="
				+ price + ", description=" + description + ", imageFileName=" + imageFileName + "]";
	}

	
	
	
}
