package com.sd4.controller;

import com.sd4.model.Category;
import com.sd4.service.CategoryService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "categories")
public class CategoryController
{
	@Autowired
	private CategoryService categoryService;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Category> createCategory(@RequestBody Category newCategory)
	{
		System.out.println(newCategory);
		System.out.println(newCategory.getId());

		System.out.println(newCategory.getLast_mod());
		Category category = categoryService.save(newCategory);
		return ResponseEntity.ok(category);
	}

	@DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deleteCategoryById(@PathVariable("id") long id)
	{
		Optional<Category> optional = categoryService.findById(id);
		if (optional.isEmpty())
		{
			return ResponseEntity.notFound().build();
		}
		//categoryService.deleteById(id);
		return ResponseEntity.ok(optional.get());
	}
	@GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public ResponseEntity<Category> getCategoryById(@PathVariable("id") long id)
	{
		Optional<Category> optional = categoryService.findById(id);
		if (optional.isEmpty())
		{
			return ResponseEntity.notFound().build();
		}
		Link selfLink = linkTo(getClass()).slash(id).withSelfRel();

		optional.get().add(selfLink);
		return ResponseEntity.ok(optional.get());
	}

	@GetMapping(value = "", produces = MediaTypes.HAL_JSON_VALUE)
	public CollectionModel<Category> getCategories()
	{
		System.out.println("getCategorys");

		List<Category> categorys = categoryService.findAll();

		for (Category category : categorys)
		{
			long id = category.getId();
			Link selfLink = linkTo(getClass()).slash(id).withSelfRel();
			category.add(selfLink);
		}

		Link link = linkTo(getClass()).withSelfRel();
		CollectionModel<Category> collectionModel = CollectionModel.of(categorys, link);
		return collectionModel;
	}

	@PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity updateCategory(@PathVariable("id") long id, @RequestBody Category category)
	{
		if (id != category.getId())
		{
			return ResponseEntity.badRequest().build();
		}
		categoryService.save(category);
		return ResponseEntity.ok(category);
	}
}
