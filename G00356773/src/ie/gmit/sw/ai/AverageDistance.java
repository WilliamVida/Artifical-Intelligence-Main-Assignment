package ie.gmit.sw.ai;

public class AverageDistance {

	public double getAverageDistance(int row, int col) {
		double averageDistance;

		averageDistance = (Math.abs(30 - row) + Math.abs(30 - col)) / 2;

		return averageDistance;
	}

}
