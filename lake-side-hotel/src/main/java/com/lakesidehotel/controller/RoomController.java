package com.lakesidehotel.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lakesidehotel.exception.InternalServerError;
import com.lakesidehotel.exception.PhotoRetrievalException;
import com.lakesidehotel.exception.ResourceNotFoundException;
import com.lakesidehotel.model.BookedRoom;
import com.lakesidehotel.model.Room;
import com.lakesidehotel.respone.BookingResponse;
import com.lakesidehotel.respone.RoomResponse;
import com.lakesidehotel.service.BookingService;
import com.lakesidehotel.service.IRoomService;

import lombok.RequiredArgsConstructor;
@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

	private final IRoomService iRoomService;
	private final BookingService bookingService;
	@PostMapping("/add/new-room")
	public ResponseEntity<RoomResponse> addNewRoom(
			@RequestParam("photo") MultipartFile photo,
			@RequestParam("roomType") String roomType,
			@RequestParam("roomPrice") BigDecimal roomPrice
			) throws IOException, SQLException{
		Room savedRoom=this.iRoomService.addNewRoom(photo,roomType,roomPrice);
		RoomResponse response = new RoomResponse(savedRoom.getId(), savedRoom.getRoomType(), savedRoom.getRoomPrice());
	//return new ResponseEntity<RoomService>(HttpStatus.CREATED);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/room/types")
	public List<String> getRoomType(){
		return this.iRoomService.getAllRoomTypes();
	}
	@GetMapping("/all-rooms")
	public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException, ResourceNotFoundException{
		List<Room> rooms=this.iRoomService.getAllRooms();
		List<RoomResponse> roomResponses=new ArrayList<>();
		for(Room room:rooms) {
			byte[] photoBytes=this.iRoomService.getRoomPhotoByRoomId(room.getId());
			if(photoBytes != null && photoBytes.length>0) {
				String base64Photo=Base64.encodeBase64String(photoBytes);
			RoomResponse roomResponse=getRoomResponse(room);
			roomResponse.setPhoto(base64Photo);
			roomResponses.add(roomResponse);
			}
		
		}
		return ResponseEntity.ok(roomResponses);
	
	}

	private RoomResponse getRoomResponse(Room room) {
		List<BookedRoom> bookedRooms=getAllBookingsByRoomId(room.getId());
		List<BookingResponse> bookingInfo=bookedRooms.stream().map(booking-> new BookingResponse(booking.getBookingId(), booking.getCheckInDate(), booking.getCheckoutDate(), booking.getBookingConfirmationCode())).toList();
		byte[] photoBytes=null;
		Blob blob=room.getPhoto();
		if(blob != null ) {
			try {
				photoBytes=blob.getBytes(1,(int)blob.length());
			} catch (SQLException e) {
				throw new PhotoRetrievalException("Error retrieving photo");
			}
		}
		return new RoomResponse(room.getId(),
				room.getRoomType(),
				room.getRoomPrice(),
				room.isBooked(),
				photoBytes,
				bookingInfo
				);
	}

	private List<BookedRoom> getAllBookingsByRoomId(Long id) {
		
		return this.bookingService.getAllBookingsByRoomId(id);
	}
	
	@DeleteMapping("/delete/room/{roomId}")
	public ResponseEntity<Void> deleteRoom(@PathVariable("roomId") Long roomId){
		 this.iRoomService.deleteRoom(roomId);
		 return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	@PutMapping("/update/{roomId}")
	public ResponseEntity<RoomResponse> updateRoom(
			@PathVariable("roomId") Long roomId,
			@RequestParam(required = false)	String roomType,
		@RequestParam(required = false) BigDecimal roomPrice,
		@RequestParam(required = false) MultipartFile photo) throws IOException, SQLException, ResourceNotFoundException, InternalServerError{
		
		byte[] photoBytes=photo != null && !photo.isEmpty()?photo.getBytes():this.iRoomService.getRoomPhotoByRoomId(roomId);
		Blob photoBlob=photoBytes !=null && photoBytes.length>0 ? new SerialBlob(photoBytes) : null;
		Room theRoom=this.iRoomService.updateRoom(roomId,roomType,roomPrice,photoBytes);
		theRoom.setPhoto(photoBlob);
		RoomResponse roomResponse = getRoomResponse(theRoom);
		return ResponseEntity.ok(roomResponse);
	}
	
	@GetMapping("/room/{roomId}")
	public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable("roomId") Long roomId) throws ResourceNotFoundException{
		Optional<Room> theRoom=this.iRoomService.getRoomByRoomId(roomId);
		
		return 
				theRoom.map(room ->{
					RoomResponse roomResponse =getRoomResponse(room);
					return ResponseEntity.ok(Optional.of(roomResponse));
				}).orElseThrow(()-> new ResourceNotFoundException("Room Not Found..!!"));
			
		
	}
	@GetMapping("/available-rooms")
	public ResponseEntity<List<RoomResponse>> getAvailableRooms(
			@RequestParam("checkInDate") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDate checkInDate
			,
			@RequestParam("checkOutDate") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDate checkOutDate,
			@RequestParam("roomType") String roomType
			) throws SQLException, ResourceNotFoundException{
		List<Room> availableRoom=this.iRoomService.getAvailableRooms(checkInDate,checkOutDate,roomType);
		List<RoomResponse> roomResponse = new ArrayList<>();
		for(Room room:availableRoom) {
			byte[] photoBytes=this.iRoomService.getRoomPhotoByRoomId(room.getId());
			if(photoBytes != null && photoBytes.length>0) {
				String photoBase64=Base64.encodeBase64String(photoBytes);
				RoomResponse response =getRoomResponse(room);
				response.setPhoto(photoBase64);
				roomResponse.add(response);
			}
		}
		
		if(roomResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}else {
			return ResponseEntity.ok(roomResponse);
		}
	}
	
}
