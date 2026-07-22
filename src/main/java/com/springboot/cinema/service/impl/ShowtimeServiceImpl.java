package com.springboot.cinema.service.impl;

import com.springboot.cinema.entity.Movie;
import com.springboot.cinema.entity.Room;
import com.springboot.cinema.entity.Showtime;
import com.springboot.cinema.entity.ShowtimeStatus;
import com.springboot.cinema.repository.MovieRepository;
import com.springboot.cinema.repository.RoomRepository;
import com.springboot.cinema.repository.ShowtimeRepository;
import com.springboot.cinema.service.ShowtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShowtimeServiceImpl implements ShowtimeService {

    private static final int ROOM_CLEANUP_BUFFER_MINUTES = 15;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RoomRepository roomRepository;

    public ShowtimeServiceImpl(ShowtimeRepository showtimeRepository) {
        this.showtimeRepository = showtimeRepository;
    }

    @Override
    public Showtime getShowtimeById(Integer showtimeId) {
        return showtimeRepository.findById(showtimeId).orElse(null);
    }

    @Override
    public List<LocalDate> getShowtimeDates(int movieId) {
        List<Showtime> showtimes = showtimeRepository.findUpcomingShowtimesByMovieId(movieId, LocalDateTime.now());
        return showtimes.stream()
                .map(s -> s.getStartTime().toLocalDate())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public Map<Room, List<Showtime>> getShowtimesByRoomAndDate(int movieId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        List<Showtime> showtimes = showtimeRepository.findByMovieIdAndDate(movieId, start, end);
        return showtimes.stream()
                .collect(Collectors.groupingBy(Showtime::getRoom));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Showtime> getShowtimesByMovieId(int movieId) {
        return showtimeRepository.findAllByMovieIdOrderByStartTime(movieId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countSoldTickets(int showtimeId) {
        return showtimeRepository.countActiveBookedTicketsByShowtimeId(showtimeId);
    }

    private ShowtimeStatus statusOf(Showtime showtime) {
        return showtime.getStatus() != null ? showtime.getStatus() : ShowtimeStatus.DRAFT;
    }

    private boolean isPast(Showtime showtime) {
        return showtime.getStartTime() == null || !showtime.getStartTime().isAfter(LocalDateTime.now());
    }

    @Override
    @Transactional
    public Showtime saveShowtime(Showtime showtime, int movieId, int roomId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Phim không tồn tại."));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Phòng chiếu không tồn tại."));

        boolean isUpdate = showtime.getId() != 0;
        Showtime existing = null;
        if (isUpdate) {
            existing = showtimeRepository.findById(showtime.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Suất chiếu không tồn tại."));

            if (statusOf(existing) == ShowtimeStatus.OPEN) {
                throw new IllegalStateException("Suất chiếu đã mở bán vé, không thể chỉnh sửa để đảm bảo tính nhất quán.");
            }
            if (isPast(existing)) {
                throw new IllegalStateException("Suất chiếu đã qua giờ chiếu, không thể chỉnh sửa.");
            }
            long activeTickets = showtimeRepository.countActiveBookedTicketsByShowtimeId(existing.getId());
            if (activeTickets > 0) {
                throw new IllegalStateException("Suất chiếu đã có vé được đặt hoặc đang giữ chỗ, không thể chỉnh sửa.");
            }
        }

        if (showtime.getStartTime() == null || !showtime.getStartTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Thời gian bắt đầu phải ở tương lai.");
        }
        if (showtime.getPrice() == null || showtime.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá vé phải lớn hơn 0.");
        }

        LocalDateTime endTime = showtime.getStartTime().plusMinutes(movie.getDuration());
        LocalDateTime bufferedStart = showtime.getStartTime().minusMinutes(ROOM_CLEANUP_BUFFER_MINUTES);
        LocalDateTime bufferedEnd = endTime.plusMinutes(ROOM_CLEANUP_BUFFER_MINUTES);
        int excludeId = isUpdate ? showtime.getId() : -1;
        if (showtimeRepository.existsOverlappingShowtime(roomId, bufferedStart, bufferedEnd, excludeId)) {
            throw new IllegalStateException("Phòng chiếu cần tối thiểu " + ROOM_CLEANUP_BUFFER_MINUTES +
                    " phút để dọn phòng trước/sau suất chiếu khác, vui lòng chọn khung giờ khác.");
        }

        if (isUpdate) {
            existing.setMovie(movie);
            existing.setRoom(room);
            existing.setStartTime(showtime.getStartTime());
            existing.setEndTime(endTime);
            existing.setPrice(showtime.getPrice());
            return showtimeRepository.save(existing);
        }

        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setEndTime(endTime);
        showtime.setStatus(ShowtimeStatus.DRAFT);
        return showtimeRepository.save(showtime);
    }

    @Override
    @Transactional
    public Showtime openForSale(int showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Suất chiếu không tồn tại."));
        if (statusOf(showtime) == ShowtimeStatus.OPEN) {
            throw new IllegalStateException("Suất chiếu đã được mở bán trước đó.");
        }
        if (isPast(showtime)) {
            throw new IllegalStateException("Không thể mở bán cho suất chiếu đã qua giờ chiếu.");
        }
        showtime.setStatus(ShowtimeStatus.OPEN);
        return showtimeRepository.save(showtime);
    }

    @Override
    @Transactional
    public void deleteShowtime(int showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Suất chiếu không tồn tại."));
        if (statusOf(showtime) == ShowtimeStatus.OPEN) {
            throw new IllegalStateException("Suất chiếu đã mở bán vé, không thể xoá để đảm bảo tính nhất quán.");
        }
        long activeTickets = showtimeRepository.countActiveBookedTicketsByShowtimeId(showtimeId);
        if (activeTickets > 0) {
            throw new IllegalStateException("Không thể xoá suất chiếu đã có vé được đặt hoặc đang giữ chỗ.");
        }
        showtimeRepository.deleteById(showtimeId);
    }
}
