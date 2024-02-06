package com.lakesidehotel.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookedRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bookingId;
	@Column(name = "checke_in")
	private LocalDate checkInDate;
	@Column(name = "checke_out")
	private LocalDate checkoutDate;
	@Column(name = "Guest_FullName")
	private String guestFullName;
	@Column(name = "Guest_Email")
	private String guestEmail;
	@Column(name = "children")
	private Integer numOfChildren;
	@Column(name = "adults")
	private Integer numOfAdults;
	@Column(name = "total_guest")
	private Integer totalNumOfGuest;
	@Column(name = "confirmation_code")
	private String bookingConfirmationCode;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	private Room room;
	
	public void calculateTolatNumberOfGuest() {
		this.totalNumOfGuest=this.numOfAdults+this.numOfChildren;
	}

	

	public void setNumOfChildren(Integer numOfChildren) {
		this.numOfChildren = numOfChildren;
		this.calculateTolatNumberOfGuest();
	}

	

	public void setNumOfAdults(Integer numOfAdults) {
		this.numOfAdults = numOfAdults;
		this.calculateTolatNumberOfGuest();
	}

	public void setBookingConfirmationCode(String bookingConfirmationCode) {
		this.bookingConfirmationCode = bookingConfirmationCode;
	}
	
	
}
