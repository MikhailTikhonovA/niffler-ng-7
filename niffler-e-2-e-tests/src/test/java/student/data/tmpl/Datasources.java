package student.data.tmpl;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class Datasources {

    private Datasources(){
    }

    private static final Map<String, DataSource> DATASOURCE = new ConcurrentHashMap<>();

    public static DataSource dataSource(String jdbcUrl) {
        return DATASOURCE.computeIfAbsent(
                jdbcUrl,
                key -> {
                    AtomikosDataSourceBean dataSourceBean = new AtomikosDataSourceBean();
                    final String uniqId = StringUtils.substringAfter(jdbcUrl, "5432/");
                    dataSourceBean.setUniqueResourceName(uniqId);
                    dataSourceBean.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
                    Properties properties = new Properties();
                    properties.put("URL", jdbcUrl);
                    properties.put("user", "postgres");
                    properties.put("password", "secret");
                    dataSourceBean.setXaProperties(properties);
                    dataSourceBean.setPoolSize(10);
                    return dataSourceBean;
                }
        );
    }
}
