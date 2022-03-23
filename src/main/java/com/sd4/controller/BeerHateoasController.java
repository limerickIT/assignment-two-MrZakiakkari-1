package com.sd4.controller;

import com.sd4.model.Beer;
import com.sd4.model.Brewery;
import com.sd4.model.Category;
import com.sd4.model.Style;
import com.sd4.repository.StyleRepository;
import com.sd4.service.BeerPdfPrinter;
import com.sd4.service.BeerService;
import com.sd4.service.BreweryService;
import com.sd4.service.CategoryService;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import org.springframework.http.HttpStatus;
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
@RequestMapping(value = "beer")
public class BeerHateoasController
{
	@Autowired
	private BeerService beerService;
	@Autowired
	private BreweryService breweryService;

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private StyleRepository styleRepository;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Beer> createBeer(@RequestBody Beer newBeer)
	{
		Beer beer = beerService.save(newBeer);
		return ResponseEntity.ok(beer);
	}

	@DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deleteBeerById(@PathVariable("id") long id)
	{
		Optional<Beer> optional = beerService.findById(id);
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
		Optional<Beer> optional = beerService.findById(id);
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

		List<Beer> beers = beerService.findAll();

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

	@GetMapping(value = "/image/{beerId}/{size}", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<BufferedImage> getBeerImage(@PathVariable("beerId") long beerId, @PathVariable("size") String size) throws Exception
	{
		Optional<Beer> optional = beerService.findById(beerId);
		if (optional.isEmpty())
		{
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}

		String path = "static/assets/images/"
				+ ("thumbnail".equalsIgnoreCase(size) ? "thumbs" : "large")
				+ "/" + optional.get().getImage();

		System.out.println(path);
		final InputStream inputStream = new ClassPathResource(path).getInputStream();

		BufferedImage bufferedImage = ImageIO.read(inputStream);
		return ResponseEntity.ok(bufferedImage);
	}
	@GetMapping(value = "/pdf/{beerId}", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<BufferedImage> getPdf(@PathVariable("beerId") long beerId) throws Exception
	{
		Optional<Beer> optional = beerService.findById(beerId);
		if (optional.isEmpty())
		{
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}

		Optional<Brewery> brewery = breweryService.findById(optional.get().getBrewery_id());
		Optional<Category> category = categoryService.findById(optional.get().getCat_id());
		Optional<Style> style = styleRepository.findById(optional.get().getStyle_id());
		BeerPdfPrinter beerPdfPrinter = new BeerPdfPrinter(optional.get(), brewery.get(), category.get(), style.get());

		beerPdfPrinter.generatePdfReport();

		return ResponseEntity.ok(bufferedImage);
	}
}
