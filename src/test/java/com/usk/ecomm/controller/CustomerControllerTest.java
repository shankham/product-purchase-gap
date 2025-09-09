package com.usk.ecomm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.usk.ecomm.entity.Order;
import com.usk.ecomm.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private Order order;

    @InjectMocks
    private CustomerController customerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateDeliveryStatusReturnsNullWhenServiceReturnsNull() throws JsonProcessingException {
        when(customerService.updateDeliveryStatus("ord3")).thenReturn(null);
        Order result = customerController.updateDeliveryStatus("ord3");
        assertEquals(null, result);
    }

    @Test
    void updateDeliveryStatusPropagatesRuntimeExceptionFromService() {
        when(customerService.updateDeliveryStatus("ord4"))
                .thenThrow(new RuntimeException("Service error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerController.updateDeliveryStatus("ord4")
        );
        assertEquals("Service error", ex.getMessage());
    }

}
