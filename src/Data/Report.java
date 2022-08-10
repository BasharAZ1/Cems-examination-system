package Data;

import java.util.Arrays;

/**
 * full report holding distribution for various deciles (0-9, 10-19, ... ,
 * 90-100), median and average
 * 
 * @author Ayala Cohen
 *
 */
public class Report {
	private int average, median;
	private int[] distribution = new int[10]; /* distribution for all the various deciles */

	public int getAverage() {
		return average;
	}

	public void setAverage(int average) {
		this.average = average;
	}

	public int getMedian() {
		return median;
	}

	public void setMedian(int median) {
		this.median = median;
	}

	public int[] getDistribution() {
		return distribution;
	}

	public void setDistribution(int[] distribution) {
		this.distribution = distribution;
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;

		if (!(o instanceof Report))
			return false;

		Report r = (Report) o;

		if (r.getAverage() != this.getAverage() || r.getMedian() != this.getMedian()
				|| !Arrays.equals(r.getDistribution(), this.getDistribution()))
			return false;
		return true;
	}

}
