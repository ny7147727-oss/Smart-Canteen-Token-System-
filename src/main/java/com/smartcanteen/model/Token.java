package com.smartcanteen.model;

import java.time.LocalDateTime;

public class Token {
    private int tokenId;
    private int tokenNumber;
    private int orderId;
    private TokenStatus status;
    private LocalDateTime generatedAt;

    public Token() {
    }

    public Token(int tokenId, int tokenNumber, int orderId, TokenStatus status, LocalDateTime generatedAt) {
        this.tokenId = tokenId;
        this.tokenNumber = tokenNumber;
        this.orderId = orderId;
        this.status = status;
        this.generatedAt = generatedAt;
    }

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(int tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public TokenStatus getStatus() {
        return status;
    }

    public void setStatus(TokenStatus status) {
        this.status = status;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenId=" + tokenId +
                ", tokenNumber=" + tokenNumber +
                ", orderId=" + orderId +
                ", status=" + status +
                ", generatedAt=" + generatedAt +
                '}';
    }
}
