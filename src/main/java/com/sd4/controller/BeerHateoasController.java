package com.sd4.controller;

import com.sd4.model.Beer;
import com.sd4.service.BeerService;
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
@RequestMapping(value = "beers")
public class BeerHateoasController
{
	@Autowired
	private BeerService beerService;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Beer> createBeer(@RequestBody Beer newBeer)
	{
		Beer beer = beerService.save(newBeer);
		return ResponseEntity.ok(beer);
	}

	@DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deleteBeerById(@PathVariable("id") long id)
	{
		Optional<Beer> optional = beerService.get(id);
		if (optional.isEmpty())
		{
			return ResponseEntity.notFound().build();
		}
		//beerService.deleteById(id);
		return ResponseEntity.ok(optional.get());
	}
	@GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public ResponseEntity<Beer> getBeerById(@PathVariable("id") long id)
	{
		Optional<Beer> optional = beerService.get(id);
		if (optional.isEmpty())
		{
			return ResponseEntity.notFound().build();
		}
		Link selfLink = linkTo(getClass()).slash(id).withSelfRel();

		optional.get().add(selfLink);
		return ResponseEntity.ok(optional.get());
	}

	@GetMapping(value = "", produces = MediaTypes.HAL_JSON_VALUE)
	public CollectionModel<Beer> getBeers()
	{
		System.out.println("getBeers");

		List<Beer> beers = beerService.listAll();

		for (Beer beer : beers)
		{
			long id = beer.getId();
			Link selfLink = linkTo(getClass()).slash(id).withSelfRel();
			beer.add(selfLink);
		}

		Link link = linkTo(getClass()).withSelfRel();
		CollectionModel<Beer> collectionModel = CollectionModel.of(beers, link);
		return collectionModel;
	}

	@PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity updateBeer(@PathVariable("id") long id, @RequestBody Beer beer)
	{
		if (id != beer.getId())
		{
			return ResponseEntity.badRequest().build();
		}
		beerService.save(beer);
		return ResponseEntity.ok(beer);
	}
}
