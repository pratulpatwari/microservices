package dev.pratul.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "users", schema = "public")
@Data
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Users implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4553516770711472895L;

	@Id
	@Column(name = "id")
	@NaturalId
	private Long id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

//	@ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
//	private Set<Accounts> accounts = new HashSet<>();

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserAccount> accounts = new ArrayList<>();

	@Column(name = "middle_initial")
	private String middleInitial;

	@Column(name = "status")
	private String status;

	@Column(name = "email")
	private String email;

	@JsonIgnore
	@Column(name = "username")
	private String userName;

	public Users() {
	}

	public Users(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Users that = (Users) obj;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
