package com.mini.shopper.controller;

import java.rmi.ServerException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mini.shopper.dto.CartItemReq;
import com.mini.shopper.dto.CartItemsResponse;
import com.mini.shopper.dto.CartReqDto;
import com.mini.shopper.dto.CartResDto;
import com.mini.shopper.exception.CartNotFoundException;
import com.mini.shopper.model.Cart;
import com.mini.shopper.service.CartService;

@RestController
public class CartController {

	@Autowired
	CartService cartService;
	

	@PostMapping(path = "/addToCart")
	public ResponseEntity<Cart> addProductsToCart(@RequestBody CartReqDto addtocart) throws ServerException, CartNotFoundException {
		Cart cartItem = cartService.addProductsToCart(addtocart);
		{
			if (cartItem == null) {
				throw new CartNotFoundException("The product you are trying to add in the cart is invalid");
			} else {
				return new ResponseEntity<>(cartItem, HttpStatus.CREATED);
			}
		}
	}

	@PostMapping(path = "/isItemInCart")
	public ResponseEntity<?> isItemInCart(@RequestBody CartItemReq isitemincart) throws ServerException {
		Cart cartItem = cartService.isItemInCart(isitemincart);
		{
			if (cartItem == null) {
				return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<>(cartItem, HttpStatus.OK);
			}
		}
	}

	@PutMapping(path = "/updateCart")
	public ResponseEntity<Cart> updateCart(@RequestBody CartReqDto updatedcart) throws CartNotFoundException {

		Cart updatedCart = cartService.updateCart(updatedcart);
		{
			if (updatedCart == null) {
				throw new CartNotFoundException("Cart not found or No Items in Cart");
			} else {
				return new ResponseEntity<>(updatedCart, HttpStatus.OK);
			}
		}

	}

	@GetMapping(path = "/getCart/{userId}")
	public ResponseEntity<List<CartResDto>> getCart(@PathVariable String userId) throws CartNotFoundException {
		List<CartResDto> cart = cartService.getCart(userId);
		if (cart.isEmpty()) {
			throw new CartNotFoundException("Cart with userId: "+userId+ "not found.");
		} else {
			return new ResponseEntity<>(cart, HttpStatus.OK);
		}
	}

	@GetMapping(path = "/getCartItems/{userId}")
	public ResponseEntity<List<CartItemsResponse>> getCartItems(@PathVariable String userId) throws CartNotFoundException {
		List<CartItemsResponse> cartItems = cartService.getCartItems(userId);
		if (cartItems.isEmpty()) {
			throw new CartNotFoundException("No Items in the cart for the userId: "+userId);
		} else {
			return new ResponseEntity<>(cartItems, HttpStatus.OK);
		}

	}

	@DeleteMapping("/removeFromCart")
	public ResponseEntity<String> removeProductFromCart(@RequestBody CartItemReq removefromcartreq) {

		try {
			cartService.removeProductFromCart(removefromcartreq.getUserId(), removefromcartreq.getProductId());
			return ResponseEntity.ok("Product removed from cart successfully");
		} catch (NotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error removing product from cart: " + e.getMessage());
		}

	}

}
