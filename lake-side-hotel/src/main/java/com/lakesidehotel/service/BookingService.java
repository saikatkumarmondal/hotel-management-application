package com.lakesidehotel.service;

import java.lang.module.ResolutionException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lakesidehotel.exception.InvalidBookingRequestException;
import com.lakesidehotel.exception.ResourceNotFoundException;
import com.lakesidehotel.model.BookedRoom;
import com.lakesidehotel.model.Room;
import com.lakesidehotel.repository.BookingResponsetory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService{
	
	private final BookingResponsetory bookingResponsetory;
	
	private final IRoomService roomService;
	public List<BookedRoom> getAllBookingsByRoomId(Long id) {
	
		return this.bookingResponsetory.findByRoomId(id);
	}

	@Override
	public void cancelBooking(Long bookingId) {
		this.bookingResponsetory.deleteById(bookingId);
		
	}

	@Override
	public String saveBooking(Long roomId, BookedRoom bookingRequest) throws InvalidBookingRequestException {
		if(bookingRequest.getCheckoutDate().isBefore(bookingRequest.getCheckInDate())) {
			throw new InvalidBookingRequestException("check-in must be come before check-out date");
		}
		Room room=this.roomService.getRoomByRoomId(roomId).get();
		List<BookedRoom> existingBookings=room.getBookings();
		boolean roomIsAvailable=roomIsAvailabe(bookingRequest,existingBookings);
		if(roomIsAvailable) {
			room.addBooking(bookingRequest);
			this.bookingResponsetory.save(bookingRequest);
		}else {
			throw new InvalidBookingRequestException("Sorry..!!This room is not available for selected date");
		}
		
		return bookingRequest.getBookingConfirmationCode();
	}

	@Override
	public BookedRoom getBookingByConfirmationCode(String confirmationCode) throws ResourceNotFoundException {
		
		return this.bookingResponsetory.findByBookingConfirmationCode(confirmationCode).orElseThrow(()->new ResourceNotFoundException("Room is not booked..!!"));
	}

	@Override
	public List<BookedRoom> getAllBooking() {
		
		return this.bookingResponsetory.findAll();
	}


	private boolean roomIsAvailabe(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
		
		return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckoutDate().isBefore(existingBooking.getCheckoutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckoutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckoutDate().equals(existingBooking.getCheckoutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckoutDate().isAfter(existingBooking.getCheckoutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckoutDate())
                                && bookingRequest.getCheckoutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckoutDate())
                                && bookingRequest.getCheckoutDate().equals(bookingRequest.getCheckInDate()))
                );
	}

}
