package com.sd4.service;

import com.sd4.model.Category;
import com.sd4.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author M.Zaki Al Akkari <https://github.com/MrZakiakkari>
 */
@Service
public class CategoryService
{
	@Autowired
	private CategoryRepository categoryRepository;

	public void deleteById(long id)
	{
		categoryRepository.deleteById(id);
	}
	public List<Category> findAll()
	{
		return (List<Category>) categoryRepository.findAll();
	}
	public Optional<Category> findById(long id)
	{
		return categoryRepository.findById(id);
	}
	public Category save(Category category)
	{
		return categoryRepository.save(category);
	}
}
