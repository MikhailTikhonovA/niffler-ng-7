package student.data.mapper;

import org.springframework.jdbc.core.RowMapper;
import student.data.entity.spend.CategoryEntity;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CategoryEntityRowMapper implements RowMapper<CategoryEntity> {

  public static final CategoryEntityRowMapper instance = new CategoryEntityRowMapper();

  private CategoryEntityRowMapper() {
  }

  @Override
  public CategoryEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    CategoryEntity ce = new CategoryEntity();
    ce.setId(rs.getObject("id", UUID.class));
    ce.setUsername(rs.getString("username"));
    ce.setName(rs.getString("name"));
    ce.setArchived(rs.getBoolean("archived"));
    return ce;
  }
}
