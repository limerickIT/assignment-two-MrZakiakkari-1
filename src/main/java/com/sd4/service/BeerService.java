package com.sd4.service;

import com.sd4.model.Beer;
import com.sd4.repository.BeerRepository;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

	public List<Beer> search(String keyword)
	{
		if (keyword != null)
		{
			return beerRepository.search(keyword);
		}
		return (List<Beer>) beerRepository.findAll();
	}
	public List<Beer> findAll()
	{
		return (List<Beer>) beerRepository.findAll();
	}
	public Page<Beer> findAll(int pageNo, int pageSize)
	{
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return beerRepository.findAll(pageable);
	}
	public Page<Beer> findAll(int pageNo, int pageSize, Sort sort)
	{
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		return beerRepository.findAll(pageable);
	}
	public Page<Beer> findAll(int pageNo, int pageSize, String sortBy, String sortDirection)
	{
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		return findAll(pageNo, pageSize, sort);
	}

	public Beer save(Beer beer)
	{
		return beerRepository.save(beer);
	}

	public Optional<Beer> findById(long id)
	{
		return beerRepository.findById(id);
	}
	public void deleteById(long id)
	{
		beerRepository.deleteById(id);
	}

	public static File zipBeerImages(List<Beer> beers) throws IOException
	{
		final List<String> imageFilenames = beers.stream()
				.map(Beer::getImage)
				.distinct()
				.toList();

		final File zipFile = File.createTempFile("result", ".zip");
		try (final FileOutputStream fileOutputStream = new FileOutputStream(zipFile); final ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream))
		{
			for (String imageFilename : imageFilenames)
			{
				final String path = "static/assets/images/large/" + imageFilename;
				final ClassPathResource resource = new ClassPathResource(path);
				addFileToZipStream(resource.getFile(), zipOutputStream);
			}
		}
		return zipFile;
	}

	private static void addFileToZipStream(final File file, final ZipOutputStream zipOutputStream) throws IOException
	{
		final int BUFFER_SIZE = 1024;

		try (final FileInputStream fileInputStream = new FileInputStream(file); final BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, BUFFER_SIZE))
		{
			final ZipEntry zipEntry = new ZipEntry(file.getName());
			zipOutputStream.putNextEntry(zipEntry);
			byte data[] = new byte[BUFFER_SIZE];
			int count;
			while ((count = bufferedInputStream.read(data, 0, BUFFER_SIZE)) != -1)
			{
				zipOutputStream.write(data, 0, count);
			}
			zipOutputStream.closeEntry();
		}
	}
}
