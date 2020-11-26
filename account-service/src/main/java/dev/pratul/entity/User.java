package dev.pratul.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "users", schema = "public")
@Data
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@EqualsAndHashCode
public class User {

	@Id
	@Column(name = "id")
	@NaturalId
	private Long id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "middle_initial")
	private String middleInitial;

	@Column(name = "status")
	private String status;

	@Column(name = "email")
	private String email;

	@JsonIgnore
	@Column(name = "username")
	private String userName;

	public User() {
	}

	public User(Long id) {
		this.id = id;
	}
}
