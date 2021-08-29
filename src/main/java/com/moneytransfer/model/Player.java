package com.moneytransfer.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "Player")
public class Player {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "player_id")
  private Long playerId;

  @Column(name = "player_name", nullable = false)
  private String playerName;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonManagedReference
  private Account account;

}
