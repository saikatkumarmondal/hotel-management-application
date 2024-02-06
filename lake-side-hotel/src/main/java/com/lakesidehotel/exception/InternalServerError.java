package com.lakesidehotel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InternalServerError extends Exception {

	public InternalServerError(String mgs) {
		super(mgs);
	}
}
