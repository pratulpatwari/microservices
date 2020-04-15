package dev.pratul.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
@ToString
public class Users {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
	@SequenceGenerator(name = "seq_gen", sequenceName = "users_id_seq", schema = "public", allocationSize = 1)
	private Long id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "middle_initial")
	private String middleInitial;

	@Column(name = "password")
	private String password;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "username")
	private String username;

	@Column(name = "created_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	private ZonedDateTime createdDate;

	@Column(name = "modified_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	private ZonedDateTime modifiedDate;
}
