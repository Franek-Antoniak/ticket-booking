package touk.recru.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
@ToString
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Ticket extends BaseEntity {
	@Enumerated(EnumType.STRING)
	private TicketType ticketType;

	@ManyToOne
	@JoinColumn(name = "booking_id")
	private Booking booking;
	@OneToOne
	@JoinColumn(name = "seat_id")
	private Seat seat;

	public BigDecimal getPrice() {
		return ticketType.getPrice();
	}
}
