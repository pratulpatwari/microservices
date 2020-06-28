package dev.pratul.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user_accounts_map", schema = "public")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserAccount implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1328570349868916192L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
	@SequenceGenerator(name = "seq_gen", sequenceName = "user_accounts_id_seq", schema = "public", allocationSize = 1)
	private Long id;

	@EqualsAndHashCode.Include
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "acc_id")
	private Accounts accounts;

	@EqualsAndHashCode.Include
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users users;

	@Column(name = "status")
	private boolean status;

	@Column(name = "created_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	@CreationTimestamp
	private ZonedDateTime createDate;

	@Column(name = "modified_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	@UpdateTimestamp
	private ZonedDateTime updateDate;

	public UserAccount() {
	}

	public UserAccount(Users user, boolean status) {
		this.users = user;
		this.status = status;
	}
}
