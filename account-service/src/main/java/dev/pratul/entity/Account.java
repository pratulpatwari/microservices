package dev.pratul.entity;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "account", schema = "public")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Account {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
	@SequenceGenerator(name = "seq_gen", sequenceName = "account_id_seq", schema = "public", allocationSize = 1)
	private Long id;

	@EqualsAndHashCode.Include
	@NaturalId
	@Column(name = "acc_id")
	private String accountId;

	@Column(name = "acc_name")
	private String accountName;

	@Column(name = "status")
	private boolean status = true;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_account_map", schema = "public", joinColumns = {
			@JoinColumn(name = "acc_id", referencedColumnName = "id", nullable = false, updatable = true) }, inverseJoinColumns = {
					@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = true) })
	private Set<User> user = new HashSet<>();

	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserAccount> userAccount = new HashSet<>();

	@Column(name = "create_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	private ZonedDateTime createDate;

	@Column(name = "update_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	private ZonedDateTime updateDate;

	public Account() {
	}

	public Account(String accountId, String accountName, UserAccount... userAccount) {
		this.accountId = accountId;
		this.accountName = accountName;
		for (UserAccount account : userAccount) {
			account.setAccount(this);
		}
		this.userAccount = Stream.of(userAccount).collect(Collectors.toSet());
	}
}
