package edu.stthomas.seis610.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class SummarizeCSV {
	private static final Logger Log = Logger.getLogger("Global");
	private static final String COMMA = ",", NEWLINE = "\n";

	// private static final String GP_INPUT_FILE = "Sample.20141122-174406.csv";
	private static final String GP_INPUT_FILE = "GPResults.20141122-174406.csv";

	private static File xGpOutputFile;

	public static void main(String[] args) {

		// Specify to only use a simple (text) formatter for the logging.
		Handler myHandler = new ConsoleHandler();
		myHandler.setFormatter(new GPSimpleFormatter());
		Log.addHandler(myHandler);
		Log.setUseParentHandlers(false);

		xGpOutputFile = new File("GPSummary." + GP_INPUT_FILE.split(Pattern.quote("."))[1] + ".csv");

		/*
		 * Write the Header for the CSV File only once.
		 */
		writeCsv(true);

		try {
			BufferedReader CSVFile = new BufferedReader(new FileReader(GP_INPUT_FILE));

			String dataRow = CSVFile.readLine(); // Should just be the header
			LinkedList<String[]> rows = new LinkedList<String[]>();
			ArrayList<Double> genCnt = new ArrayList<Double>();
			ArrayList<Double> elapsedTime = new ArrayList<Double>();

			while (dataRow != null) {
				for (int i = 0; i < 100; i++) {
					if ((dataRow = CSVFile.readLine()) != null) {
						rows.addLast(dataRow.split(","));
						genCnt.add(Double.parseDouble(rows.getLast()[0]));
						elapsedTime.add(Double.parseDouble(rows.getLast()[1]));
					} else {
						Log.severe("Error found while reading a set of data, null line was encountered. [i=" + i + "]");
						break;
					}
				}

				if (rows.size() == 100) {
					Double avgGenCnt = calculateAvg(genCnt);
					Double trimmedGenCnt = calculateTrimmedAvg(genCnt, 0.10);
					Double avgElapsedTime = calculateAvg(elapsedTime);
					Double trimmedElapsedTime = calculateTrimmedAvg(elapsedTime, 0.10);
					writeCsvSummary(rows, "" + rows.size(), avgElapsedTime, avgGenCnt, trimmedElapsedTime,
							trimmedGenCnt);

					rows.clear(); // Get ready to start over
					genCnt.clear();
					elapsedTime.clear();
					dataRow = CSVFile.readLine(); // Should be a blank row
					if (dataRow == null || dataRow.indexOf(',') > 0) {
						Log.severe("Error because record is a not a blank separator like we expect!");
						Log.severe("" + dataRow);
					}
				}
			}

			Log.info("DONE!");
			CSVFile.close();
		} catch (IOException ex) {
			Log.log(Level.SEVERE, "Error attempting to write the CSV ", ex);
			ex.printStackTrace();
		}

	}

	public static Double calculateAvg(ArrayList<Double> aDoubleList) {
		Double sum = new Double(0);
		for (Double i : aDoubleList) {
			sum = sum + i;
		}
		return sum / aDoubleList.size();
	}

	public static Double calculateTrimmedAvg(ArrayList<Double> aDoubleList, double aPercent) {
		int trimmedCount = (int) Math.floor(aPercent * aDoubleList.size());
		Collections.sort(aDoubleList);
		Double sum = new Double(0);
		for (Double i : aDoubleList.subList(trimmedCount, (aDoubleList.size() - trimmedCount + 1))) {
			sum = sum + i;
		}
		return sum / (aDoubleList.size() - trimmedCount * 2);
	}

	public static void writeCsvSummary(LinkedList<String[]> aCsvLines, String aLabel, double aAvgElapsedTime,
			double aAvgGenerationCnt, double aTrimmedElapsedTime, double aTrimmedGenCnt) {
		try {
			FileWriter csvWriter = new FileWriter(xGpOutputFile, true);
			String[] firstLine = aCsvLines.get(1);

			// Write the text label eye catcher...
			csvWriter.append(aLabel);

			// Write the average number of generations it took to find the best plan
			csvWriter.append(COMMA);
			csvWriter.append(Double.toString(aAvgGenerationCnt));
			csvWriter.append(COMMA);
			csvWriter.append(Double.toString(aTrimmedGenCnt));

			// Write the average elapsed time it took to determine the best plan for the final generation.
			csvWriter.append(COMMA);
			csvWriter.append(Double.toString(aAvgElapsedTime));
			csvWriter.append(COMMA);
			csvWriter.append(Double.toString(aTrimmedElapsedTime));

			// Write Size Settings
			csvWriter.append(COMMA);
			csvWriter.append(firstLine[3]);
			csvWriter.append(COMMA);
			csvWriter.append(firstLine[4]);
			csvWriter.append(COMMA);
			csvWriter.append(firstLine[5]);

			// Write Height Settings
			csvWriter.append(COMMA);
			csvWriter.append(firstLine[6]);
			csvWriter.append(COMMA);
			csvWriter.append(firstLine[7]);
			csvWriter.append(COMMA);
			csvWriter.append(firstLine[8]);

			// Write String Value Settings
			csvWriter.append(COMMA);
			csvWriter.append(firstLine[9]);
			csvWriter.append(COMMA);
			csvWriter.append(firstLine[10]);
			csvWriter.append(COMMA);
			csvWriter.append(firstLine[11]);

			// Write Probability Value Settings
			csvWriter.append(COMMA);
			csvWriter.append(firstLine[12]);
			csvWriter.append(COMMA);
			csvWriter.append(firstLine[13]);
			csvWriter.append(COMMA);
			csvWriter.append(firstLine[14]);
			csvWriter.append(COMMA);
			csvWriter.append(firstLine[15]);

			// Write a new line character at the end of this record.
			csvWriter.append(NEWLINE);

			// Make sure the results are written and flushed out to the file.
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException ex) {
			Log.log(Level.SEVERE, "Error attempting to write the CSV ", ex);
			ex.printStackTrace();
		}
	}

	public static void writeCsv(boolean aHeader) {
		try {
			FileWriter csvFile = new FileWriter(xGpOutputFile, true);

			if (aHeader) {
				// Write the Label Keyword for this record
				csvFile.append("Summary Label");

				// Write the number of generations it took to find the best plan
				csvFile.append(COMMA);
				csvFile.append("Avg Generation Cnt");
				csvFile.append(COMMA);
				csvFile.append("Trimmed Generation Cnt");

				// Write the elapsed time it took to determine the best plan for the final generation.
				// Write the number of generations it took to find the best plan
				csvFile.append(COMMA);
				csvFile.append("Avg Elapsed Time (milliseconds)");
				csvFile.append(COMMA);
				csvFile.append("Trimmed Elapsed Time (milliseconds)");

				// Write the details of the Settings used to find this GP.
				csvFile.append(COMMA);
				csvFile.append("Population Size,Tournament Size,Max Generations");
				csvFile.append(COMMA);
				csvFile.append("Initial Tree Height,Max Tree Height,Mutation Tree Height");
				csvFile.append(COMMA);
				csvFile.append("Initial Generation Method,Reproduction Method,Input Training Data");
				csvFile.append(COMMA);
				csvFile.append("Final Fitness Goal,Crossover Probability,Reproduction Probability,Mutation Probability");
			}

			// Write a new line character at the end of this record.
			csvFile.append(NEWLINE);

			// Make sure the results are written and flushed out to the file.
			csvFile.flush();
			csvFile.close();
		} catch (IOException ex) {
			Log.log(Level.SEVERE, "Error attempting to write the CSV ", ex);
			ex.printStackTrace();
		}
	}
}
