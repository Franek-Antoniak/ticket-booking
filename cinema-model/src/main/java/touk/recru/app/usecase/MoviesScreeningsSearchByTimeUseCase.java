package touk.recru.app.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import touk.recru.app.dto.room.ScreeningBookingInfoDTO;
import touk.recru.app.dto.screening.ScreeningViewInfoDTO;
import touk.recru.app.factory.ScreeningPageableFactory;
import touk.recru.app.service.screening.ScreeningService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class MoviesScreeningsSearchByTimeUseCase {
	private final ScreeningService screeningService;
	private final ScreeningPageableFactory pageFactory;

	public Page<ScreeningViewInfoDTO> compute(LocalDateTime from) {
		return screeningService.searchByTime(from, pageFactory.defaultPageable());
	}

	public Page<ScreeningViewInfoDTO> compute(LocalDateTime from, LocalDateTime to) {
		return screeningService.searchByTime(from, to, pageFactory.defaultPageable());
	}

	public Page<ScreeningViewInfoDTO> compute(LocalDateTime from, int page, int size) {
		return screeningService.searchByTime(from, pageFactory.create(page, size));
	}

	public Page<ScreeningViewInfoDTO> compute(LocalDateTime from, LocalDateTime to, int page, int size) {
		return screeningService.searchByTime(from, to, pageFactory.create(page, size));
	}
}