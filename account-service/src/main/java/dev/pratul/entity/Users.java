package dev.pratul.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
@ToString
public class Users implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4553516770711472895L;

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
	private Set<Accounts> accounts = new HashSet<>();

	@Column(name = "middle_initial")
	private String middleInitial;

	@JsonIgnore
	@Column(name = "password")
	private String password;

	@Column(name = "status")
	private String status;

	@Column(name = "email")
	private String email;

	@JsonIgnore
	@Column(name = "username")
	private String userName;

}
