package com.smartcanteen.backend.service;

import com.smartcanteen.database.dao.IPaymentDAO;
import com.smartcanteen.database.impl.PaymentDAO;
import com.smartcanteen.common.exception.PaymentException;
import com.smartcanteen.common.exception.OrderException;
import com.smartcanteen.model.Order;
import com.smartcanteen.model.Payment;
import com.smartcanteen.model.PaymentMethod;
import com.smartcanteen.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class PaymentService {

    private final IPaymentDAO paymentDAO;
    private final OrderService orderService;

    private static final Pattern UPI_PATTERN = Pattern.compile("^[a-zA-Z0-9.\\-_]{2,64}@[a-zA-Z0-9]{2,32}$");
    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("^\\d{16}$");
    private static final Pattern CVV_PATTERN = Pattern.compile("^\\d{3,4}$");

    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
        this.orderService = new OrderService();
    }

    public PaymentService(IPaymentDAO paymentDAO, OrderService orderService) {
        this.paymentDAO = (paymentDAO != null) ? paymentDAO : new PaymentDAO();
        this.orderService = (orderService != null) ? orderService : new OrderService();
    }

    /**
     * Generates a unique, traceable demo transaction reference.
     * Example: TXN-20260924-A8F19B
     */
    public String generateTransactionReference() {
        String datePart = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now());
        String randomHex = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "TXN-" + datePart + "-" + randomHex;
    }

    /**
     * Validates UPI ID format (e.g., student@upi, rahul@oksbi).
     */
    public void validateUPI(String upiId) throws PaymentException {
        if (upiId == null || upiId.trim().isEmpty()) {
            throw new PaymentException("UPI ID is required. Please enter a valid virtual payment address (e.g., student@upi).");
        }
        String trimmed = upiId.trim();
        if (!UPI_PATTERN.matcher(trimmed).matches()) {
            throw new PaymentException("Invalid UPI ID format. Expected format: username@bank (e.g. rahul@oksbi, student@upi).");
        }
    }

    /**
     * Validates card details and returns masked card representation (e.g. '**** **** **** 1234').
     * Security rule: Full 16 digits and CVV are NEVER stored.
     */
    public String validateAndMaskCard(String cardNumber, String expiryDate, String cvv, String cardHolderName) throws PaymentException {
        if (cardHolderName == null || cardHolderName.trim().isEmpty()) {
            throw new PaymentException("Cardholder name is required.");
        }

        if (cardNumber == null) {
            throw new PaymentException("Card number is required.");
        }
        String cleanNumber = cardNumber.replaceAll("[\\s-]", "");
        if (!CARD_NUMBER_PATTERN.matcher(cleanNumber).matches()) {
            throw new PaymentException("Invalid card number. Please enter a valid 16-digit debit/credit card number.");
        }

        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            throw new PaymentException("Card expiry date is required (MM/YY).");
        }
        String cleanExpiry = expiryDate.trim();
        try {
            String[] parts = cleanExpiry.split("/");
            if (parts.length != 2) {
                throw new Exception();
            }
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);
            if (month < 1 || month > 12) {
                throw new Exception();
            }
            // Normalize 2-digit year to 20YY
            if (year < 100) {
                year += 2000;
            }
            YearMonth cardExpiry = YearMonth.of(year, month);
            YearMonth currentMonth = YearMonth.now();
            if (cardExpiry.isBefore(currentMonth)) {
                throw new PaymentException("The entered card has expired (" + cleanExpiry + "). Please use a valid active card.");
            }
        } catch (PaymentException pe) {
            throw pe;
        } catch (Exception e) {
            throw new PaymentException("Invalid expiry format. Please use MM/YY (e.g., 08/28).");
        }

        if (cvv == null || !CVV_PATTERN.matcher(cvv.trim()).matches()) {
            throw new PaymentException("Invalid CVV. Please enter a 3 or 4 digit security code.");
        }

        // Return strictly masked details (last 4 digits only)
        String last4 = cleanNumber.substring(12);
        return "Card: **** **** **** " + last4 + " (" + cardHolderName.trim() + ")";
    }

    /**
     * Validates wallet balance for student campus demo wallet.
     */
    public void validateWallet(BigDecimal requiredAmount, BigDecimal currentBalance) throws PaymentException {
        if (currentBalance == null || requiredAmount == null) {
            throw new PaymentException("Invalid wallet balance or payment amount.");
        }
        if (currentBalance.compareTo(requiredAmount) < 0) {
            throw new PaymentException(String.format("Insufficient wallet balance. Available: ₹%.2f, Required: ₹%.2f.",
                    currentBalance, requiredAmount));
        }
    }

    /**
     * Executes the PAYMENT-FIRST business flow:
     * 1. Validate payment details based on method
     * 2. If simulation flag or balance check fails -> record FAILED payment, throw exception (NO order, NO token, CART INTACT).
     * 3. If payment succeeds -> record SUCCESS payment in DB with unique TXN reference.
     * 4. Call OrderService to transactionally create Order, Order Items, Token, link Payment, and enqueue to Food Queue.
     * 5. Cart is cleared only inside OrderService after transactional DB commit.
     */
    public Order processPaymentAndPlaceOrder(Payment payment, CartService cartService, boolean simulateFailure) throws PaymentException {
        if (payment == null) {
            throw new PaymentException("Payment details cannot be null.");
        }
        if (cartService == null || cartService.getItems().isEmpty()) {
            throw new PaymentException("Cannot process payment for an empty cart.");
        }

        BigDecimal totalAmount = cartService.getTotal();
        payment.setAmount(totalAmount);

        if (payment.getTransactionReference() == null || payment.getTransactionReference().isEmpty()) {
            payment.setTransactionReference(generateTransactionReference());
        }
        payment.setCreatedAt(LocalDateTime.now());

        // Step 1: Check for simulated failure test
        if (simulateFailure) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            try {
                paymentDAO.insertPayment(payment);
            } catch (Exception e) {
                System.err.println("Notice: Failed payment record logging: " + e.getMessage());
            }
            throw new PaymentException("Payment Declined: Bank transaction simulation failed (Payment Status: FAILED). " +
                    "No food token was generated, and your cart has been preserved.");
        }

        // Step 2: Payment SUCCESS recording
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        int paymentId;
        try {
            paymentId = paymentDAO.insertPayment(payment);
            payment.setPaymentId(paymentId);
        } catch (Exception e) {
            throw new PaymentException("Failed to securely record payment transaction: " + e.getMessage(), e);
        }

        // Step 3: SUCCESSFUL PaymentService -> OrderService -> Token generation & Food Queue
        try {
            Order placedOrder = orderService.placeOrder(payment.getUserId(), cartService, payment);
            return placedOrder;
        } catch (OrderException oe) {
            // Update payment record to FAILED if order creation failed
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentDAO.updateStatus(paymentId, PaymentStatus.FAILED);
            throw new PaymentException("Payment succeeded but order creation encountered an error: " + oe.getMessage(), oe);
        }
    }

    /**
     * Records a cancelled payment transaction when student returns to cart or cancels checkout.
     */
    public void recordCancelledPayment(Payment payment) {
        if (payment == null) {
            return;
        }
        try {
            payment.setPaymentStatus(PaymentStatus.CANCELLED);
            if (payment.getTransactionReference() == null) {
                payment.setTransactionReference(generateTransactionReference());
            }
            payment.setCreatedAt(LocalDateTime.now());
            paymentDAO.insertPayment(payment);
        } catch (Exception e) {
            System.err.println("Notice: Cancelled payment record skipped: " + e.getMessage());
        }
    }

    public Payment getPaymentById(int paymentId) {
        return paymentDAO.getById(paymentId);
    }

    public Payment getPaymentByTransactionReference(String txnRef) {
        return paymentDAO.getByTransactionReference(txnRef);
    }

    public List<Payment> getPaymentsByUser(int userId) {
        return paymentDAO.getPaymentsByUser(userId);
    }

    public List<Payment> getAllPayments() {
        return paymentDAO.getAllPayments();
    }
}
