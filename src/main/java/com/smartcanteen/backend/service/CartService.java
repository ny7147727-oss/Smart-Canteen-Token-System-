package com.smartcanteen.backend.service;

import com.smartcanteen.model.CartItem;
import com.smartcanteen.model.MenuItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartService {

    private static final CartService INSTANCE = new CartService();

    private final Map<Integer, CartItem> cart = new HashMap<>();

    public static CartService getInstance() {
        return INSTANCE;
    }

    public void addToCart(MenuItem item, int quantity) {
        if (item == null || quantity <= 0) {
            return;
        }

        int itemId = item.getItemId();
        if (cart.containsKey(itemId)) {
            CartItem existing = cart.get(itemId);
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            cart.put(itemId, new CartItem(item, quantity));
        }
    }

    public void addToCart(CartItem cartItem) {
        if (cartItem == null || cartItem.getItem() == null || cartItem.getQuantity() <= 0) {
            return;
        }
        addToCart(cartItem.getItem(), cartItem.getQuantity());
    }

    public void removeFromCart(int itemId) {
        cart.remove(itemId);
    }

    public void updateQuantity(int itemId, int quantity) {
        if (quantity > 0) {
            if (cart.containsKey(itemId)) {
                cart.get(itemId).setQuantity(quantity);
            }
        } else {
            cart.remove(itemId);
        }
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(cart.values());
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cart.values()) {
            if (cartItem != null && cartItem.getSubtotal() != null) {
                total = total.add(cartItem.getSubtotal());
            }
        }
        return total;
    }

    public void clear() {
        cart.clear();
    }
}
