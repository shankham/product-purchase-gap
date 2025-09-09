
package com.usk.ecomm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usk.ecomm.entity.Order;
import com.usk.ecomm.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private KafkaObjectService kafkaObjectService;

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateDeliveryStatusDoesNotThrowWhenOrderIsNull() {
        when(orderRepository.findById("orderNotExist")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> {
            Order result = customerService.updateDeliveryStatus("orderNotExist");
            assertNull(result);
        });
    }

    @Test
    void updateDeliveryStatusProcessesOrderWhenOrderExists() throws Exception {
        Order order = new Order();
        when(orderRepository.findById("order123")).thenReturn(Optional.of(order));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"order\":\"details\"}");
        doNothing().when(kafkaObjectService).send(any());

        Order result = customerService.updateDeliveryStatus("order123");

        assertEquals(order, result);
        verify(kafkaObjectService).send(any());
    }

    @Test
    void updateDeliveryStatusHandlesJsonProcessingException() throws Exception {
        Order order = new Order();
        when(orderRepository.findById("orderJsonError")).thenReturn(Optional.of(order));
        when(objectMapper.writeValueAsString(any())).thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("error") {
        });

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerService.updateDeliveryStatus("orderJsonError")
        );
        assertTrue(ex.getMessage().contains("Internal error while processing order data"));
    }

    @Test
    void updateDeliveryStatusHandlesKafkaException() throws Exception {
        Order order = new Order();
        when(orderRepository.findById("orderKafkaError")).thenReturn(Optional.of(order));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"order\":\"details\"}");
        doThrow(new org.apache.kafka.common.KafkaException("Kafka error")).when(kafkaObjectService).send(any());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerService.updateDeliveryStatus("orderKafkaError")
        );
        assertTrue(ex.getMessage().contains("Order placed but failed to notify delivery system"));
    }

    @Test
    void updateDeliveryStatusHandlesUnexpectedException() throws Exception {
        Order order = new Order();
        when(orderRepository.findById("orderUnexpectedError")).thenReturn(Optional.of(order));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"order\":\"details\"}");
        doThrow(new RuntimeException("Unexpected")).when(kafkaObjectService).send(any());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerService.updateDeliveryStatus("orderUnexpectedError")
        );
        assertTrue(ex.getMessage().contains("Unexpected error occurred while processing order"));
    }
}