/**
 * 
 */
package gp;


public class TrainingData {
	
	// ESCA-JAVA0098:
	Double inputData;
	Double outputData;
	
	// ESCA-JAVA0117:
	public TrainingData(Double input, Double output) {
		this.inputData=input;
		this.outputData=output;
	}
	
	// ESCA-JAVA0117:
	public Double getInputData() {
		return inputData;
	}
	
	// ESCA-JAVA0117:
	public Double getOutputData() {
		return outputData;
	}
	
	public void setInputData(Double aInputData) {
		inputData = aInputData;
	}
	
	public void setOutputData(Double aOutputData) {
		outputData = aOutputData;
	}

}
