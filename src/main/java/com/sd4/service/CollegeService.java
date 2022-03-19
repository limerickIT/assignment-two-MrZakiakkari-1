package com.sd4.service;

import com.sd4.model.College;
import com.sd4.repository.CollegeRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author M.Zaki Al Akkari <https://github.com/MrZakiakkari>
 */
@Service
public class CollegeService
{
	@Autowired
	private CollegeRepository collegeRepository;

	public void deleteById(long id)
	{
		collegeRepository.deleteById(id);
	}
	public List<College> findAll()
	{
		return (List<College>) collegeRepository.findAll();
	}
	public Optional<College> findById(long id)
	{
		return collegeRepository.findById(id);
	}
	public College save(College college)
	{
		college.setModifiedOn(new Date());
		return collegeRepository.save(college);
	}
}
