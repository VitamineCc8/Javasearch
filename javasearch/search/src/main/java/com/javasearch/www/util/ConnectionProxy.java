package com.javasearch.www.util;

import java.sql.*;

public class ConnectionProxy {
    private Connection connection;
    private Statement ps;
    private ResultSet rs;
    private String sql;

    public ConnectionProxy(Connection connection) {
        this.connection = connection;
        this.sql = "";
    }

    public void setAutoCommit(boolean is) throws SQLException {
        connection.setAutoCommit(is);
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void close() {
        if (!CommonUtil.isNull(rs)) {
            try {
                rs.close();
            } catch (SQLException throwables) {
                Logger.error("close error");
            }
        }
        if (!CommonUtil.isNull(ps)) {
            try {
                ps.close();
            } catch (SQLException throwables) {
                Logger.error("close error");
            }
        }
        ConnectionUtil.flyBackConnection(connection);
    }

    public void getPreparedStatement() {
        try {
            if (CommonUtil.isNull(this.ps)) {
                ps = connection.createStatement();
            }
        } catch (SQLException e) {
            Logger.error(e.toString());
        }
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Integer executeUpdate() throws SQLException {
        if (!CommonUtil.isNull(this.ps) && !CommonUtil.isNull(sql)) {
            return ps.executeUpdate(sql);
        } else {
            Logger.error("sql null");
            return null;
        }
    }

    public ResultSet executeQuery() throws SQLException {
        if (!CommonUtil.isNull(this.ps) && !CommonUtil.isNull(sql)) {
            return ps.executeQuery(sql);
        } else {
            return null;
        }
    }
}
