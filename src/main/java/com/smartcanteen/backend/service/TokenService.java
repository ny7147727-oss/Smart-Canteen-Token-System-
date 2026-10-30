package com.smartcanteen.backend.service;

import com.smartcanteen.database.dao.ITokenDAO;
import com.smartcanteen.database.impl.TokenDAO;
import com.smartcanteen.model.Token;
import com.smartcanteen.model.TokenStatus;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public class TokenService {

    private final ITokenDAO tokenDAO;

    public TokenService() {
        this.tokenDAO = new TokenDAO();
    }

    public TokenService(ITokenDAO tokenDAO) {
        this.tokenDAO = (tokenDAO != null) ? tokenDAO : new TokenDAO();
    }

    public Token getToken(int tokenNumber) {
        return tokenDAO.getByTokenNumber(tokenNumber);
    }

    public Token getToken(Token token) {
        if (token == null) {
            return null;
        }
        return tokenDAO.getByTokenNumber(token.getTokenNumber());
    }

    public List<Token> getWaitingTokens() {
        return tokenDAO.getWaitingTokens();
    }

    public boolean updateTokenStatus(int tokenNumber, TokenStatus status) {
        return tokenDAO.updateStatus(tokenNumber, status);
    }

    public int getMaxTokenNumber() {
        return tokenDAO.getMaxTokenNumber();
    }

    public boolean createToken(Connection connection, int tokenNumber, int orderId) {
        Token token = new Token();
        token.setTokenNumber(tokenNumber);
        token.setOrderId(orderId);
        token.setStatus(TokenStatus.WAITING);
        token.setGeneratedAt(LocalDateTime.now());
        return tokenDAO.insertToken(connection, token);
    }
}
