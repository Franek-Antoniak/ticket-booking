package touk.recru.app.repository.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import touk.recru.app.entity.Ticket;


@Repository
interface JpaTicketRepository extends TicketRepository, JpaRepository<Ticket, Long> {
}