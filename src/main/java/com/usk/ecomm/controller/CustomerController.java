package com.usk.ecomm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.usk.ecomm.dto.LoginRequest;
import com.usk.ecomm.dto.Purchase;
import com.usk.ecomm.dto.Response;
import com.usk.ecomm.entity.Customer;
import com.usk.ecomm.entity.CustomerCart;
import com.usk.ecomm.entity.Order;
import com.usk.ecomm.entity.Product;
import com.usk.ecomm.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/register")
    public Customer register(@RequestBody Customer customer) {
        System.out.println("customer: " + customer.getEmail() + " " + customer);
        return customerService.register(customer);
    }

    @PostMapping("/login")
    public Response login(@RequestBody LoginRequest loginRequest) {
        return customerService.login(loginRequest);
    }

    @GetMapping("/logout")
    public Response logout() {
        return customerService.logout();
    }

    @PostMapping("/cart")
    public Response addCart(@RequestBody CustomerCart customerCart) {
        return customerService.addCart(customerCart);
    }

    @GetMapping("/getCartProducts/{customerId}")
    public List<CustomerCart> getCartProducts(@PathVariable String customerId) {
        return customerService.getCartProducts(customerId);
    }

    @PostMapping("/purchase")
    public Order purchase(@RequestBody Purchase purchase) throws JsonProcessingException {
        System.out.println("working");
        return customerService.purchase(purchase);
    }

    @GetMapping("/dashboard")
    public List<Order> dashboard() {
        return customerService.dashboard();
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String productName) {
        return customerService.searchProducts(productName);
    }

    @PostMapping("/updateDeliveryStatus")
    public Order updateDeliveryStatus(@RequestParam String orderId) throws JsonProcessingException {
        System.out.println("working");
        return customerService.updateDeliveryStatus(orderId);
    }

}
