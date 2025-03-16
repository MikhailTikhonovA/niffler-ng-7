package student.data.tmpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

public class JdbcTransactionTemplate {

    private final JdbcConnectionHolder holder;
    private final AtomicBoolean closeAfterAction = new AtomicBoolean(true);

    public JdbcTransactionTemplate(String jdbcUrl) {
        this.holder = Connections.holder(jdbcUrl);
    }

    public JdbcTransactionTemplate holdConnectionAfterAction(){
        this.closeAfterAction.set(false);
        return this;
    }

    public <T> T execute(Supplier<T> action, Integer isolationLevel) throws Exception {
        Connection connection = null;
        int level = (isolationLevel != null) ? isolationLevel : Connection.TRANSACTION_SERIALIZABLE;
        try {
            connection = holder.connection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(level);
            T result = action.get();
            connection.commit();
            connection.setAutoCommit(true);
            return result;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    throw new RuntimeException(e);
                }
            }
            throw new RuntimeException(e);
        }finally {
            if(closeAfterAction.get()){
                holder.close();
            }
        }
    }

    public <T> T execute(Supplier<T> action) throws Exception {
        return execute(action, TRANSACTION_READ_COMMITTED);
    }
}
