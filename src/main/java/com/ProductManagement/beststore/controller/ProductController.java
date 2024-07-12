package com.ProductManagement.beststore.controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ProductManagement.beststore.models.MyUser;
import com.ProductManagement.beststore.models.Product;
import com.ProductManagement.beststore.models.ProductCreation;
import com.ProductManagement.beststore.repository.ProductRepository;
import com.ProductManagement.beststore.repository.UserRepository;

import jakarta.validation.Valid;

@Controller
public class ProductController {
	
	@Autowired
	private ProductRepository repository;
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private UserDetailsManager userDetailsManager;
	
	public ProductController(ProductRepository repository)
	{
		this.repository=repository;
	}
	

	@GetMapping("/admin/users")
	public String listUsers(Model model) {
        
		try {
            // Retrieve users from MyUserRepository
            List<MyUser> users = userRepository.findAll();

            // Convert MyUser to UserDetails to mimic Spring Security's User object (if necessary)
            List<UserDetails> userDetailsList = users.stream()
                    .map(user -> User.withUsername(user.getName())
                            .password(user.getPassword())
                            .roles(user.getRole())
                            .build())
                    .collect(Collectors.toList());

            model.addAttribute("users", userDetailsList);
            return "webDetails/adminUsersPage";
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "webDetails/adminUsersPage"; // Handle exceptions gracefully
        }
	}

	
	@GetMapping("/login")
	public String showLoginPage()
	{
		return "webDetails/LoginPage";
	}
	
	@GetMapping("/register")
	public String showRegisterPage()
	{
		return "webDetails/RegisterPage";
	}
	
	@GetMapping("/home")
	public String showHomePage()
	{
		return "webDetails/HomePage";
	}
	
	// returns the name of the html file to be returned
	@GetMapping("/admin/jpa/products")
	public String showProductList(Model model)
	{
		
		List<Product> products=repository.findAll();
		model.addAttribute("products",products);	//products list added to model products will be accessible to index.html
		return "products/adminHomePage";	//products folder present in the templates folder having index.html within it
	}
	
	//endpoint name and the .html page name should be different
	@GetMapping("/admin/jpa/products/create")
	public String showCreatePage(Model model)
	{
		ProductCreation productCreation=new ProductCreation();
		model.addAttribute("productCreation",productCreation);
		return "products/CreateProduct";
	}
	
	//submit button requires POST, need to validate the object of ProductCreation hence @valid is used, to check if there is any validation error add parameter BindingResult
	@PostMapping("/admin/jpa/products/create")
	public String createProduct(@Valid @ModelAttribute ProductCreation productCreation,BindingResult result)
	{
		//check whether the productCreation object has error related to imageFile beeing uploaded in form
		if(productCreation.getImageFile().isEmpty())
		{
			result.addError(new FieldError("productCreation","imageFile","The image file is required"));
		}
		
		//check whether there is any error
		if(result.hasErrors())
		{
			return "products/CreateProduct";
		}
		
		//save image file
		MultipartFile image=productCreation.getImageFile();
		String storageFileName=image.getOriginalFilename();
		
		try
		{
			String uploadDir="public/images/";
			Path uploadPath=Paths.get(uploadDir);
			
			if(!Files.exists(uploadPath))
			{
				Files.createDirectories(uploadPath);
			}
			
			try(InputStream inputStream = image.getInputStream())
			{
				Files.copy(inputStream, Paths.get(uploadDir+storageFileName),StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception: "+ex.getMessage());
		}
		
		
		// save the product in database
		Product product=new Product();
		product.setName(productCreation.getName());
		product.setBrand(productCreation.getBrand());
		product.setCategory(productCreation.getCategory());
		product.setPrice(productCreation.getPrice());
		product.setDescription(productCreation.getDescription());
		product.setImageFileName(storageFileName);
		
		repository.save(product);
		
		return "redirect:/admin/jpa/products";
	}
	
	//update the product
	@GetMapping("/admin/jpa/products/update")
	public String showEditPage(Model model, @RequestParam int id)
	{
		try {
			Product product=repository.findById(id).get();
			model.addAttribute("product",product);
			
			ProductCreation productCreation=new ProductCreation();
			productCreation.setName(product.getName());
			productCreation.setBrand(product.getBrand());
			productCreation.setCategory(product.getCategory());
			productCreation.setPrice(product.getPrice());
			productCreation.setDescription(product.getDescription());
			
			model.addAttribute("productCreation",productCreation);
		}
		catch(Exception ex)
		{
			System.out.println("Exception: "+ex.getMessage());
			return "redirect:/admin/jpa/products";				// Use / to append after localhost:8081
		}
		return "products/EditProduct";
	}
	
	@PostMapping("/admin/jpa/products/update")
	public String updateProduct(Model model,@RequestParam int id,@Valid @ModelAttribute ProductCreation productCreation,BindingResult result)
	{
		try	//connecting to the database hence use try...catch() block
		{
			Product product=repository.findById(id).get();
			model.addAttribute("product",product);		//reading product details from page
			
			if(result.hasErrors())
			{
				return "products/EditProduct";
			}
			
			//check whether there is a new image added
			if(!productCreation.getImageFile().isEmpty())
			{
				//delete the old image file
				String uploadDir="public/images/";
				Path oldImagePath=Paths.get(uploadDir+product.getImageFileName()); //previous filename is available at product object in the database
				
				try
				{
					Files.delete(oldImagePath);
				}
				catch(Exception ex)
				{
					System.out.println("Exception: "+ex.getMessage());
				}
				
				//save new Image file
				MultipartFile image=productCreation.getImageFile();	
				String storageFileName=image.getOriginalFilename();
				
				try(InputStream inputStream = image.getInputStream())
				{
					Files.copy(inputStream, Paths.get(uploadDir+storageFileName),StandardCopyOption.REPLACE_EXISTING);
				}
				
				product.setImageFileName(storageFileName);
				
				
				
			}
			
			// save the product in database
			product.setName(productCreation.getName());
			product.setBrand(productCreation.getBrand());
			product.setCategory(productCreation.getCategory());
			product.setPrice(productCreation.getPrice());
			product.setDescription(productCreation.getDescription());
					
			repository.save(product);
		}
		catch(Exception ex)
		{
			System.out.println("Exception : "+ex.getMessage());
		}
		
		return "redirect:/admin/jpa/products";
	}
	
	
	@GetMapping("/admin/jpa/products/delete")
	public String deleteProduct(@RequestParam int id)
	{
		try
		{
			Product product=repository.findById(id).get();
			
			//delete the product image from public/images folder
			Path imagePath=Paths.get("public/images/"+product.getImageFileName());
			
			try
			{
				Files.delete(imagePath);
			}
			catch(Exception ex)
			{
				System.out.println("Exception: "+ex.getMessage());
			}
			
			//delete the product from database
			repository.delete(product);
			
		}
		catch(Exception ex)
		{
			System.out.println("Exception: "+ex.getMessage());
		}
		
		return "redirect:/admin/jpa/products";
	}
	
	 @GetMapping("/admin/users/remove")
	    public String removeUser(@RequestParam String username) {
		 
	        try {
	            // Find user by username in the repository
	            MyUser user = userRepository.findByName(username);

	            // If user exists, delete from repository
	            if (user != null) {
	                userRepository.delete(user);
	            }
	        } catch (Exception ex) {
	            System.out.println("Exception: " + ex.getMessage());
	        }
	        
	        try {
	            // Delete user from UserDetailsManager
	            userDetailsManager.deleteUser(username);
	        } catch (Exception ex) {
	            System.out.println("Exception: " + ex.getMessage());
	        }
	        
	        return "redirect:/admin/users"; // Redirect back to admin users page
	    }
	
	
	@GetMapping("/admin/about")
	public String showAboutPage()
	{
		return "webDetails/aboutPage";
	}
	
	@GetMapping("/admin/contact")
	public String showContactPage()
	{
		return "webDetails/contactPage";
	}
	
}
