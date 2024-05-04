package com.mini.shopper.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mini.shopper.model.Cart;
import com.mini.shopper.model.Product;

public interface CartRepo extends JpaRepository<Cart, Integer> {

	List<Cart> findByCartId(int cartId);
	
	Cart findByProductIdAndUserId(Product product,String userId);
	
	void deleteByProductIdAndUserId(Product product,String userId);
	
	void deleteAllByUserId(String userId);
	
	boolean deleteById(int id);
	
	List<Cart> findByUserId(String userId);
}
