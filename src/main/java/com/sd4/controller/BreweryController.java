package com.sd4.controller;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GaeRequestHandler;
import com.google.maps.GeoApiContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author M.Zaki Al Akkari <https://github.com/MrZakiakkari>
 */
@Controller
@RequestMapping("brewery")
public class BreweryController
{

	@RequestMapping("/map/{id}")
	public String page(Model model)
	{
		GeoApiContext geoApiContext = new GeoApiContext.Builder(new GaeRequestHandler.Builder())
				.apiKey("AIza...")
				.build();

		String location = "123 main street, new york, ny";
		try
		{
			GeocodingApiRequest req = GeocodingApi.newRequest(context);
			GeocodingResult[] results = req.address(address).await();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonResults = gson.toJson(results);
		}
		catch (ApiException e)
		{
			//Handle API exceptions here
		}

		DirectionsApiRequest apiRequest = DirectionsApi.newRequest(geoApiContext);
		model.addAttribute("attribute", "value");
		return "view.name";
	}

}
