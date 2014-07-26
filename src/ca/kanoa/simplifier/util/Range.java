package ca.kanoa.simplifier.util;

public class Range {

	private double minimum;
	private double maximum;

	public Range(double min, double max) {
		this.minimum = min;
		this.maximum = max;
	}

	public Range(double number) {
		this(0, number);
	}

	public Range(int min, int max) {
		this((double) min, (double) max);
	}

	public Range(int number) {
		this((double) number);
	}

	public Range(String range) throws RangeFormatException {
		if (range.contains("..")) {
			if (range.length() == 2) {
				this.minimum = Double.MAX_VALUE * -1;
				this.maximum = Double.MAX_VALUE;
			} else if (range.startsWith("..")) {
				try {
					this.minimum = Double.MAX_VALUE * -1;
					this.maximum = Double.parseDouble(range.substring(2));
				} catch (NumberFormatException ex) {
					throw new RangeFormatException(ex);
				}
			} else if (range.endsWith("..")) {
				try {
					this.minimum = Double.parseDouble(range.substring(0, range.indexOf("..")));
					this.maximum = Double.MAX_VALUE;
				} catch (NumberFormatException ex) {
					throw new RangeFormatException(ex);
				}
			} else {
				try {
					this.minimum = Double.parseDouble(range.substring(0, range.indexOf("..")));
					this.maximum = Double.parseDouble(range.substring(range.indexOf("..") +2));
				} catch (NumberFormatException ex) {
					throw new RangeFormatException(ex);
				}
			}
		} else if (range.length() == 0) {  
			this.minimum = Double.MAX_VALUE * -1;
			this.maximum = Double.MAX_VALUE;
		}
		else {
			double val;
			try {
				val = Double.parseDouble(range);
			} catch (NumberFormatException ex) {
				throw new RangeFormatException(ex);
			}
			this.minimum = val;
			this.maximum = val;
		}
	}
	
	public boolean contains(double number) {
		return number >= this.minimum && number <= this.maximum;
	}
	
	public boolean contains(int number) {
		return contains((double) number);
	}
	
	@Override
	public String toString() {
		return this.minimum + ".." + this.maximum;
	}

}
