
package com.ecommerce.demo.controller;

import com.ecommerce.demo.model.Product;
import com.ecommerce.demo.model.User;
import com.ecommerce.demo.service.CartService;
import com.ecommerce.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    @Autowired
    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @GetMapping
    public String viewCart(Model model) {
        User user = getDefaultUser();
        model.addAttribute("items", cartService.getItems(user));
        return "cart";
    }

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            RedirectAttributes redirectAttributes) {
        try {
            User user = getDefaultUser();
            Product product = productService.getProductById(productId);
            if (product != null) {
                cartService.addToCart(user, product, quantity);
                redirectAttributes.addFlashAttribute("message", "Product added to cart successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", "Product not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding product to cart");
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove/{itemId}")
    public String removeFromCart(@PathVariable Long itemId,
                                 RedirectAttributes redirectAttributes) {
        try {
            cartService.removeItem(itemId);
            redirectAttributes.addFlashAttribute("message", "Item removed from cart");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error removing item from cart");
        }
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
        try {
            User user = getDefaultUser();
            cartService.clearCart(user);
            redirectAttributes.addFlashAttribute("message", "Cart cleared successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error clearing cart");
        }
        return "redirect:/cart";
    }


    private User getDefaultUser() {
        User defaultUser = new User();
        defaultUser.setId(1L);
        defaultUser.setUsername("default_user");
        return defaultUser;
    }
}