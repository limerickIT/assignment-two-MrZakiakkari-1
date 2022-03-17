package com.sd4.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.sd4.model.Brewery;
import com.sd4.repository.BreweryRepository;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	BreweryRepository breweryRepository;

	@GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public ResponseEntity<Brewery> getBeerById(@PathVariable("id") long id)
	{
		Optional<Brewery> optional = breweryRepository.findById(id);
		if (optional.isEmpty())
		{
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		Link selfLink = linkTo(methodOn(getClass()).getBeerById(id)).withSelfRel();
		optional.get().add(selfLink);
		return ResponseEntity.ok(optional.get());
	}

	@RequestMapping("/map/{id}")
	public ResponseEntity<String> page(Model model) throws InterruptedException, IOException
	{
		String address = "123 main street, new york, ny";
		try
		{
			GeocodingApiRequest req = GeocodingApi.newRequest(geoApiContext);
			GeocodingResult[] results = req.address(address).await();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonResults = gson.toJson(results);
			return ResponseEntity.ok(jsonResults);
		}
		catch (ApiException e)
		{
			System.out.println(e);
			return ResponseEntity.ok(e.getMessage());
		}
	}

}
