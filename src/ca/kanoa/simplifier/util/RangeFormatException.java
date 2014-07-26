package ca.kanoa.simplifier.util;

public class RangeFormatException extends NumberFormatException {

	private static final long serialVersionUID = 8663194156069090027L;
	
	public RangeFormatException(String exception) {
		super(exception);
	}
	
	public RangeFormatException(NumberFormatException ex) {
		super(ex.getMessage());
	}

}
