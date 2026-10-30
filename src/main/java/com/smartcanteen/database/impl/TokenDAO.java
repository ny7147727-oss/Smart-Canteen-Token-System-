package com.smartcanteen.database.impl;

import com.smartcanteen.database.dao.ITokenDAO;
import com.smartcanteen.common.exception.DatabaseException;
import com.smartcanteen.model.Token;
import com.smartcanteen.model.TokenStatus;
import com.smartcanteen.database.connection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TokenDAO implements ITokenDAO {

    @Override
    public boolean insertToken(Token token) {
        try (Connection conn = DBConnection.getConnection()) {
            return insertToken(conn, token);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert token for token number: " + (token != null ? token.getTokenNumber() : 0), e);
        }
    }

    @Override
    public boolean insertToken(Connection connection, Token token) {
        String sql = "INSERT INTO tokens (token_number, order_id, status, generated_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, token.getTokenNumber());
            stmt.setInt(2, token.getOrderId());
            stmt.setString(3, (token.getStatus() != null ? token.getStatus() : TokenStatus.WAITING).name());
            stmt.setTimestamp(4, token.getGeneratedAt() != null ? Timestamp.valueOf(token.getGeneratedAt()) : new Timestamp(System.currentTimeMillis()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        token.setTokenId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert token in transaction for token number: " + (token != null ? token.getTokenNumber() : 0), e);
        }
    }

    @Override
    public Token getByTokenNumber(int tokenNumber) {
        String sql = "SELECT token_id, token_number, order_id, status, generated_at FROM tokens WHERE token_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tokenNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve token with token number: " + tokenNumber, e);
        }
        return null;
    }

    @Override
    public Token getByTokenNumber(Token token) {
        if (token == null) {
            return null;
        }
        return getByTokenNumber(token.getTokenNumber());
    }

    @Override
    public List<Token> getWaitingTokens() {
        String sql = "SELECT token_id, token_number, order_id, status, generated_at " +
                     "FROM tokens WHERE status = 'WAITING' ORDER BY generated_at ASC";
        List<Token> tokens = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tokens.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve waiting tokens", e);
        }
        return tokens;
    }

    @Override
    public boolean updateStatus(int tokenNumber, TokenStatus status) {
        String sql = "UPDATE tokens SET status = ? WHERE token_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, tokenNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update status for token number: " + tokenNumber, e);
        }
    }

    @Override
    public boolean updateStatus(Token token) {
        if (token == null) {
            return false;
        }
        return updateStatus(token.getTokenNumber(), token.getStatus());
    }

    @Override
    public int getMaxTokenNumber() {
        String sql = "SELECT COALESCE(MAX(token_number), 0) FROM tokens";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve max token number", e);
        }
        return 0;
    }

    private Token mapRow(ResultSet rs) throws SQLException {
        int tokenId = rs.getInt("token_id");
        int tokenNumber = rs.getInt("token_number");
        int orderId = rs.getInt("order_id");
        String statusStr = rs.getString("status");
        TokenStatus status = (statusStr != null) ? TokenStatus.valueOf(statusStr) : TokenStatus.WAITING;
        Timestamp ts = rs.getTimestamp("generated_at");
        LocalDateTime generatedAt = (ts != null) ? ts.toLocalDateTime() : null;

        return new Token(tokenId, tokenNumber, orderId, status, generatedAt);
    }
}
