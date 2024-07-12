package com.ProductManagement.beststore.controller;

import com.ProductManagement.beststore.models.MyUser;
import com.ProductManagement.beststore.models.Product;
import com.ProductManagement.beststore.repository.ProductRepository;
import com.ProductManagement.beststore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/profile")
    public String showUserProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        MyUser user = getUserByUsername(username);
        model.addAttribute("user", user);
        return "UserSection/userProfilePage";
    }

    @GetMapping("/user/jpa/products")
    public String showProductList(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "UserSection/userHomePage";
    }

    @GetMapping("/user/about")
    public String showAboutPage() {
        return "UserSection/userAboutPage";
    }

    @GetMapping("/user/contact")
    public String showContactPage() {
        return "UserSection/userContactPage";
    }


    @GetMapping("/user/cart")
    public String showCart(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        MyUser user = userRepository.findByName(username);
        model.addAttribute("cart", user.getCart());
        return "UserSection/userCartPage";
    }

    @PostMapping("/user/cart/addToCart")
    public String addToCartPost(@RequestParam Integer productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        MyUser user = userRepository.findByName(username);
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            user.getCart().add(product);
            userRepository.save(user);
            return "redirect:/user/cart"; // Redirect to the cart page
        } else {
            return "redirect:/user/jpa/products"; // Redirect to products page if product not found
        }
    }

    private MyUser getUserByUsername(String username) {
        org.springframework.security.core.userdetails.User userDetails =
                (org.springframework.security.core.userdetails.User) userDetailsService.loadUserByUsername(username);
        MyUser myUser = new MyUser();
        myUser.setName(userDetails.getUsername());
        myUser.setPassword(userDetails.getPassword());
        myUser.setRole("ROLE_USER");
        return myUser;
    }
    
    @PostMapping("/user/cart/removeFromCart")
    public String removeFromCart(@RequestParam Integer productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        MyUser user = userRepository.findByName(username);
        Product productToRemove = productRepository.findById(productId).orElse(null);
        if (productToRemove != null) {
            user.getCart().remove(productToRemove);
            userRepository.save(user);
        }
        return "redirect:/user/cart";
    }
}
