package com.sd4.service;

import com.sd4.model.Beer;
import com.sd4.repository.BeerRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author M.Zaki Al Akkari <https://github.com/MrZakiakkari>
 */
@Service
public class BeerService
{
	@Autowired
	private BeerRepository beerRepository;

	public List<Beer> listAll(String keyword)
	{
		if (keyword != null)
		{
			return beerRepository.search(keyword);
		}
		return (List<Beer>) beerRepository.findAll();
	}
	public List<Beer> listAll()
	{
		return (List<Beer>) beerRepository.findAll();
	}

	public Beer save(Beer beer)
	{
		return beerRepository.save(beer);
	}

	public Optional<Beer> get(long id)
	{
		return beerRepository.findById(id);
	}
	public void deleteById(long id)
	{
		beerRepository.deleteById(id);
	}

}
