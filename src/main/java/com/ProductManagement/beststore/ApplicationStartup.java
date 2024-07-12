package com.ProductManagement.beststore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Component;

import com.ProductManagement.beststore.models.MyUser;
import com.ProductManagement.beststore.models.Product;
import com.ProductManagement.beststore.repository.ProductRepository;
import com.ProductManagement.beststore.repository.UserRepository;

@Component
public class ApplicationStartup {
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private UserDetailsService userDetailsService;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	
	public void CreateUsers(String username,String password,String role)
	{
		((JdbcUserDetailsManager) userDetailsService).createUser(User.withUsername(username)
	            .password(passwordEncoder.encode(password))
	            .roles(role)
	            .build());
	    
	    MyUser newUser = new MyUser();
	    newUser.setName(username); // Assuming username is stored in name field
	    newUser.setPassword(passwordEncoder.encode(password));
	    newUser.setRole(role);

	    // Save the user using UserRepository
	    userRepository.save(newUser);
	}
	
	public void products(String name,String brand,String category,double price,String description,String storageFileName)
    {
    	Product product=new Product();
		product.setName(name);
		product.setBrand(brand);
		product.setCategory(category);
		product.setPrice(price);
		product.setDescription(description);
		product.setImageFileName(storageFileName);
		
		productRepository.save(product);
    }
	
	@EventListener(ApplicationReadyEvent.class)
	public void ApplicationStart()
	{
		CreateUsers("Ricson","123","USER");
		CreateUsers("Rayhan","123","USER");
		CreateUsers("Ragul","123","USER");
		
		products("Jupiter 125","TVS","Scooters",79299,"Engine Capacity=124.76cc, Power=6kW @ 6500rpm, Weight=108kgs","Jupiter125.jfif");
		products("Ntorq","TVS","Scooters",84636,"Engine Capacity=124.8cc, Power=6.9kW @ 7000rpm, Weight=110kgs","Ntorq125.jfif");
		products("Jupiter","TVS","Scooters",73650,"Engine Capacity=109.7cc, Power=5.8kW @ 7500rpm, Weight=107kgs","Jupiter.jfif");
		products("Zest 110","TVS","Scooters",74476,"Engine Capacity=109.7cc, Power=5.75kW @ 7500rpm, Weight=103kgs","Zest110.jfif");
		products("Scooty Pep+","TVS","Scooters",65514,"Engine Capacity=87.8cc, Power=4.0kW @ 6500rpm, Weight=93kgs","ScootyPep.jfif");
		
		products("Apache RTR 310","TVS","MotorCycles",249990,"Engine Capacity=312.12cc, Power=26.16kW @ 9700rpm, Weight=169kgs","ApacheRTR.jfif");
		products("Apache RR 312","TVS","MotorCycles",272000,"Engine Capacity=312.00cc, Power=33.5 bhp, Weight=174kgs","ApacheRR.jfif");
		products("Ronin","TVS","MotorCycles",149200,"Engine Capacity=225.9cc, Power=20.4 ps, Weight=160kgs","Ronin.jfif");
		products("Raider","TVS","MotorCycles",95219,"Engine Capacity=124.8cc, Power=8.37kW @ 7500rpm, Weight=123kgs","Raider.jfif");
		products("Radeon","TVS","MotorCycles",62630,"Engine Capacity=109.7cc, Power=6.03kW @ 7350rpm, Weight=113kgs","Radeon.jfif");
		products("StaR City +","TVS","MotorCycles",75000,"Engine Capacity=109.7cc, Power=6.03kW @ 7350rpm, Weight=116kgs","StarCity.jfif");
		products("Sport","TVS","MotorCycles",71383,"Engine Capacity=309.7cc, Power=6.03kW @ 7350rpm, Weight=112kgs","Sport.jfif");

		products("XL 100","TVS","MotorCycles",44999,"Engine Capacity=99.7cc, Power=3.2kW @ 6000rpm, Weight=88kgs","Xl100.jfif");
		
		products("X","TVS","Electric",249990,"Real Range(Eco)=140km*, Battery Capacity=3h 40m, Top Speed=105km/h","X.jfif");
		products("iQube","TVS","Electric",84999,"Real Range(Eco)=150km*, Battery Capacity=4h 06m, Top Speed=82km/h","iQube.jfif");
		
		products("King","TVS","Electric",84999,"Engine Capacity=199cc, Power=6bhp, Weight=360kgs","King.jfif");

	}
}	
