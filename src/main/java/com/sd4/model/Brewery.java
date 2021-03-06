package com.sd4.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

/**
 *
 * @author Alan.Ryan
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Brewery extends RepresentationModel<Brewery> implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String code;
	private String country;
	private String phone;
	private String website;
	private String image;

	@Lob
	private String description;

	private Integer add_user;

	@Temporal(TemporalType.TIMESTAMP)
	private Date last_mod;

	private Double credit_limit;
	private String email;
}
