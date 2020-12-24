package dev.pratul.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Role {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
	@SequenceGenerator(name = "seq_gen", sequenceName = "role_id_seq", schema = "public", allocationSize = 1)
	private Integer id;

	@Column(name = "name")
	@NotBlank(message = "Role name cannot be blank")
	private String name;

	@Column(name = "description")
	@NotBlank(message = "Role description cannot be blank")
	private String description;

	@Column(name = "created_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	@CreationTimestamp
	private ZonedDateTime createdDate;

	@Column(name = "modified_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	@UpdateTimestamp
	private ZonedDateTime modifiedDate;

	public Role(Integer id) {
		this.id = id;
	}

	public Role(Integer id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

}
