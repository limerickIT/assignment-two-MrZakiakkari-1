package com.sd4.controller;

import com.google.maps.GeoApiContext;
import com.sd4.model.Brewery;
import com.sd4.service.BreweryService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import net.glxn.qrgen.core.AbstractQRCode;
import net.glxn.qrgen.core.vcard.VCard;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author M.Zaki Al Akkari <https://github.com/MrZakiakkari>
 */
@Controller
@RequestMapping("brewery")
public class BreweryController
{
	private GeoApiContext geoApiContext; // 1

	@Autowired
	public BreweryController(@Value("${gmaps.api.key}") String apiKey)
	{
		geoApiContext = new GeoApiContext.Builder().apiKey(apiKey)
				.maxRetries(2)
				.connectTimeout(10L, TimeUnit.SECONDS)
				.build();
	}

	@Autowired
	BreweryService breweryService;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Brewery> createBrewery(@RequestBody Brewery newBrewery)
	{
		Brewery brewery = breweryService.save(newBrewery);
		return ResponseEntity.ok(brewery);
	}

	@DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deleteBreweryById(@PathVariable("id") Long id)
	{
		Optional<Brewery> optional = breweryService.findById(id);
		if (optional.isEmpty())
		{
			return ResponseEntity.notFound().build();
		}
		//breweryService.deleteById(id);
		return ResponseEntity.ok(optional.get());
	}

	@GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public ResponseEntity<Brewery> getBreweryById(@PathVariable("id") long id)
	{
		Optional<Brewery> optional = breweryService.findById(id);
		if (optional.isEmpty())
		{
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		Link selfLink = linkTo(getClass()).slash(id).withSelfRel();
		optional.get().add(selfLink);
		return ResponseEntity.ok(optional.get());
	}

	@GetMapping(value = "", produces = MediaTypes.HAL_JSON_VALUE)
	public CollectionModel<Brewery> getBreweries()
	{
		Iterable<Brewery> breweries = breweryService.findAll();
		for (Brewery brewery : breweries)
		{
			long id = brewery.getId();
			Link selfLink = linkTo(getClass()).slash(id).withSelfRel();
			brewery.add(selfLink);
		}
		Link link = linkTo(getClass()).withSelfRel();
		CollectionModel<Brewery> collectionModel = CollectionModel.of(breweries, link);
		return collectionModel;
	}

	@GetMapping("/map/{breweryId}")
	public ResponseEntity<String> getMap(@PathVariable long breweryId)
	{
		final Optional<Brewery> optional = breweryService.findById(breweryId);
		if (optional.isEmpty())
		{
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		final Brewery brewery = optional.get();

		String name = brewery.getName();
		String address = optional.get().getAddress1();
		String address2 = optional.get().getAddress2();
		String city = optional.get().getCity();
		String country = optional.get().getCountry();
		String code = optional.get().getCode();

		return ResponseEntity.ok("<html><body><h2>" + name + address + address2 + city
				+ "</h2><iframe width=\"600\" height=\"500\" id=\"gmap_canvas\" src=\"https://maps.google.com/maps?q="
				+ name + address + address2 + city + code + country
				+ "=&output=embed\" frameborder=\"0\" scrolling=\"no\" marginheight=\"0\" marginwidth=\"0\">");
	}

	@PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity updateBrewery(@PathVariable("id") long id, @RequestBody Brewery brewery)
	{
		if (id != brewery.getId())
		{
			return ResponseEntity.badRequest().build();
		}
		breweryService.save(brewery);
		return ResponseEntity.ok(brewery);
	}

	@GetMapping(value = "/qrcode/{breweryId}", produces = MediaType.IMAGE_PNG_VALUE)
	public ResponseEntity<BufferedImage> zxingQRCode(@PathVariable("breweryId") long breweryId) throws Exception
	{
		final Optional<Brewery> optional = breweryService.findById(breweryId);
		if (optional.isEmpty())
		{
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}

		final Brewery brewery = optional.get();
		final VCard vCard = BreweryService.getVard(brewery);
		final AbstractQRCode qrCode = QRCode.from(vCard.toString()).withSize(250, 250);

		try (final ByteArrayOutputStream stream = qrCode.stream())
		{
			BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(stream.toByteArray()));
			return ResponseEntity.ok(bufferedImage);
		}
	}

}
