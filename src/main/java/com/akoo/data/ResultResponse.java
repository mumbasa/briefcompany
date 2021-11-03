package com.akoo.data;

import java.util.List;

public class ResultResponse {
	private String response;
	private List<Integer> intArray;
	private Object payload;
	private int statusCode;
	
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public List<Integer> getIntArray() {
		return intArray;
	}

	public void setIntArray(List<Integer> intArray) {
		this.intArray = intArray;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

}
