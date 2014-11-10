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
		return (Math.pow(inputVal,2) - 1)/2;
	}
}
