package com.moneytransfer.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Account")
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	private Long accountId;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "player_id", nullable = false)
	@JsonBackReference
	private Player player;

	@Column(name = "balance")
	private BigDecimal balance;

	@Column(name = "updated_at")
	@CreationTimestamp
	private LocalDateTime updatedAt;

	public void updateBalance(BigDecimal amount) {
		balance = balance.add(amount);
	}

	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", balance=" + balance + ", updatedAt=" + updatedAt + "]";
	}

}
