package chemAnalytics;

import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelAPI {

	public static final String DEFAULT_PATH = System.getProperty("user.home") + File.separator + "chemAnalytics"
			+ File.separator;
	public static final String EXCEL_EXTENSION = ".xls";

	private Sheet xlsMasterSheet;
	private static String xlsMasterFileName = "master" + EXCEL_EXTENSION;

	private WritableSheet xlsDataSheet;
	private static String xlsDataFileName = "analytics" + EXCEL_EXTENSION;

	private static String storagePath = DEFAULT_PATH;

	public ExcelAPI() {
		initSheets();
	}

	private void initSheets() {
		File storageDir = new File(storagePath);
		if(!storageDir.exists()) {
			storageDir.mkdir();
		}
		File xlsMasterFile = new File(storagePath, xlsMasterFileName);
		File xlsDataFile = new File(storagePath, xlsDataFileName);

		try {
			xlsMasterSheet = getMasterSheet(xlsMasterFile, 0);
			xlsDataSheet = getAnalyticsSheet(xlsDataFile, "analytics");
		} catch (BiffException | WriteException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeAnalytics(String[] studentData) throws IllegalArgumentException {

	}

	/**
	 * Finds row in Master spreadsheet containing <code>studentID</code> and returns
	 * all pertinent data for that student if in table, otherwise returns null
	 * 
	 * @param studentID String containing 8-digit UML Student ID
	 * 
	 * @return String[4] in the format {Full Name, Affil / Major, Year, Class} null
	 *         if student not found
	 */
	private String[] getRawStudentData(Integer studentID) {
		if (xlsMasterSheet == null) {
			System.out.println("Master sheet is null");
			System.exit(1);
		}
		Cell studentCell = xlsMasterSheet.findCell(studentID.toString());
		if (studentCell == null)
			return null;
		int studentRow = studentCell.getRow();

		return new String[] { xlsMasterSheet.getCell(1, studentRow).getContents(),
				xlsMasterSheet.getCell(2, studentRow).getContents(),
				xlsMasterSheet.getCell(3, studentRow).getContents(),
				xlsMasterSheet.getCell(4, studentRow).getContents() };
	}

	/**
	 * 
	 * 
	 * @param rawStudentData String[4] in the format {Full Name, Affil / Major,
	 *                       Year, Class}
	 * @return String[6] in the format {First Name, Last Name, Affil, Major, Year,
	 *         Class}
	 * @throws IllegalArgumentException if rawStudentData is incorrect length for
	 *                                  processing
	 */
	private String[] processRawStudentData(String[] rawStudentData) throws IllegalArgumentException {
		if (rawStudentData.length != 4)
			throw new IllegalArgumentException(
					"raw student data provided incorrect length " + rawStudentData.length + ", expected length 4");

		// Separate Raw Data for easier manipulation
		String fullName = rawStudentData[0];
		String affil = rawStudentData[1];
		String year = rawStudentData[2];
		String sClass = rawStudentData[3];

		// Create new String[] to hold processed data
		String[] pStudentData = new String[6];
		pStudentData[0] = fullName.substring(fullName.indexOf(',') + 1).trim(); // First Name
		pStudentData[1] = fullName.substring(0, fullName.indexOf(',')).trim(); // Last Name
		pStudentData[2] = affil.substring(0, affil.indexOf('–')).trim(); // Affiliated College
		pStudentData[3] = affil.substring(affil.indexOf('–') + 1).trim(); // Major
		pStudentData[4] = year; // Gradelevel
		pStudentData[5] = sClass; // Class Taking

		return pStudentData;
	}

	/**
	 * Retrieves data from master spreadsheet using <code>getRawStudentData</code>
	 * and formats the data to be more easily readable to the user.
	 * 
	 * @param studentID String containing 8-digit UML Student ID
	 * @return String[6] in the format {First Name, Last Name, Affil, Major, Year,
	 *         Class} otherwise null if student not found.
	 */
	public String[] getProcessedStudentData(String studentID) {
		String[] rawStudentData = getRawStudentData(new Integer(studentID));
		if (rawStudentData == null)
			return new String[] { "Student Not Found" };
		return processRawStudentData(rawStudentData);
	}

	private static Sheet getMasterSheet(File xlsFile, int sheetIndex)
			throws IOException, BiffException, WriteException {
		WritableWorkbook wWorkbook = null;
		Workbook workbook = null;

		if (!xlsFile.exists()) {
			JOptionPane.showMessageDialog(null, "No Master Sheet Was Found, Creating Default Sheet...");

			if (!xlsFile.createNewFile()) {
				System.err.println("Error Creating Master File!");
				System.exit(1);
			}
			wWorkbook = Workbook.createWorkbook(xlsFile);
			wWorkbook.createSheet("master", 0);

			// TODO Formatting specific to Master Workbook...find a new place to utilize
			// this method

			formatMasterWorkbook(wWorkbook, 0);

			wWorkbook.write();
			wWorkbook.close();

			workbook = Workbook.getWorkbook(xlsFile);
		} else {
			workbook = Workbook.getWorkbook(xlsFile);
			wWorkbook = Workbook.createWorkbook(new File(storagePath, "temp.xls"), workbook);
			workbook.close();

			formatMasterWorkbook(wWorkbook, 0);

			wWorkbook.write();
			wWorkbook.close();

			workbook = Workbook.getWorkbook(xlsFile);
		}

		return workbook.getSheet(sheetIndex);
	}

	private static void formatMasterWorkbook(WritableWorkbook wMasterWkbk, int sheetName) {
		WritableSheet wMasterSheet = wMasterWkbk.getSheet(sheetName);
		WritableCell tempCell = wMasterSheet.getWritableCell(0, 0);

		if (tempCell.getContents().equals("Notify")) {
			wMasterSheet.removeColumn(0);
			wMasterSheet.removeColumn(0);
			wMasterSheet.removeColumn(2);
			wMasterSheet.removeColumn(2);

			if (wMasterSheet.getColumns() < 5) {
				wMasterSheet.insertColumn(4);
				try {
					Label label = new Label(4, 0, "Class");
					wMasterSheet.addCell(label);
				} catch (RowsExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private static WritableSheet getAnalyticsSheet(File xlsFile, String sheetName)
			throws IOException, BiffException, WriteException {
		WritableWorkbook wWorkbook = null;
		WritableSheet wSheet = null;
		Workbook workbook = null;

		// Create the workbook if it doesn't already exist
		if (!xlsFile.exists()) {
			if (!xlsFile.createNewFile()) {
				System.err.println("Error Creating Data File!");
				System.exit(1);
			}
			wWorkbook = Workbook.createWorkbook(xlsFile);
			wWorkbook.createSheet(sheetName, 0);
			wWorkbook.write();
			wWorkbook.close();

			workbook = Workbook.getWorkbook(xlsFile);
		} else {
			workbook = Workbook.getWorkbook(xlsFile);
			wWorkbook = Workbook.createWorkbook(new File(storagePath, "temp.xls"), workbook);
			wSheet = wWorkbook.getSheet(sheetName);
			workbook.close();
			wWorkbook.close();
		}

		return wSheet;
	}

}
