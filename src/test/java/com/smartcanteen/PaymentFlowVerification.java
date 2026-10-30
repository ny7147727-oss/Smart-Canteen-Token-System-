package com.smartcanteen;

import com.smartcanteen.database.dao.IOrderDAO;
import com.smartcanteen.database.dao.IOrderItemDAO;
import com.smartcanteen.database.dao.IPaymentDAO;
import com.smartcanteen.database.dao.ITokenDAO;
import com.smartcanteen.common.exception.OrderException;
import com.smartcanteen.common.exception.PaymentException;
import com.smartcanteen.model.CartItem;
import com.smartcanteen.model.MenuItem;
import com.smartcanteen.model.Order;
import com.smartcanteen.model.OrderItem;
import com.smartcanteen.model.OrderStatus;
import com.smartcanteen.model.Payment;
import com.smartcanteen.model.PaymentMethod;
import com.smartcanteen.model.PaymentStatus;
import com.smartcanteen.model.Token;
import com.smartcanteen.backend.service.CartService;
import com.smartcanteen.backend.service.OrderService;
import com.smartcanteen.backend.service.PaymentService;
import com.smartcanteen.backend.service.TokenService;
import com.smartcanteen.common.util.TokenGenerator;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PaymentFlowVerification {

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("     SMARTSERVE TRANSACTION & PAYMENT-FIRST TEST SUITE   ");
        System.out.println("=========================================================");

        int passed = 0;
        int failed = 0;

        PaymentService paymentService = new PaymentService();
        CartService cartService = CartService.getInstance();
        cartService.clear();
        TokenGenerator.reset();

        // ---------------------------------------------------------------------
        // TEST 1: UPI ID Validation
        // ---------------------------------------------------------------------
        try {
            paymentService.validateUPI("student@upi");
            paymentService.validateUPI("rahul@oksbi");
            System.out.println("[PASS] Test 1A: Valid UPI formats accepted.");
            passed++;
        } catch (Exception e) {
            System.err.println("[FAIL] Test 1A: " + e.getMessage());
            failed++;
        }

        try {
            paymentService.validateUPI("invalid-upi-no-at-sign");
            System.err.println("[FAIL] Test 1B: Invalid UPI format accepted.");
            failed++;
        } catch (PaymentException pe) {
            System.out.println("[PASS] Test 1B: Invalid UPI rejected: " + pe.getMessage());
            passed++;
        }

        // ---------------------------------------------------------------------
        // TEST 2: Card Validation & Masking (Security Requirement)
        // ---------------------------------------------------------------------
        try {
            String masked = paymentService.validateAndMaskCard("4532876510923456", "12/28", "888", "Rahul Kumar");
            if (masked.contains("**** **** **** 3456") && !masked.contains("45328765")) {
                System.out.println("[PASS] Test 2A: Card successfully masked as: " + masked);
                passed++;
            } else {
                System.err.println("[FAIL] Test 2A: Card masking incorrect: " + masked);
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Test 2A: " + e.getMessage());
            failed++;
        }

        // ---------------------------------------------------------------------
        // TEST 3: Wallet Balance Validation
        // ---------------------------------------------------------------------
        try {
            paymentService.validateWallet(new BigDecimal("120.00"), new BigDecimal("500.00"));
            System.out.println("[PASS] Test 3A: Sufficient wallet balance accepted.");
            passed++;
        } catch (Exception e) {
            System.err.println("[FAIL] Test 3A: " + e.getMessage());
            failed++;
        }

        try {
            paymentService.validateWallet(new BigDecimal("600.00"), new BigDecimal("500.00"));
            System.err.println("[FAIL] Test 3B: Overdraw wallet balance accepted.");
            failed++;
        } catch (PaymentException pe) {
            System.out.println("[PASS] Test 3B: Insufficient wallet balance rejected.");
            passed++;
        }

        // ---------------------------------------------------------------------
        // TEST 4: Payment FAILED Flow (Verify NO Token, Cart REMAINS Intact)
        // ---------------------------------------------------------------------
        MenuItem item1 = new MenuItem();
        item1.setItemId(1);
        item1.setItemName("Chole Bhature");
        item1.setPrice(new BigDecimal("80.00"));
        cartService.addToCart(item1, 2); // Total = 160.00

        int tokenBeforeFailed = TokenGenerator.peek();

        Payment failedPayment = new Payment();
        failedPayment.setUserId(2);
        failedPayment.setPaymentMethod(PaymentMethod.UPI);
        failedPayment.setMaskedDetails("UPI: student@upi");

        try {
            paymentService.processPaymentAndPlaceOrder(failedPayment, cartService, true); // simulate failure = true
            System.err.println("[FAIL] Test 4: Simulated failure did not throw PaymentException.");
            failed++;
        } catch (PaymentException pe) {
            int tokenAfterFailed = TokenGenerator.peek();
            if (tokenAfterFailed == tokenBeforeFailed && cartService.getItems().size() == 1) {
                System.out.println("[PASS] Test 4: Failed payment prevented token generation (Token remained: " +
                        tokenAfterFailed + ") and Cart preserved (Total: ₹" + cartService.getTotal() + ").");
                passed++;
            } else {
                System.err.println("[FAIL] Test 4: Token was generated or cart was modified on payment failure!");
                failed++;
            }
        }

        // ---------------------------------------------------------------------
        // TEST 5: Payment CANCELLED Flow (Verify Cart REMAINS Intact)
        // ---------------------------------------------------------------------
        int tokenBeforeCancelled = TokenGenerator.peek();
        Payment cancelledPayment = new Payment();
        cancelledPayment.setUserId(2);
        cancelledPayment.setAmount(cartService.getTotal());
        cancelledPayment.setPaymentMethod(PaymentMethod.WALLET);
        paymentService.recordCancelledPayment(cancelledPayment);

        int tokenAfterCancelled = TokenGenerator.peek();
        if (tokenAfterCancelled == tokenBeforeCancelled && cartService.getItems().size() == 1) {
            System.out.println("[PASS] Test 5: Payment Cancelled correctly. Cart remains intact and no token created.");
            passed++;
        } else {
            System.err.println("[FAIL] Test 5: Cart or Token modified on payment cancellation!");
            failed++;
        }

        // ---------------------------------------------------------------------
        // TEST 6: Transaction Rollback & Database Failure Simulation
        // ---------------------------------------------------------------------
        // Create an OrderService with a failing TokenDAO to simulate critical mid-transaction DB failure
        ITokenDAO failingTokenDAO = new ITokenDAO() {
            @Override
            public boolean insertToken(Token token) { return false; }
            @Override
            public boolean insertToken(Connection connection, Token token) {
                throw new RuntimeException("Simulated Database Disk Error during Token insert");
            }
            @Override
            public Token getByTokenNumber(int tokenNumber) { return null; }
            @Override
            public Token getByTokenNumber(Token token) { return null; }
            @Override
            public List<Token> getWaitingTokens() { return new ArrayList<>(); }
            @Override
            public boolean updateStatus(int tokenNumber, com.smartcanteen.model.TokenStatus status) { return false; }
            @Override
            public boolean updateStatus(Token token) { return false; }
            @Override
            public int getMaxTokenNumber() { return 0; }
        };

        IOrderDAO mockOrderDAO = new IOrderDAO() {
            @Override
            public int insertOrder(Order order) { return 1; }
            @Override
            public int insertOrder(Connection connection, Order order) { return 999; }
            @Override
            public boolean updateStatus(int orderId, OrderStatus status) { return true; }
            @Override
            public boolean updateStatus(Order order) { return true; }
            @Override
            public Order getById(int orderId) { return null; }
            @Override
            public List<Order> getOrdersByUser(int userId) { return new ArrayList<>(); }
            @Override
            public List<Order> getActiveOrders() { return new ArrayList<>(); }
            @Override
            public List<Order> getAllOrders() { return new ArrayList<>(); }
        };

        IOrderItemDAO mockOrderItemDAO = new IOrderItemDAO() {
            @Override
            public boolean insertOrderItem(OrderItem orderItem) { return true; }
            @Override
            public boolean insertOrderItem(Connection connection, OrderItem orderItem) { return true; }
            @Override
            public List<OrderItem> getItemsByOrderId(int orderId) { return new ArrayList<>(); }
        };

        IPaymentDAO mockPaymentDAO = new IPaymentDAO() {
            @Override
            public int insertPayment(Payment payment) { return 1; }
            @Override
            public int insertPayment(Connection connection, Payment payment) { return 1; }
            @Override
            public boolean updateStatus(int paymentId, PaymentStatus status) { return true; }
            @Override
            public boolean updatePaymentOrderId(Connection connection, int paymentId, int orderId) { return true; }
            @Override
            public Payment getById(int paymentId) { return null; }
            @Override
            public Payment getByTransactionReference(String transactionReference) { return null; }
            @Override
            public List<Payment> getPaymentsByUser(int userId) { return new ArrayList<>(); }
            @Override
            public List<Payment> getPaymentsByOrder(int orderId) { return new ArrayList<>(); }
            @Override
            public List<Payment> getAllPayments() { return new ArrayList<>(); }
        };

        OrderService rollbackTestOrderService = new OrderService(mockOrderDAO, mockOrderItemDAO, failingTokenDAO, mockPaymentDAO);

        try {
            rollbackTestOrderService.placeOrder(2, cartService, null);
            System.err.println("[FAIL] Test 6: Transaction should have rolled back on error.");
            failed++;
        } catch (OrderException oe) {
            // Check that cart was NOT cleared during failed transaction
            if (cartService.getItems().size() == 1) {
                System.out.println("[PASS] Test 6: Transaction rollback verified. Cart remained intact when database operation failed.");
                passed++;
            } else {
                System.err.println("[FAIL] Test 6: Cart was cleared despite transaction rollback!");
                failed++;
            }
        }

        // ---------------------------------------------------------------------
        // TEST 7: Token Uniqueness Verification (No Duplicates)
        // ---------------------------------------------------------------------
        TokenGenerator.reset();
        Set<Integer> generatedTokens = new HashSet<>();
        boolean duplicatesFound = false;

        for (int i = 0; i < 1000; i++) {
            int token = TokenGenerator.next();
            if (!generatedTokens.add(token)) {
                duplicatesFound = true;
                break;
            }
        }

        if (!duplicatesFound && generatedTokens.size() == 1000) {
            System.out.println("[PASS] Test 7: 1000 sequential tokens generated with 100% uniqueness (No duplicates).");
            passed++;
        } else {
            System.err.println("[FAIL] Test 7: Duplicate tokens detected in sequence!");
            failed++;
        }

        cartService.clear();

        System.out.println("=========================================================");
        System.out.println(String.format("FINAL TEST SUITE RESULTS: %d Passed, %d Failed", passed, failed));
        System.out.println("=========================================================");
        if (failed > 0) {
            System.exit(1);
        }
    }
}
