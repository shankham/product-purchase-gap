package com.usk.ecomm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usk.ecomm.dto.*;
import com.usk.ecomm.entity.*;
import com.usk.ecomm.feignclient.BankFeignClient;
import com.usk.ecomm.repository.*;
import jakarta.servlet.http.HttpSession;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerCartRepository customerCartRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    TranscationRepository transcationRepository;

    @Autowired
    ProductReposiotry productRepository;

    @Autowired
    HttpSession httpSession;

    @Autowired
    BankFeignClient bankFeignClient;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    KafkaObjectService kafkaObjectService;

    public CustomerService() {
    }

    public Customer getLoggedInCustomer() {
        return (Customer) httpSession.getAttribute("loggedInCustomer");
    }

    public Customer register(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Already Exists");
        }
        return customerRepository.save(customer);
    }

    public Response login(LoginRequest loginRequest) {
        Customer customer = customerRepository.findByEmailAndPassword(loginRequest.getEmail(),
                loginRequest.getPassword());
        Response loginResponse = new Response();
        if (customer == null) {
            loginResponse.setStatus("Failed");
            loginResponse.setMessage("Invailed Credentials");
        } else {
            httpSession.setAttribute("loggedInCustomer", customer);
            loginResponse.setStatus("success");
            loginResponse.setMessage("LoggedIn Successfully");
        }
        return loginResponse;
    }

    public Response addCart(CustomerCart customerCart) {
        Response response = new Response();
        Customer customer = getLoggedInCustomer();
        if (customer == null) {
            response.setStatus("Failed");
            response.setMessage("You must be logged in to add items to cart");
            return response;
        }
        if (!customer.getCustomerId().equals(customerCart.getCustomerId())) {
            throw new RuntimeException("Invalid user");
        }
        Product product = productRepository.findByProductId(customerCart.getProductId());

        if (product == null) {
            response.setMessage("this product is not available please another product");
            response.setStatus("Failed");
            return response;
        }
        if (product.getQuantity() < customerCart.getQuantity() || customerCart.getQuantity() <= 0) {
            response.setMessage("select the quatity >0 and less then or equal to " + product.getQuantity());
            response.setStatus("Failed");
            return response;
        }

        customerCart.setTotalPrice(product.getPrice() * customerCart.getQuantity());
        customerCart.setCustomerId(customer.getCustomerId());
        customerCartRepository.save(customerCart);

        response.setMessage("product " + product.getProductName() + " added to Cart");
        response.setStatus("Success");
        return response;
    }

    public List<CustomerCart> getCartProducts(String customerId) {
        if (getLoggedInCustomer() != null) {
            return customerCartRepository.findByCustomerId(customerId);
        }
        return null;
    }

    @Transactional
    public Order purchase(Purchase purchase) {
        Customer customer = getLoggedInCustomer();
        if (customer == null) {
            throw new RuntimeException("Session timeout please login");
        }
        if (!customer.getCustomerId().equals(purchase.getCustomerId())) {
            throw new RuntimeException("Invalid user");
        }
        List<CustomerCart> cartItems = customerCartRepository.findByCustomerId(customer.getCustomerId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());

        double total = 0;
        List<OrderItem> items = new ArrayList<>();
        List<String> productIds = cartItems.stream().map(cart -> cart.getProductId()).toList();
        List<Product> products = productRepository.findByProductIdIn(productIds);
        Map<String, Product> productBasedOnId = products.stream()
                .collect(Collectors.toMap(Product::getProductId, product -> product));

        for (CustomerCart cart : cartItems) {
            OrderItem item = new OrderItem();
            //item.setOrder(order);
            Product product = productBasedOnId.get(cart.getProductId());
            item.setProductName(product.getProductName());
            item.setPrice(product.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setTotalPrice(cart.getTotalPrice());
            product.setQuantity(product.getQuantity() - cart.getQuantity());
            total += cart.getTotalPrice();
            OrderItem placedItem = orderItemRepository.save(item);
            items.add(placedItem);
        }
        order.setItem(items);
        order.setTotalAmount(total);
        TranscationResponse transcationResponse = new TranscationResponse();
        transcationResponse.setFromAccount(purchase.getAccountNumber());
        transcationResponse.setToAccount(403107632);
        transcationResponse.setTranscationDate(LocalDate.now());
        transcationResponse.setAmount(total);
        Transcation transcation = bankFeignClient.transcation(transcationResponse);
        //order.setPaymentStatus(transcation.getStatus());
        order.setPaymentStatus("Completed");
        Transcation savedTranscation = transcationRepository.save(transcation);
        order.setTranscation(savedTranscation);
        if (transcation.getStatus().equals("Completed")) {
            customerCartRepository.deleteByCustomerId(customer.getCustomerId());
            productRepository.saveAll(productBasedOnId.values());
        }
        Order orderPlaced = orderRepository.save(order);
        if (transcation.getStatus().equals("Completed") || transcation.getStatus().equals("Failed")) {

            DelivaryDto delivaryDto = new DelivaryDto(orderPlaced.toString());
            try {

                String jsonString = objectMapper.writeValueAsString(delivaryDto);
                String jsonString1 = objectMapper.writeValueAsString(orderPlaced);

                System.out.println(jsonString);
                System.out.println(delivaryDto.toString());
                System.out.println(jsonString1);

                delivaryDto.setOrder(jsonString1);
                System.out.println(delivaryDto);

                kafkaObjectService.send(delivaryDto);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Internal error while processing order data", e);
            } catch (KafkaException e) {
                throw new RuntimeException("Order placed but failed to notify delivery system", e);
            } catch (Exception e) {
                throw new RuntimeException("Unexpected error occurred while processing order", e);
            }
        }
        return orderPlaced;


    }

    public Order updateDeliveryStatus(String orderId) {
		Order orderPlaced = orderRepository.findById(orderId).orElse(null);
		if (null != orderPlaced) {
			DelivaryDto delivaryDto = new DelivaryDto(orderPlaced.toString());
			try {
				String jsonString = objectMapper.writeValueAsString(delivaryDto);
				String jsonString1 = objectMapper.writeValueAsString(orderPlaced);
				System.out.println(jsonString);
				System.out.println(delivaryDto.toString());
				System.out.println(jsonString1);

				delivaryDto.setOrder(jsonString1);
				System.out.println(delivaryDto);

				kafkaObjectService.send(delivaryDto);
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Internal error while processing order data", e);
			} catch (KafkaException e) {
				throw new RuntimeException("Order placed but failed to notify delivery system", e);
			} catch (Exception e) {
				throw new RuntimeException("Unexpected error occurred while processing order", e);
			}
		//	return orderPlaced;
		}
		return orderPlaced;
	}
        public List<Order> dashboard () {
            Customer customer = getLoggedInCustomer();
            if (customer == null) {
                throw new RuntimeException("Session timeout please login");
            }
            LocalDateTime sixMothnsAgo = LocalDateTime.now().minusMonths(6);
            return orderRepository.findByCustomerCustomerIdAndOrderDateAfter(customer.getCustomerId(), sixMothnsAgo);
        }

        public List<Product> searchProducts (String productName){

            return productRepository.findByProductNameContaining(productName);
        }

	public CustomerService(CustomerRepository customerRepository, CustomerCartRepository customerCartRepository,
                OrderRepository orderRepository, TranscationRepository transcationRepository, HttpSession httpSession,
                BankFeignClient bankFeignClient) {
            super();
            this.customerRepository = customerRepository;
            this.customerCartRepository = customerCartRepository;
            this.orderRepository = orderRepository;
            this.transcationRepository = transcationRepository;
            // this.productRepository = productRepository;
            this.httpSession = httpSession;
            this.bankFeignClient = bankFeignClient;
        }

        public Response logout () {
            Response response = new Response();
            httpSession.removeAttribute("loggedInCustomer");
            response.setMessage("Logged out Successfully");
            response.setStatus("Success");
            return response;
        }
    }