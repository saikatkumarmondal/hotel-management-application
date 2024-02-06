package com.lakesidehotel.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lakesidehotel.exception.InvalidBookingRequestException;
import com.lakesidehotel.exception.ResourceNotFoundException;
import com.lakesidehotel.model.BookedRoom;
import com.lakesidehotel.model.Room;
import com.lakesidehotel.respone.BookingResponse;
import com.lakesidehotel.respone.RoomResponse;
import com.lakesidehotel.service.IBookingService;
import com.lakesidehotel.service.IRoomService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {

	private final IBookingService bookingService;
	private final IRoomService roomService;
	@GetMapping("/all-bookings")
	public ResponseEntity<List<BookingResponse>> getAllBookings(){
	List<BookedRoom> bookings = this.bookingService.getAllBooking();
	List<BookingResponse> bookingResponses = new ArrayList<>();
	for(BookedRoom booking:bookings) {
		BookingResponse bookingResponse = getBookingResponse(booking);
		bookingResponses.add(bookingResponse);
	}
	return ResponseEntity.ok(bookingResponses);
	}
	
	@GetMapping("/confirmation/{confirmationCode}")
	public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable("confirmationCode") String confirmationCode){
		try {
			BookedRoom booking=this.bookingService.getBookingByConfirmationCode(confirmationCode);
			BookingResponse bookingResponse =getBookingResponse(booking);
			return ResponseEntity.ok(bookingResponse);
		} catch (ResourceNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
	
	@PostMapping("/room/{roomId}/booking")
	public ResponseEntity<?> saveBooking(@PathVariable("roomId")Long roomId,@RequestBody BookedRoom bookedRoom){
		try {
			String confirmationCode=this.bookingService.saveBooking(roomId,bookedRoom);
		
			return ResponseEntity.ok("Room Booked successfully..confirmation code is="+confirmationCode);
		
		} catch (InvalidBookingRequestException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@DeleteMapping("/booking/{bookingId}/delete")
	public void cancelBooking(@PathVariable("bookingId")Long bookingId) {
		this.bookingService.cancelBooking(bookingId);
	}
	
	private BookingResponse getBookingResponse(BookedRoom booking) {
	Room theRoom =this.roomService.getRoomByRoomId(booking.getRoom().getId()).get();
	RoomResponse room=new RoomResponse(
	theRoom.getId(),
	theRoom.getRoomType(),
	theRoom.getRoomPrice());
	
	return new BookingResponse(
			booking.getBookingId(),
			booking.getCheckInDate(),
			booking.getCheckoutDate(),
			booking.getGuestFullName(),
			booking.getGuestEmail(),
			booking.getNumOfChildren(),
			booking.getNumOfAdults(),
			booking.getTotalNumOfGuest(),
			booking.getBookingConfirmationCode(),
			room);
	}
}
