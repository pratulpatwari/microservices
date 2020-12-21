package dev.pratul.entity;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", schema = "public")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
	@SequenceGenerator(name = "seq_gen", sequenceName = "users_id_seq", schema = "public", allocationSize = 1)
	@EqualsAndHashCode.Include
	private Long id;

	@Column(name = "first_name")
	@NotBlank(message = "First Name can not be blank")
	private String firstName;

	@Column(name = "last_name")
	@NotBlank(message = "Last Name can not be blank")
	private String lastName;

	@Column(name = "middle_initial")
	private String middleInitial;

	@Column(name = "password")
	@NotBlank(message = "Password cannot be blank")
	@Size(min = 5, max = 200, message = "Password must be between 5 to 200 length")
	private String password;

	@Column(name = "status")
	private String status = "active";

	@Column(name = "email", unique = true)
	@NotBlank(message = "Email can not be blank")
	@Email(message = "Invalid Email format")
	private String email;

	@Column(name = "username")
	@NotBlank(message = "Username cannot be blank")
	private String userName;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "user_role_map", schema = "public", joinColumns = {
			@JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "role_id", referencedColumnName = "id") })
	private Set<Role> roles = new HashSet<>();

	@Column(name = "created_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	@CreationTimestamp
	private ZonedDateTime createdDate;

	@Column(name = "modified_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	@UpdateTimestamp
	private ZonedDateTime modifiedDate;

	public User(String firstName, String middleInitial, String lastName, String email, Role[] roles) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleInitial = middleInitial;
		this.email = email;
		for (Role role : roles) {
			this.roles.add(role);
		}

	}
}
