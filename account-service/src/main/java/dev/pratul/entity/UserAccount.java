package dev.pratul.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_account_map", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class UserAccount {
	
	@EmbeddedId
	private UserAccountKey id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("accountId")
	@JoinColumn(name = "acc_id")
	private Account account;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("userId")
	@JoinColumn(name = "user_id")
	private User user;

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

	public UserAccount(User user, boolean status) {
		this.user = user;
		this.status = status;
	}
	
	public UserAccount(User user, Account account, boolean status) {
		this.user = user;
		this.account = account;
		this.status = status;
	}
}
