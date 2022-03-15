package com.sd4.repository;

import com.sd4.model.Beer;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author M.Zaki Al Akkari <https://github.com/MrZakiakkari>
 */
public interface BeerRepository extends CrudRepository<Beer, Long>
{
	@Query("SELECT b FROM Beer b WHERE CONCAT(b.id, b.name) LIKE %?1%")
	public List<Beer> search(String keyword);

}
