package com.smartcanteen.database.dao;

import com.smartcanteen.model.Token;
import com.smartcanteen.model.TokenStatus;

import java.sql.Connection;
import java.util.List;

public interface ITokenDAO {
    boolean insertToken(Token token);
    boolean insertToken(Connection connection, Token token);
    Token getByTokenNumber(int tokenNumber);
    Token getByTokenNumber(Token token);
    List<Token> getWaitingTokens();
    boolean updateStatus(int tokenNumber, TokenStatus status);
    boolean updateStatus(Token token);
    int getMaxTokenNumber();
}
