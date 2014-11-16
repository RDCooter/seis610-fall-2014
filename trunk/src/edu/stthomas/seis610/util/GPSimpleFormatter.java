package edu.stthomas.seis610.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Custom Simple Formatter for Single Line Log Messages
 * 
 * @author Robert Driesch 
 */
public class GPSimpleFormatter extends Formatter {
	private static final SimpleDateFormat myDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	/**
	 * Format the LogRecord to more closely match QShell generated logs.
	 * 
	 * <P>
	 * Match the format used by many of the QShell scripts that generate log files by placing the date first enclosed in
	 * brackets, followed by the origin of the log entry and finally followed by the actual message itself.
	 * 
	 * <P>
	 * [2014-09-02 16:44:57.356] SEVERE: <org.jboss.windup.util.ZipUtil unzip:> Failed to load: foo.zip
	 * 
	 * <P>
	 * Similar formatting could be achieved by making use of the Format String that can be specified in the Logger
	 * Properties file or passed as a parameter into the java runtime. A sample of the format string is listed below:
	 * -Djava.util.logging.SimpleFormatter.format=[%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL] %4$s: <%2$s> %5$s%6$s%n
	 *
	 * @param r Specifies the LogRecord object that contains the information about the log entry to be formatted /
	 *            generated.
	 */
	@Override
	public String format(LogRecord r) {

        return "[" + myDateTimeFormat.format(new Date(r.getMillis())) + "] " + r.getLevel() + ": <"
				/*+ r.getSourceClassName() + "::" */+ r.getSourceMethodName() + "> " + r.getMessage()
				+ (r.getThrown() == null ? "" : r.getThrown()) + "\n";
	}

}
