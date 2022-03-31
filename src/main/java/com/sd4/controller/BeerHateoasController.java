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
import com.sd4.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import net.minidev.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.bind.annotation.RequestParam;
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
	private PagedResourcesAssembler<Beer> pagedResourcesAssembler;
	@Autowired
	private StyleRepository styleRepository;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Beer> createBeer(@RequestBody Beer newBeer)
	{
		Beer beer = beerService.save(newBeer);
		return ResponseEntity.ok(beer);
	}

	@Operation(description = "Deletes a beer")
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

	@Operation(summary = "Get a Beer by its id")
	@ApiResponses(value =
	{
		@ApiResponse(responseCode = "200", description = "Found the Beer",
				content =
				{
					@Content(mediaType = "application/json",
							schema = @Schema(implementation = Beer.class))
				}),
		@ApiResponse(responseCode = "400", description = "Invalid id supplied",
				content = @Content),
		@ApiResponse(responseCode = "404", description = "Beer not found",
				content = @Content)
	})
	@GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public ResponseEntity<Beer> getBeerById(@PathVariable("id") long id)
	{
		Optional<Beer> optional = beerService.findById(id);
		if (optional.isEmpty())
		{
			return ResponseEntity.notFound().build();
		}
		Beer beer = optional.get();

		Link selfLink = linkTo(getClass()).slash(id).withSelfRel();
		beer.add(selfLink);

		Link beersLink = linkTo(methodOn(getClass()).getBeers()).withRel("all");
		beer.add(beersLink);

		return ResponseEntity.ok(optional.get());
	}

	@Operation(summary = "Get a beer by its id with it's name, description and the related brewery")
	@GetMapping(value = "/{id}/all", produces = MediaTypes.HAL_JSON_VALUE)
	public ResponseEntity<JSONObject> getBeerDetails(@PathVariable("id") long beerId)
	{
		Optional<Beer> optional = beerService.findById(beerId);
		if (optional.isEmpty())
		{
			return ResponseEntity.notFound().build();
		}
		Optional<Brewery> brewery = breweryService.findById(optional.get().getBrewery_id());
		JSONObject jSONObject = new JSONObject();
		jSONObject.put("Name", optional.get().getName());
		jSONObject.put("Description", optional.get().getDescription());
		jSONObject.put("Brewery Name", brewery.get().getName());

		return ResponseEntity.ok(jSONObject);
	}

	/**
	 * returns all beers
	 *
	 * @return
	 */
	@Operation(summary = "Getting a list of beers")
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

			Link beerLink = linkTo(methodOn(BeerHateoasController.class).getBeerDetails(id)).withRel("details");
			beer.add(beerLink);

		}

		Link link = linkTo(getClass()).withSelfRel();
		CollectionModel<Beer> collectionModel = CollectionModel.of(beers, link);
		return collectionModel;
	}
	@Operation(summary = "Getting beers through pagination")
	@GetMapping(value = "/page", produces = MediaTypes.HAL_JSON_VALUE)
	public PagedModel<EntityModel<Beer>> getAll(
			@RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
			@RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
	)
	{
		Page<Beer> page = beerService.findAll(pageNo, pageSize, sortBy, sortDir);
		for (Beer beer : page.getContent())
		{
			long id = beer.getId();
			Link selfLink = linkTo(getClass()).slash(id).withSelfRel();
			beer.add(selfLink);

			Link beerLink = linkTo(methodOn(BeerHateoasController.class).getBeerDetails(id)).withRel("details");
			beer.add(beerLink);
		}
		Link link = linkTo(getClass()).withSelfRel();

		PagedModel<EntityModel<Beer>> result = pagedResourcesAssembler.toModel(page, link);

		return result;

	}

	/**
	 *
	 * @param beerId
	 * @param size represents the size type of the image
	 * @return
	 * @throws Exception
	 */
	@Operation(summary = "Get beer image", parameters =
	{
		@Parameter(name = "size", description = "Thubnail or Large")
	})
	@GetMapping(value = "/image/{beerId}/{size}", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<BufferedImage> getBeerImage(@PathVariable("beerId") final long beerId, @PathVariable("size") final String size) throws Exception
	{
		final Optional<Beer> optional = beerService.findById(beerId);
		if (optional.isEmpty())
		{
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		final Beer beer = optional.get();
		final String path = "static/assets/images/"
				+ ("thumbnail".equalsIgnoreCase(size) ? "thumbs" : "large") // determines if it is thumbnail or large
				+ "/" + beer.getImage();

		try (final InputStream inputStream = new ClassPathResource(path).getInputStream())
		{
			return ResponseEntity.ok(ImageIO.read(inputStream));
		}
	}
	@Operation(summary = "Get a beer's details on a pdf")
	@GetMapping(value = "/pdf/{beerId}", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<BeerPdfPrinter> getPdf(@PathVariable("beerId") long beerId) throws Exception
	{
		final Optional<Beer> optional = beerService.findById(beerId);
		if (optional.isEmpty())
		{
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		final Beer beer = optional.get();

		Brewery brewery = breweryService.findById(beer.getBrewery_id()).get();
		Category category = categoryService.findById(beer.getCat_id()).get();
		Style style = styleRepository.findById(beer.getStyle_id()).get();

		BeerPdfPrinter beerPdfPrinter = new BeerPdfPrinter(beer, brewery, category, style);

		final File pdfFile = beerPdfPrinter.generatePdfReport();
		try (final InputStream inputStream = new FileInputStream(pdfFile))
		{

			final HttpHeaders responseHeaders = new HttpHeaders();
			final String filename = beer.getName() + ".pdf";

			responseHeaders.set("Content-Disposition", "attachment; filename=\"" + filename + "\"");

			return new ResponseEntity(IOUtils.toByteArray(inputStream), responseHeaders, HttpStatus.OK);
		}
	}
	@Operation(summary = "Get a zipped folder of beer's images")
	@GetMapping(value = "/zipped", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<byte[]> getZippedBeerImages() throws IOException
	{
		final List<Beer> beers = beerService.findAll();
		final File zipFile = BeerService.zipBeerImages(beers);
		try (final InputStream inputStream = new FileInputStream(zipFile))
		{
			final HttpHeaders responseHeaders = new HttpHeaders();
			final String filename = "beer-images.zip";
			responseHeaders.set("Content-Disposition", "attachment; filename=\"" + filename + "\"");

			return new ResponseEntity(IOUtils.toByteArray(inputStream), responseHeaders, HttpStatus.OK);
		}
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
