package com.lakesidehotel.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lakesidehotel.exception.InternalServerError;
import com.lakesidehotel.exception.ResourceNotFoundException;
import com.lakesidehotel.model.Room;
import com.lakesidehotel.repository.RoomRepository;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class RoomService implements IRoomService {
	
	private final RoomRepository roomRepository;
	
	@Override
	public Room addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice) throws IOException,SQLException {
		Room room = new Room();
		room.setRoomType(roomType);
		room.setRoomPrice(roomPrice);
		if(!photo.isEmpty()) {
			byte[] photoBytes=photo.getBytes();
			Blob photoBlob = new SerialBlob(photoBytes);
			room.setPhoto(photoBlob);
		}
		return this.roomRepository.save(room);
	}

	@Override
	public List<String> getAllRoomTypes() {
		
		return this.roomRepository.findDistinctRoomTypes();
	}

	@Override
	public List<Room> getAllRooms() {
		
		return this.roomRepository.findAll();
	}

	@Override
	public byte[] getRoomPhotoByRoomId(Long id) throws ResourceNotFoundException, SQLException {
		Optional<Room> room = this.roomRepository.findById(id);
		if(room.isEmpty()) {
			throw new ResourceNotFoundException("Sorry..!!Room Not found..!!");
		}
		Blob blobPhoto = room.get().getPhoto();
		if(blobPhoto != null) {
			return blobPhoto.getBytes(1, (int)blobPhoto.length());
		}
		return null;
	}

	@Override
	public void deleteRoom(Long roomId) {
	Optional<Room> room=this.roomRepository.findById(roomId);
	if(room.isPresent()) {
		this.roomRepository.deleteById(roomId);
	}
		
	}

	@Override
	public Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes) throws ResourceNotFoundException, InternalServerError {
		Room room=this.roomRepository.findById(roomId).orElseThrow(()-> new ResourceNotFoundException("Room Not Found...!!!"));
		if(roomType != null) room.setRoomType(roomType);
		if(roomPrice != null) room.setRoomPrice(roomPrice);
		if(photoBytes != null && photoBytes.length>0) {
			try {
				room.setPhoto(new SerialBlob(photoBytes));
			} catch (SQLException e) {
				throw new InternalServerError("Error updating room..!!");
			}
		}
		return this.roomRepository.save(room);
	}

	@Override
	public Optional<Room> getRoomByRoomId(Long roomId) {
		
		return Optional.of(this.roomRepository.findById(roomId)).get();
	}

	@Override
	public List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
		
		return this.roomRepository.findAvailableRoomsByDatesAndType(checkInDate,checkOutDate,roomType);
	}

}
