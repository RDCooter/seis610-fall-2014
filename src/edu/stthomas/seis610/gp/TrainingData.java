package edu.stthomas.seis610.gp;

public class TrainingData {
	Double inputData;
	Double outputData;

	public TrainingData(Double input, Double output) {
		setInputData(input);
		setOutputData(output);
	}

	public Double getInputData() {
		return inputData;
	}

	public void setInputData(Double aInputData) {
		this.inputData = aInputData;
	}

	public Double getOutputData() {
		return outputData;
	}

	public void setOutputData(Double aOutputData) {
		this.outputData = aOutputData;
	}

	@Override
	public String toString() {
		return "x=" + inputData + ", y=" + outputData;
	}

	/**
	 * Static method to determine the result of the target (perfect) function from a given input value.
	 * 
	 * @param the input value (X) of the target (perfect) function
	 * @return the desired output of the tree for the given input
	 */
	public static Double calculatePerfectOutput(Double inputVal) {
//		return calculateXSquareMinusOneOverTwo(inputVal);
		return calculateTwoTimesXSquareMinusFourOverTwo(inputVal);
	}

	/**
	 * Static method to determine the result of the initial target (perfect) function from a given input value.
	 * <p>
	 * TargetFunction = ((x*x) - 1) / 2
	 * 
	 * @param the input value (X) of the target (perfect) function
	 * @return the desired output of the tree for the given input
	 */
	private static Double calculateXSquareMinusOneOverTwo(Double inputVal) {
		return (Math.pow(inputVal, 2) - 1) / 2;
	}

	/**
	 * Static method to determine the result of the final target (perfect) function from a given input value.
	 * <p>
	 * TargetFunction = (2(x*x) - 4) / 2
	 * 
	 * @param the input value (X) of the target (perfect) function
	 * @return the desired output of the tree for the given input
	 */
	private static Double calculateTwoTimesXSquareMinusFourOverTwo(Double inputVal) {
		return (2 * Math.pow(inputVal, 2) - 4) / 2;
	}
}
