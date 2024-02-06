package com.lakesidehotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lakesidehotel.model.BookedRoom;

public interface BookingResponsetory extends JpaRepository<BookedRoom, Long>{
	
	List<BookedRoom> findByRoomId(Long roomId);

	Optional<BookedRoom> findByBookingConfirmationCode(String confirmationCode);
}
