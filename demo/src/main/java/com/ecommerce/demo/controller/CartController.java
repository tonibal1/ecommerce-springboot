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

    // Shfaq përmbajtjen e shportës
    @GetMapping
    public String viewCart(Model model) {
        User user = getDefaultUser();
        model.addAttribute("items", cartService.getItems(user));
        return "cart";
    }

    // Shto produkt në shportë
    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            RedirectAttributes redirectAttributes) {
        try {
            User user = getDefaultUser();
            Product product = productService.getProductById(productId);
            if (product != null) {
                cartService.addToCart(user, product, quantity);
                redirectAttributes.addFlashAttribute("message", "Produkti u shtua në shportë me sukses!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Produkti nuk u gjet.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gabim gjatë shtimit në shportë.");
        }
        return "redirect:/cart";
    }

    // Fshij një artikull nga shporta
    @PostMapping("/remove/{itemId}")
    public String removeFromCart(@PathVariable Long itemId,
                                 RedirectAttributes redirectAttributes) {
        try {
            cartService.removeItem(itemId);
            redirectAttributes.addFlashAttribute("message", "Artikulli u fshi me sukses.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gabim gjatë fshirjes së artikullit.");
        }
        return "redirect:/cart";
    }

    // Pastro gjithë shportën
    @PostMapping("/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
        try {
            User user = getDefaultUser();
            cartService.clearCart(user);
            redirectAttributes.addFlashAttribute("message", "Shporta u pastrua me sukses.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gabim gjatë pastrimit të shportës.");
        }
        return "redirect:/cart";
    }

    // Përdorues i përkohshëm (derisa të vendoset Spring Security)
    private User getDefaultUser() {
        User defaultUser = new User();
        defaultUser.setId(1L);
        defaultUser.setUsername("default_user");
        return defaultUser;
    }
}
