package org.example.timesheet.reports;

import java.io.IOException;
import java.io.OutputStream;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.example.timesheet.TimesheetConstants;
import org.example.timesheet.processors.DayWorkData;
import org.example.timesheet.util.DateConverter;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

@Named
public class ExcelReportWriter {
	@Inject
	private DateConverter dateConverter;
	
	interface SheetCfg {
		interface Rows {
			public static final int DATE = 0;
		}
		interface Columns {
			public static final int DATE = 0;
			public static final int WORK_HOURS = 1;
			public static final int BREAK = 2;
			public static final int REMARKS = 3;
			public static final int ENTER = 4;
			public static final int EXIT = 5;
			public static final int WORK_HOURS_FORMULA = 6;
		}
	}
	interface Formulas {
		Function<String, String> DATE_HOURS_FN = str -> String.format("HOUR(%1$s)+MINUTE(%1$s)/%2$s+SECOND(%1$s)/%3$s", str, TimeUnit.HOURS.toMinutes(1), TimeUnit.HOURS.toSeconds(1));
		Function<String, String> MINUTE_HOURS_FN = str -> String.format("%s/%s", str, TimeUnit.HOURS.toMinutes(1));
		BiFunction<String, String,String> DATE_DIFF_FN = (c1,c2) -> String.format("%s - %s", c1, c2);
	}
	
	@SuppressWarnings("unused")
	public void write(List<DayWorkData> dayWorkDatas, OutputStream outputStream) throws IOException {
		Workbook workbook = new HSSFWorkbook();

		DataFormat dataFormat = workbook.createDataFormat();
		
		CellStyle dateCellStyle = workbook.createCellStyle();
		dateCellStyle.setDataFormat(dataFormat.getFormat("dd"));
		CellStyle timeCellStyle = workbook.createCellStyle();
		timeCellStyle.setDataFormat(dataFormat.getFormat("HH:mm"));
		CellStyle floatNumberCellStyle = workbook.createCellStyle();
		floatNumberCellStyle.setDataFormat(dataFormat.getFormat("0.00"));
		CellStyle normalCellStyle = workbook.createCellStyle();
		
		short weekendColor = IndexedColors.AQUA.getIndex();
		short holidayColor = IndexedColors.ORANGE.getIndex();
		short vacationColor = IndexedColors.YELLOW.getIndex();
		CellStyle weekendDateCellStyle= createBGColorCellStyle(workbook.createCellStyle(), dateCellStyle, weekendColor);
		CellStyle weekendTimeCellStyle = createBGColorCellStyle(workbook.createCellStyle(), timeCellStyle, weekendColor);
		CellStyle weekendFloatNumberCellStyle = createBGColorCellStyle(workbook.createCellStyle(), floatNumberCellStyle, weekendColor);
		CellStyle weekendNormalCellStyle = createBGColorCellStyle(workbook.createCellStyle(), normalCellStyle, weekendColor);
		// TODO: holiday and vacation styles
		CellStyle holidayNormalCellStyle= createBGColorCellStyle(workbook.createCellStyle(), normalCellStyle, holidayColor);
		CellStyle vacationNormalCellStyle= createBGColorCellStyle(workbook.createCellStyle(), normalCellStyle, vacationColor);
		
		String sheetName = null;
		if (!dayWorkDatas.isEmpty()) {
			DayWorkData firstDayInfo = dayWorkDatas.get(0);
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
			sheetName = dateFormatter.format(firstDayInfo.getStartDatetime());
		}

		Sheet sheet = workbook.createSheet(sheetName);
		Row headerRow = sheet.createRow(0);
		createCell(headerRow, SheetCfg.Columns.DATE).setCellValue("Date");
		createCell(headerRow, SheetCfg.Columns.WORK_HOURS).setCellValue("Work (h)");
		createCell(headerRow, SheetCfg.Columns.BREAK).setCellValue("Break");
		createCell(headerRow, SheetCfg.Columns.REMARKS).setCellValue("Remarks");
		createCell(headerRow, SheetCfg.Columns.ENTER).setCellValue("Enter");
		createCell(headerRow, SheetCfg.Columns.EXIT).setCellValue("Exit");
		createCell(headerRow, SheetCfg.Columns.WORK_HOURS_FORMULA).setCellValue("Work (hf)");
		
		Predicate<DayWorkData> isWeekendDay = dayInfo -> dayInfo.getStartDatetime().getDayOfWeek().compareTo(DayOfWeek.SATURDAY) >= 0;
		Predicate<DayWorkData> isDayOff = dayWorkData -> dayWorkData.isDayOff();
		Predicate<DayWorkData> isHoliday = dayWorkData -> dayWorkData.isHoliday();
		Predicate<DayWorkData> isVacation = dayWorkData -> isHoliday.negate().and(isDayOff).test(dayWorkData);
		Predicate<DayWorkData> isWorkDay = dayWorkData -> isWeekendDay.negate().and(isDayOff.negate()).test(dayWorkData);
		Function<LocalDateTime, Date> toDate = dateTime -> dateTime != null ? dateConverter.fromLocalDateTime(dateTime) : null;
		
		for (int i = 0; i < dayWorkDatas.size(); i++) {
			DayWorkData dayWorkData = dayWorkDatas.get(i);
			Row row = sheet.createRow(i + 1);
			
			boolean isWeekend = isWeekendDay.test(dayWorkData);
			// TODO: holiday and vacation styles
			CellStyle currentNormalCellStyle = isWeekend ? weekendNormalCellStyle : normalCellStyle;
			CellStyle currentDateCellStyle = isWeekend ? weekendDateCellStyle : dateCellStyle;
			CellStyle currentTimeCellStyle = isWeekend ? weekendTimeCellStyle: timeCellStyle;
			CellStyle currentFloatNumberCellStyle = isWeekend ? weekendFloatNumberCellStyle : floatNumberCellStyle;
			
			Date date = dateConverter.fromLocalDateTime(dayWorkData.getStartDatetime().toLocalDate().atStartOfDay());
			createCell(row, SheetCfg.Columns.DATE, currentDateCellStyle).setCellValue(date);
			createCell(row, SheetCfg.Columns.WORK_HOURS, currentFloatNumberCellStyle).setCellValue(1f * dayWorkData.getWorkInMinutes() / TimeUnit.HOURS.toMinutes(1));
			createCell(row, SheetCfg.Columns.BREAK, currentNormalCellStyle).setCellValue(dayWorkData.getBreakInMinutes());
			createCell(row, SheetCfg.Columns.REMARKS, currentNormalCellStyle).setCellValue(MoreObjects.firstNonNull(dayWorkData.getRemarks(), ""));
			createCell(row, SheetCfg.Columns.ENTER, currentTimeCellStyle).setCellValue(toDate.apply(dayWorkData.getStartDatetime()));
			Cell exitCell = createCell(row, SheetCfg.Columns.EXIT, currentTimeCellStyle);
			Optional.ofNullable(dayWorkData.getExitDatetime())
				.ifPresent(d -> exitCell.setCellValue(toDate.apply(d)));
			
			String enterCellRef = getCellRef(row.getCell(SheetCfg.Columns.ENTER, Row.CREATE_NULL_AS_BLANK));
			String exitCellRef = getCellRef(row.getCell(SheetCfg.Columns.EXIT, Row.CREATE_NULL_AS_BLANK));
			String breakCellRef = getCellRef(row.getCell(SheetCfg.Columns.BREAK, Row.CREATE_NULL_AS_BLANK));
			String workTimeNoBreakFormula = Formulas.DATE_DIFF_FN.apply(exitCellRef, enterCellRef);
			String workHoursFormula = String.format("%s - %s", Formulas.DATE_HOURS_FN.apply(workTimeNoBreakFormula), Formulas.MINUTE_HOURS_FN.apply(breakCellRef));
			createCell(row, SheetCfg.Columns.WORK_HOURS_FORMULA, currentFloatNumberCellStyle, Cell.CELL_TYPE_FORMULA).setCellFormula(workHoursFormula);
		}
		
		int footerStartRowIndex = dayWorkDatas.size() + 1 + 2;
		int workPerHourColIndex = SheetCfg.Columns.WORK_HOURS;
		int breakPerHourColIndex = SheetCfg.Columns.BREAK;
		float sumWorkInHours = 1f * dayWorkDatas.stream().mapToLong(DayWorkData::getWorkInMinutes).sum() / TimeUnit.HOURS.toMinutes(1);
		int workDaysInMonth = dayWorkDatas.stream().collect(Collectors.summingInt(dayInfo -> isWorkDay.test(dayInfo) ? 1 : 0));
		float workHoursInMonth = workDaysInMonth * TimesheetConstants.WORK_TIME_HOURS;
		float missingWorkHours = workHoursInMonth - sumWorkInHours;
		float expectedbreakHoursInMonth = workDaysInMonth * 1;
		float totalBreakHoursInMonth = 1f * dayWorkDatas.stream().mapToLong(DayWorkData::getBreakInMinutes).sum() / TimeUnit.HOURS.toMinutes(1);
		float missingBreakHoursInMonth = expectedbreakHoursInMonth - totalBreakHoursInMonth;
		
		String firstWorkHoursCellRef = getCellRef(sheet.getRow(1 + 0).getCell(workPerHourColIndex));
		String lastWorkHoursCellRef = getCellRef(sheet.getRow(1 + dayWorkDatas.size() - 1).getCell(workPerHourColIndex));
		String firstBreakHoursCellRef = getCellRef(sheet.getRow(1 + 0).getCell(breakPerHourColIndex));
		String lastBreakHoursCellRef = getCellRef(sheet.getRow(1 + dayWorkDatas.size() - 1).getCell(breakPerHourColIndex));
		
		int currentFooterRow = footerStartRowIndex;
		
		Function<Integer,Cell> createSeparator = (rowNumber) -> { 
			Row row = sheet.createRow(rowNumber);
			Cell cell = createCell(row, workPerHourColIndex-1);
			cell.setCellValue(Strings.repeat("-", 8));
			return cell;
		};
		
		Row footerRow1 = sheet.createRow(currentFooterRow);
		createCell(footerRow1, workPerHourColIndex-1).setCellValue("DaysM");
		Cell workDaysInMonthCell = createCell(footerRow1, workPerHourColIndex, floatNumberCellStyle);
		workDaysInMonthCell.setCellValue(workDaysInMonth);
		String workDaysInMonthCellRef = getCellRef(workDaysInMonthCell);
		currentFooterRow++;

		Row footerRow2 = sheet.createRow(currentFooterRow);
		createCell(footerRow2, workPerHourColIndex-1).setCellValue("WorkM");
		Cell workInMonthByFormulaCell = createCell(footerRow2, workPerHourColIndex, floatNumberCellStyle, Cell.CELL_TYPE_FORMULA);
		workInMonthByFormulaCell.setCellFormula(String.format("%s*%s", TimesheetConstants.WORK_TIME_HOURS, workDaysInMonthCellRef));
		String workInMonthByFormulaCellRef = getCellRef(workInMonthByFormulaCell);
		currentFooterRow++;
		
		createSeparator.apply(currentFooterRow);
		currentFooterRow++;

		Row footerRow3 = sheet.createRow(currentFooterRow);
		createCell(footerRow3, workPerHourColIndex-1).setCellValue("Sum");
		Cell sumWorkInHoursCell = createCell(footerRow3, workPerHourColIndex, floatNumberCellStyle);
		sumWorkInHoursCell.setCellValue(sumWorkInHours);
		String sumWorkInHoursCellRef = getCellRef(sumWorkInHoursCell);
		currentFooterRow++;

		Row footerRow4 = sheet.createRow(currentFooterRow);
		createCell(footerRow4, workPerHourColIndex-1).setCellValue("Sum(f)");
		Cell sumWorkInHoursByFormulaCell = createCell(footerRow4, workPerHourColIndex, floatNumberCellStyle, Cell.CELL_TYPE_FORMULA);
		sumWorkInHoursByFormulaCell.setCellFormula(String.format("SUM(%s:%s)", firstWorkHoursCellRef, lastWorkHoursCellRef));
		String sumWorkInHoursByFormulaCellRef = getCellRef(sumWorkInHoursByFormulaCell);
		currentFooterRow++;
		
		Row footerRow5 = sheet.createRow(currentFooterRow);
		createCell(footerRow5, workPerHourColIndex-1).setCellValue("ToWork");
		createCell(footerRow5, workPerHourColIndex, floatNumberCellStyle).setCellValue(missingWorkHours);
		currentFooterRow++;

		Row footerRow6 = sheet.createRow(currentFooterRow);
		createCell(footerRow6, workPerHourColIndex-1).setCellValue("ToWork(f)");
		createCell(footerRow6, workPerHourColIndex, floatNumberCellStyle, Cell.CELL_TYPE_FORMULA)
			.setCellFormula(String.format("%s-%s", workInMonthByFormulaCellRef, sumWorkInHoursByFormulaCell));
		currentFooterRow++;
		
		createSeparator.apply(currentFooterRow);
		currentFooterRow++;
		
		Row footerRow7 = sheet.createRow(currentFooterRow);
		createCell(footerRow7, workPerHourColIndex-1).setCellValue("BreakExpected");
		Cell breakExpectedCell = createCell(footerRow7, workPerHourColIndex, floatNumberCellStyle, Cell.CELL_TYPE_FORMULA);
		breakExpectedCell.setCellFormula(String.format("%s*%s", TimesheetConstants.BREAK_TIME_HOURS, workDaysInMonthCellRef));
		String breakExpectedCellRef = getCellRef(breakExpectedCell);
		currentFooterRow++;

		Row footerRow8 = sheet.createRow(currentFooterRow);
		createCell(footerRow8, workPerHourColIndex-1).setCellValue("TotalBreak(f)");
		Cell totalBreakCell = createCell(footerRow8, workPerHourColIndex, floatNumberCellStyle, Cell.CELL_TYPE_FORMULA);
		totalBreakCell.setCellFormula(String.format("SUM(%s:%s)/60", firstBreakHoursCellRef, lastBreakHoursCellRef));
		String totalBreakCellRef = getCellRef(totalBreakCell);
		currentFooterRow++;

		Row footerRow9 = sheet.createRow(currentFooterRow);
		createCell(footerRow9, workPerHourColIndex-1).setCellValue("BreakMissing(f)");
		createCell(footerRow9, workPerHourColIndex, floatNumberCellStyle, Cell.CELL_TYPE_FORMULA)
			.setCellFormula(String.format("%s-%s", breakExpectedCellRef, totalBreakCellRef));
		currentFooterRow++;
		
		workbook.write(outputStream);
		workbook.close();
	}
	
	private Cell createCell(Row row, int column) {
		return createCell(row, column, null);
	}
	
	private CellStyle createBGColorCellStyle(CellStyle cellStyle, CellStyle fromCellStyle, Short bg) {
		cellStyle.cloneStyleFrom(fromCellStyle);
		setCellFill(cellStyle, bg);
		return cellStyle;
	}

	private void setCellFill(CellStyle cellStyle, Short bg) {
		if (bg!=null) {
			cellStyle.setFillForegroundColor(bg);
			cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		} else {
			cellStyle.setFillForegroundColor(IndexedColors.AUTOMATIC.getIndex());
			cellStyle.setFillPattern(CellStyle.NO_FILL);
		}
	}
	
	private Cell createCell(Row row, int column, CellStyle cellStyle) {
		return createCell(row, column, cellStyle, null);
	}
	
	private Cell createCell(Row row, int column, CellStyle cellStyle, Integer cellType) {
		Cell cell = row.createCell(column);
		if (cellStyle!=null) {
			cell.setCellStyle(cellStyle);
		}
		if (cellType!=null) {
			cell.setCellType(cellType);
		}
		return cell;
	}
	
//	private void createSeparatorCell(Row row, int column) {
//		createCell(row, column).setCellValue(Strings.repeat("-", 8));		
//	}
	
	private String getCellRef(Cell cell) {
		return getCellValueFn(() -> Optional.ofNullable(cell), c -> new CellReference(c).formatAsString());
	}
	
	@SuppressWarnings("unused")
	private <T> T getRowValueFn(Supplier<Optional<Row>> optRowFn, Function<Row, T> rowValueFn) {
		return optRowFn.get().map(rowValueFn).orElse(null);
	}
	
	private <T> T getCellValueFn(Supplier<Optional<Cell>> optCellFn, Function<Cell, T> cellValueFn) {
		return optCellFn.get().map(cellValueFn).orElse(null);
	}
	
}
