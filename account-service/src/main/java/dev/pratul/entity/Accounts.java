package dev.pratul.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Entity
@Table(name = "accounts", schema = "public")
@Data
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Accounts implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8075681622950591587L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
	@SequenceGenerator(name = "seq_gen", sequenceName = "accounts_id_seq", schema = "public", allocationSize = 1)
	private Long id;

	@NaturalId
	@Column(name = "acc_id")
	private String accountId;

	@Column(name = "acc_name")
	private String accountName;

	@Column(name = "status")
	private boolean status;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_accounts_map", schema = "public", joinColumns = {
			@JoinColumn(name = "acc_id", referencedColumnName = "id", nullable = false, updatable = true) }, inverseJoinColumns = {
					@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = true) })
	private Set<Users> user = new HashSet<>();

	@OneToMany(mappedBy = "accounts", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserAccount> users = new HashSet<>();

	@Column(name = "create_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	private ZonedDateTime createDate;

	@Column(name = "update_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	private ZonedDateTime updateDate;


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Accounts that = (Accounts) obj;
		return Objects.equals(accountId, that.accountId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountId);
	}

}
