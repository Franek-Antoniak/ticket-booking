package touk.recru.app.service.screening;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import touk.recru.app.dto.screening.MovieScreeningDTO;
import touk.recru.app.dto.screening.ScreeningBookingInfoDTO;
import touk.recru.app.dto.seat.SeatInfoViewDTO;
import touk.recru.app.entity.*;
import touk.recru.app.mapper.room.ScreeningSeatBookingInfoMapper;
import touk.recru.app.mapper.screening.ScreeningViewInfoMapper;
import touk.recru.app.mapper.seat.SeatViewInfoMapper;
import touk.recru.app.repository.booking.BookingRepository;
import touk.recru.app.repository.room.ScreeningRoomRepository;
import touk.recru.app.repository.screening.ScreeningRepository;
import touk.recru.app.service.booking.BookingService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScreeningServiceImplTest {

	@Mock
	private ScreeningRepository screeningRepository;
	@Mock
	private ScreeningViewInfoMapper screeningMapper;
	@Mock
	private ScreeningSeatBookingInfoMapper screeningSeatBookingInfoMapper;
	@Mock
	private BookingService bookingService;
	@Mock
	private SeatViewInfoMapper seatViewInfoMapper;
	@Mock
	private ScreeningRoomRepository screeningRoomRepository;
	@Mock
	private BookingRepository bookingRepository;

	@InjectMocks
	private ScreeningServiceImpl screeningService;


	@Test
	void searchByTime_between_times_shouldReturnCorrectPage() {
		// given
		LocalDateTime from = LocalDateTime.now();
		LocalDateTime to = LocalDateTime.now()
				.plusDays(4);
		Pageable pageable = PageRequest.of(0, 10);
		List<Screening> screenings = Arrays.asList(Screening.builder()
				.screeningTime(from)
				.build(), Screening.builder()
				.screeningTime(to)
				.build());
		Page<Screening> expectedPage = new PageImpl<>(screenings, pageable, screenings.size());
		when(screeningRepository.findScreeningByScreeningTimeBetween(from, to, pageable)).thenReturn(expectedPage);

		// when
		Page<MovieScreeningDTO> actualPage = screeningService.searchByTime(from, to, pageable);
		// then
		assertEquals(expectedPage.getTotalElements(), actualPage.getTotalElements());
		assertEquals(expectedPage.getNumber(), actualPage.getNumber());
		assertEquals(expectedPage.getSize(), actualPage.getSize());
		verify(screeningMapper, times(2)).toDto(any());
	}

	@Test
	void searchByTime_after_time_shouldReturnCorrectPage() {
		// given
		LocalDateTime from = LocalDateTime.now();
		Pageable pageable = PageRequest.of(0, 10);
		List<Screening> screenings = Collections.singletonList(Screening.builder()
				.screeningTime(from)
				.build());
		Page<Screening> expectedPage = new PageImpl<>(screenings, pageable, screenings.size());
		when(screeningRepository.findScreeningByScreeningTimeAfter(from, pageable)).thenReturn(expectedPage);

		// when
		Page<MovieScreeningDTO> actualPage = screeningService.searchByTime(from, pageable);

		// then
		assertEquals(expectedPage.getTotalElements(), actualPage.getTotalElements());
		assertEquals(expectedPage.getNumber(), actualPage.getNumber());
		assertEquals(expectedPage.getSize(), actualPage.getSize());
		verify(screeningMapper, times(1)).toDto(any());
	}

	@Test
	void searchAvailableSeats_shouldReturnCorrectDto() {
		// given
		UUID screeningId = UUID.randomUUID();
		Screening screening = Screening.builder()
				.uuid(screeningId)
				.screeningTime(LocalDateTime.now())
				.screeningRoom(ScreeningRoom.builder()
						.id(1L)
						.build())
				.bookings(Arrays.asList(Booking.builder()
						.id(1L)
						.build(), Booking.builder()
						.id(2L)
						.build()))
				.build();
		when(screeningRepository.findScreeningByUuid(screeningId)).thenReturn(Optional.of(screening));
		ScreeningRoom screeningRoom = ScreeningRoom.builder()
				.id(1L)
				.seats(Arrays.asList(Seat.builder()
						.id(1L)
						.build(), Seat.builder()
						.id(2L)
						.build()))
				.build();
		when(screeningRoomRepository.findById(1L)).thenReturn(Optional.of(screeningRoom));
		List<Booking> bookings = Arrays.asList(Booking.builder()
				.id(1L)
				.tickets(List.of(Ticket.builder()
						.seat(Seat.builder()
								.uuid(UUID.randomUUID())
								.build())
						.build()))
				.build(), Booking.builder()
				.id(2L)
				.tickets(List.of(Ticket.builder()
						.seat(Seat.builder()
								.uuid(UUID.randomUUID())
								.build())
						.build()))
				.build());
		when(bookingRepository.findAllByScreening(any())).thenReturn(bookings);
		List<SeatInfoViewDTO> availableSeats = Collections.singletonList(SeatInfoViewDTO.builder()
				.seatId(UUID.randomUUID())
				.build());
		when(bookingService.getAvailableSeats(screeningRoom.getSeats(), bookings)).thenReturn(availableSeats);
		ScreeningBookingInfoDTO expectedDto = ScreeningBookingInfoDTO.builder()
				.screeningId(screeningId)
				.availableSeats(Collections.singletonList(SeatInfoViewDTO.builder()
						.seatId(availableSeats.get(0)
								.getSeatId())
						.build()))
				.build();
		when(screeningSeatBookingInfoMapper.toDto(screening)).thenReturn(expectedDto);

		// when
		Optional<ScreeningBookingInfoDTO> actualDto = screeningService.searchAvailableSeats(screeningId);

		// then
		assertTrue(actualDto.isPresent());
		assertEquals(expectedDto.getScreeningId(), actualDto.get()
				.getScreeningId());
		assertEquals(expectedDto.getAvailableSeats()
				.size(), actualDto.get()
				.getAvailableSeats()
				.size());
	}
}