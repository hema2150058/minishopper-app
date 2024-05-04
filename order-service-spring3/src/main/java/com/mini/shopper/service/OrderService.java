package com.mini.shopper.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mini.shopper.dto.OrderDetailsRes;
import com.mini.shopper.dto.PlaceOrderReq;
import com.mini.shopper.dto.PlaceOrderRes;
import com.mini.shopper.model.Order;
import com.mini.shopper.repo.BillingRepo;
import com.mini.shopper.repo.CartRepo;
import com.mini.shopper.repo.OrderRepo;
import com.mini.shopper.repo.OrderedProductRepo;

@Service
public class OrderService {

	@Autowired
	CartRepo cartRepo;
	
	@Autowired
	OrderedProductRepo orderProductRepo;
	
	@Autowired
	BillingRepo billingRepo;
	
	@Autowired
	OrderRepo orderRepo;
	
	@Transactional
	public PlaceOrderRes placeOrder(PlaceOrderReq placeorderreq) {
		
		return null; //user
	}
	
	public OrderDetailsRes getOrderDetails(Long orderNumber) {
		return null; //user
	}
	
	public List<OrderDetailsRes> getPurchaseHistory(int userId) {
		return null; //user
	}
	
	public List<OrderDetailsRes> getAllOrders(){
		return null; //admin gets all orders from all users
	}
	
	public List<OrderDetailsRes> getAllPendingOrders(String orderStatus){
		List<Order> order = orderRepo.findByOrderStatus(orderStatus);
		//by findbyOrderStatus; admin
		return null;
	}
	
	public List<PlaceOrderRes> changeOrderStatus(){
		//by findbyOrderId; admin
		return null;
	}

	
}
