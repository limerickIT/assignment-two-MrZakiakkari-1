package com.sd4.service;

import com.sd4.model.Brewery;
import com.sd4.repository.BreweryRepository;
import java.util.List;
import java.util.Optional;
import net.glxn.qrgen.core.vcard.VCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author M.Zaki Al Akkari <https://github.com/MrZakiakkari>
 */
@Service
public class BreweryService
{
	@Autowired
	private BreweryRepository breweryRepository;

	public List<Brewery> findAll()
	{
		return (List<Brewery>) breweryRepository.findAll();
	}

	public Brewery save(Brewery brewery)
	{
		return breweryRepository.save(brewery);
	}

	public Optional<Brewery> findById(long id)
	{
		return breweryRepository.findById(id);
	}

	public VCard getVard(Brewery brewery)
	{
		VCard vCard = new VCard();
		vCard.setName(brewery.getName());
		vCard.setCompany(brewery.getName());
		vCard.setAddress(brewery.getAddress1());
		vCard.setPhoneNumber(brewery.getPhone());
		vCard.setTitle("Brewery");
		vCard.setEmail(brewery.getEmail());
		vCard.setWebsite(brewery.getWebsite());

		return vCard;
	}

	public void deleteById(long id)
	{
		breweryRepository.deleteById(id);
	}

}
