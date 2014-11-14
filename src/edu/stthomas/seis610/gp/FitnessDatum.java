package edu.stthomas.seis610.gp;

import java.util.logging.Logger;

//=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
/**
* A datum that will own and control the collection and calculation of the  fitness measurements for a given GP tree.
* 
* @author Robert Driesch (cooter) Nov 11, 2014 2:25:18 PM
* @version 1.2
*/
//=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
public class FitnessDatum implements Comparable<FitnessDatum>, Cloneable {
	private static final Logger Log = Logger.getLogger("Global");
	private Double xStandardizedFitness = new Double(Double.NaN);

	/**
	 * Default constructor for this class
	 */
	public FitnessDatum() {
	}

	/**
	 * Default copy constructor for this class
	 */
	public FitnessDatum(Double aStandardizedValue) {
		setStandardizedFitness(aStandardizedValue);
	}

	/**
	 * @return the fitness measurement data for this datum
	 */
	public Double getValue() {
		// Note: All comparisons to NaN are false, do not allow it to happen.
		return getStandardizedFitness();
	}

	/**
	 * @return the standardized fitness measurement data
	 */
	public Double getStandardizedFitness() {
		// Note: All comparisons to NaN are false, do not allow it to happen.
		return isValid() ? xStandardizedFitness : Double.POSITIVE_INFINITY;
	}

	/**
	 * @param aInputDatum the datum that represents the new standardized fitness measurement
	 */
	public void setStandardizedFitness(FitnessDatum aInputDatum) {
		setStandardizedFitness(aInputDatum.getStandardizedFitness());
	}

	/**
	 * @param aInputValue the double value that represents the new standardized fitness measurement
	 */
	public void setStandardizedFitness(Double aInputValue) {
		this.xStandardizedFitness = aInputValue;
	}

	/**
	 * Clears all of the cached instance variables for this datum.
	 */
	public void clear() {
		xStandardizedFitness = Double.valueOf(0);
	}

	/**
	 * Reset all of the cached instance variables for this datum so that we have some indication of when we are toggling
	 * between different sets of training data inputs & outputs.
	 */
	public void reset() {
		xStandardizedFitness = Double.NaN;
	}

	/**
	 * Determine if this datum has been used to calculate the fitness for an individual tree within the population.
	 * 
	 * @return an indicator if this datum has been populated and contains valid data
	 */
	public boolean isValid() {
		return !xStandardizedFitness.isNaN();
	}

	/**
	 * Returns a FitnessDatum whose value is (this.StandardizedFitness + otherDatum.StandardizedFitness).
	 * 
	 * @param otherDatum the other datum value to be added to this datum
	 * @return the result of the operation (this)
	 */
	public FitnessDatum add(FitnessDatum otherDatum) {
		Log.fine("<BEFORE> this." + this.xStandardizedFitness + "  otherDatum." + otherDatum.xStandardizedFitness);
		if (!this.xStandardizedFitness.isNaN() && !otherDatum.xStandardizedFitness.isNaN()) {
			setStandardizedFitness(this.xStandardizedFitness + otherDatum.xStandardizedFitness);
		} else if (this.xStandardizedFitness.isNaN()) {
			// When the source datum is not a valid number (NaN), then perform a simple assignment rather than the
			// actual operation.
			setStandardizedFitness(otherDatum.xStandardizedFitness);
		}
		Log.fine("<AFTER> Result=this." + this.xStandardizedFitness);

		return this;
	}

	/**
	 * Returns a FitnessDatum whose value is (this.StandardizedFitness + otherDatum.Double).
	 * 
	 * @param otherDouble the other double value to be added to this datum
	 * @return the result of the operation (this)
	 */
	public FitnessDatum add(Double otherDouble) {
		Log.fine("<BEFORE> this." + this.xStandardizedFitness + "  otherDouble." + otherDouble);
		if (!this.xStandardizedFitness.isNaN() && !otherDouble.isNaN()) {
			setStandardizedFitness(this.xStandardizedFitness + otherDouble);
		} else if (this.xStandardizedFitness.isNaN()) {
			// When the source datum is not a valid number (NaN), then perform a simple assignment rather than the
			// actual operation.
			setStandardizedFitness(otherDouble);
		}
		Log.fine("<AFTER> Result=this." + this.xStandardizedFitness);

		return this;
	}

	@Override
	public int compareTo(FitnessDatum otherDatum) {
		Double myFitness = new Double(Double.MAX_VALUE);
		Double otherFitness = new Double(Double.MAX_VALUE);
		try {
			myFitness = this.getValue();
			otherFitness = otherDatum.getValue();
		} catch (Exception e) {
			Log.severe("this." + this + " other." + otherDatum + "  " + e.getMessage());
		}
		
		Log.finest("this.[" + myFitness + "].compareTo(other.[" + otherFitness + "]) = " + myFitness.compareTo(otherFitness));
		return myFitness.compareTo(otherFitness);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((xStandardizedFitness == null) ? 0 : xStandardizedFitness.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object otherValue) {
		if (this == otherValue) return true;
		if (otherValue == null) return false;
	    if (!(otherValue instanceof FitnessDatum)) return false;
	    FitnessDatum otherDatum = (FitnessDatum)otherValue;
		if (this.xStandardizedFitness == null) {
			if (otherDatum.xStandardizedFitness != null)
				return false;
		} else if (this.compareTo(otherDatum) != 0)
			return false;
		return true;
	}

	@Override
	public Object clone() {
		FitnessDatum newDatum = null;

		try {
			// Start by making a shallow clone (copy) of the original fitness datum
			newDatum = (FitnessDatum) super.clone();

			// Perform a deep clone (copy) of some of the values in the datum
			newDatum.setStandardizedFitness(new Double(newDatum.getStandardizedFitness()));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return newDatum;
	}

	@Override
	public String toString() {
		return "" + (isValid() ? xStandardizedFitness : "*Invalid");
	}

	/**
	 * Static method to calculate the standardized fitness measurement for the result from the individual GP function
	 * and the training data value. The standardized fitness skews the better results towards zero and guarantees that
	 * all measurements will be positive.
	 * 
	 * @param aTrainingDatum the output training value (Y) of the target (perfect) function
	 * @param aFitnessMeasurement the total fitness measurement generated from an individual function with the specified
	 *            training data value
	 * @return the resulting standardized fitness measurement for the two inputs
	 */
	public static Double calculateStandardizedFitness(TrainingData aTrainingDatum, Double aFitnessMeasurement) {
		return Math.abs(aTrainingDatum.getOutputData() - aFitnessMeasurement);
	}
}
