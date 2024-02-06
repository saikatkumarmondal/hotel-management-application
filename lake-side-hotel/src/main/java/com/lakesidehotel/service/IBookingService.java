package com.lakesidehotel.service;

import java.util.List;

import com.lakesidehotel.exception.InvalidBookingRequestException;
import com.lakesidehotel.exception.ResourceNotFoundException;
import com.lakesidehotel.model.BookedRoom;

public interface IBookingService {

	void cancelBooking(Long bookingId);

	String saveBooking(Long roomId, BookedRoom bookedRoom) throws InvalidBookingRequestException;

	BookedRoom getBookingByConfirmationCode(String confirmationCode) throws ResourceNotFoundException;

	List<BookedRoom> getAllBooking();

}
