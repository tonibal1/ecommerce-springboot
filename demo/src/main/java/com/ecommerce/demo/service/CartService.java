
package com.ecommerce.demo.service;

import com.ecommerce.demo.model.Cart;
import com.ecommerce.demo.model.CartItem;
import com.ecommerce.demo.model.Product;
import com.ecommerce.demo.model.User;
import com.ecommerce.demo.repository.CartRepository;
import com.ecommerce.demo.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private CartItemRepository cartItemRepo;

    public Cart getCartForUser(User user) {
        return cartRepo.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepo.save(cart);
        });
    }

    public void addToCart(User user, Product product, int quantity) {
        Cart cart = getCartForUser(user);
        Optional<CartItem> existing = cartItemRepo.findByCartAndProduct(cart, product);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepo.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            cartItemRepo.save(item);
        }
    }

    public List<CartItem> getItems(User user) {
        return getCartForUser(user).getItems();
    }

    public void removeItem(Long itemId) {
        cartItemRepo.deleteById(itemId);
    }

    public void clearCart(User user) {
        Cart cart = getCartForUser(user);
        cartItemRepo.deleteAll(cart.getItems());
    }
}