package dev.pratul.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

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
	private Long id;
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
	private Set<Accounts> accounts = new HashSet<>();
}
