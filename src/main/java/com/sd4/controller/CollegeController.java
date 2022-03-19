package com.sd4.controller;

import com.sd4.model.College;
import com.sd4.service.CollegeService;
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
@RequestMapping(value = "college")
public class CollegeController
{
	@Autowired
	private CollegeService collegeService;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<College> createCollege(@RequestBody College newCollege)
	{
		System.out.println(newCollege);
		System.out.println(newCollege.getId());

		System.out.println(newCollege.getModifiedOn());
		College college = collegeService.save(newCollege);
		return ResponseEntity.ok(college);
	}

	@DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deleteCollegeById(@PathVariable("id") long id)
	{
		Optional<College> optional = collegeService.findById(id);
		if (optional.isEmpty())
		{
			return ResponseEntity.notFound().build();
		}
		//collegeService.deleteById(id);
		return ResponseEntity.ok(optional.get());
	}
	@GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public ResponseEntity<College> getCollegeById(@PathVariable("id") long id)
	{
		Optional<College> optional = collegeService.findById(id);
		if (optional.isEmpty())
		{
			return ResponseEntity.notFound().build();
		}
		Link selfLink = linkTo(getClass()).slash(id).withSelfRel();

		optional.get().add(selfLink);
		return ResponseEntity.ok(optional.get());
	}

	@GetMapping(value = "", produces = MediaTypes.HAL_JSON_VALUE)
	public CollectionModel<College> getColleges()
	{
		System.out.println("getColleges");

		List<College> colleges = collegeService.findAll();

		for (College college : colleges)
		{
			long id = college.getId();
			Link selfLink = linkTo(getClass()).slash(id).withSelfRel();
			college.add(selfLink);
		}

		Link link = linkTo(getClass()).withSelfRel();
		CollectionModel<College> collectionModel = CollectionModel.of(colleges, link);
		return collectionModel;
	}

	@PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity updateCollege(@PathVariable("id") long id, @RequestBody College college)
	{
		if (id != college.getId())
		{
			return ResponseEntity.badRequest().build();
		}
		collegeService.save(college);
		return ResponseEntity.ok(college);
	}
}
