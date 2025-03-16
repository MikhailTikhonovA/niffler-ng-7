package student.data.tmpl;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcConnectionHolder implements AutoCloseable {

    private final DataSource datasource;
    private final Map<Long, Connection> threadConnections = new ConcurrentHashMap<>();

    public JdbcConnectionHolder(DataSource datasource) {
        this.datasource = datasource;
    }

    public Connection connection(){
        return threadConnections.computeIfAbsent(
              Thread.currentThread().threadId(),
                key->{
                  try {
                      return datasource.getConnection();
                  }catch (SQLException exception){
                      throw new RuntimeException();
                  }
                }
        );
    }

    @Override
    public void close() throws Exception {
        Optional.ofNullable(threadConnections.remove(Thread.currentThread().threadId()))
                .ifPresent(connection -> {
                            try {
                                if (!connection.isClosed()) {
                                    connection.close();
                                }
                            } catch (SQLException ignored) {

                            }
                        }
                );
    }

    public void closeAllConnections() {
        threadConnections.values().forEach(
                connection -> {
                    try {
                        if (connection != null && !connection.isClosed()) {
                            connection.close();
                        }
                    } catch (SQLException e) {

                    }
                }
        );
    }
}
