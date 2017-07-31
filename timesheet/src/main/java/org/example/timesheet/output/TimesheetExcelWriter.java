package org.example.timesheet.output;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
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
import org.example.timesheet.processing.DayInfo;
import org.example.timesheet.util.DateConverter;

import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;

@Named
public class TimesheetExcelWriter {
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
	public void write(List<DayInfo> dayInfos, OutputStream outputStream) throws IOException {
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
		CellStyle weekendDateCellStyle= createWeekendCellStyle(workbook.createCellStyle(), dateCellStyle, weekendColor);
		CellStyle weekendTimeCellStyle = createWeekendCellStyle(workbook.createCellStyle(), timeCellStyle, weekendColor);
		CellStyle weekendFloatNumberCellStyle = createWeekendCellStyle(workbook.createCellStyle(), floatNumberCellStyle, weekendColor);
		CellStyle weekendNormalCellStyle = createWeekendCellStyle(workbook.createCellStyle(), normalCellStyle, weekendColor);
		
		String sheetName = null;
		if (!dayInfos.isEmpty()) {
			DayInfo firstDayInfo = dayInfos.get(0);
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM");
			sheetName = dateFormatter.format(firstDayInfo.getStartDate());
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
		
		Predicate<DayInfo> isWeekendDay = dayInfo -> dateConverter.toLocalDateTime(dayInfo.getStartDate()).getDayOfWeek().compareTo(DayOfWeek.SATURDAY) >= 0;
		Predicate<DayInfo> isDayOff = dayInfo -> dayInfo.isDayOff();
		Predicate<DayInfo> isWorkDay = dayInfo -> isWeekendDay.negate().and(isDayOff.negate()).test(dayInfo);
		
		for (int i = 0; i < dayInfos.size(); i++) {
			DayInfo dayInfo = dayInfos.get(i);
			Row row = sheet.createRow(i + 1);
			
			boolean isWeekend = isWeekendDay.test(dayInfo);
			CellStyle currentNormalCellStyle = isWeekend ? weekendNormalCellStyle : normalCellStyle;
			CellStyle currentDateCellStyle = isWeekend ? weekendDateCellStyle : dateCellStyle;
			CellStyle currentTimeCellStyle = isWeekend ? weekendTimeCellStyle: timeCellStyle;
			CellStyle currentFloatNumberCellStyle = isWeekend ? weekendFloatNumberCellStyle : floatNumberCellStyle;
			
			Date date = dateConverter.fromLocalDateTime(dateConverter.toLocalDateTime(dayInfo.getStartDate()).toLocalDate().atStartOfDay());
			createCell(row, SheetCfg.Columns.DATE, currentDateCellStyle).setCellValue(date);
			createCell(row, SheetCfg.Columns.WORK_HOURS, currentFloatNumberCellStyle).setCellValue(1f * dayInfo.getWorkInMinutes() / TimeUnit.HOURS.toMinutes(1));
			createCell(row, SheetCfg.Columns.BREAK, currentNormalCellStyle).setCellValue(dayInfo.getBreakInMinutes());
			createCell(row, SheetCfg.Columns.REMARKS, currentNormalCellStyle).setCellValue(MoreObjects.firstNonNull(dayInfo.getRemarks(), ""));
			createCell(row, SheetCfg.Columns.ENTER, currentTimeCellStyle).setCellValue(dayInfo.getStartDate());
			Cell exitCell = createCell(row, SheetCfg.Columns.EXIT, currentTimeCellStyle);
			Optional.ofNullable(dayInfo.getExitDate())
				.ifPresent(d -> exitCell.setCellValue(d));
			
			String enterCellRef = getCellRef(row.getCell(SheetCfg.Columns.ENTER, Row.CREATE_NULL_AS_BLANK));
			String exitCellRef = getCellRef(row.getCell(SheetCfg.Columns.EXIT, Row.CREATE_NULL_AS_BLANK));
			String breakCellRef = getCellRef(row.getCell(SheetCfg.Columns.BREAK, Row.CREATE_NULL_AS_BLANK));
			String workTimeNoBreakFormula = Formulas.DATE_DIFF_FN.apply(exitCellRef, enterCellRef);
			String workHoursFormula = String.format("%s - %s", Formulas.DATE_HOURS_FN.apply(workTimeNoBreakFormula), Formulas.MINUTE_HOURS_FN.apply(breakCellRef));
			createCell(row, SheetCfg.Columns.WORK_HOURS_FORMULA, currentFloatNumberCellStyle, Cell.CELL_TYPE_FORMULA).setCellFormula(workHoursFormula);
		}
		
		int footerStartRowIndex = dayInfos.size() + 1 + 2;
		int workPerHourColIndex = SheetCfg.Columns.WORK_HOURS;
		float sumWorkInHours = 1f * dayInfos.stream().mapToLong(DayInfo::getWorkInMinutes).sum() / TimeUnit.HOURS.toMinutes(1);
		int workDaysInMonth = dayInfos.stream().collect(Collectors.summingInt(dayInfo -> isWorkDay.apply(dayInfo) ? 1 : 0));;
		float workHoursInMonth = workDaysInMonth * 8;
		float missingWorkHours = workHoursInMonth - sumWorkInHours;
		
		String firstWorkHoursCellRef = getCellRef(sheet.getRow(1 + 0).getCell(workPerHourColIndex));
		String lastWorkHoursCellRef = getCellRef(sheet.getRow(1 + dayInfos.size() - 1).getCell(workPerHourColIndex));
		
		Row footerRow1 = sheet.createRow(footerStartRowIndex + 0);
		createCell(footerRow1, workPerHourColIndex-1).setCellValue("Sum");
		Cell sumWorkInHoursCell = createCell(footerRow1, workPerHourColIndex, floatNumberCellStyle);
		sumWorkInHoursCell.setCellValue(sumWorkInHours);
		String sumWorkInHoursCellRef = getCellRef(sumWorkInHoursCell);

		Row footerRow2 = sheet.createRow(footerStartRowIndex + 1);
		createCell(footerRow2, workPerHourColIndex-1).setCellValue("Sum(f)");
		Cell sumWorkInHoursByFormulaCell = createCell(footerRow2, workPerHourColIndex, floatNumberCellStyle, Cell.CELL_TYPE_FORMULA);
		sumWorkInHoursByFormulaCell.setCellFormula(String.format("SUM(%s:%s)", firstWorkHoursCellRef, lastWorkHoursCellRef));
		String sumWorkInHoursByFormulaCellRef = getCellRef(sumWorkInHoursByFormulaCell);

		Row footerRow3 = sheet.createRow(footerStartRowIndex + 2);
		createCell(footerRow3, workPerHourColIndex-1).setCellValue("DaysM");
		Cell workDaysInMonthCell = createCell(footerRow3, workPerHourColIndex, floatNumberCellStyle);
		workDaysInMonthCell.setCellValue(workDaysInMonth);
		String workDaysInMonthCellRef = getCellRef(workDaysInMonthCell);
		
		Row footerRow4 = sheet.createRow(footerStartRowIndex + 3);
		createCell(footerRow4, workPerHourColIndex-1).setCellValue("WorkM");
		Cell workInMonthByFormulaCell = createCell(footerRow4, workPerHourColIndex, floatNumberCellStyle, Cell.CELL_TYPE_FORMULA);
		workInMonthByFormulaCell.setCellFormula("8 * " + workDaysInMonthCellRef);
		String workInMonthByFormulaCellRef = getCellRef(workInMonthByFormulaCell);
		
		Row footerRow5 = sheet.createRow(footerStartRowIndex + 4);
		createCell(footerRow5, workPerHourColIndex-1).setCellValue("ToWork");
		createCell(footerRow5, workPerHourColIndex, floatNumberCellStyle).setCellValue(missingWorkHours);

		Row footerRow6 = sheet.createRow(footerStartRowIndex + 5);
		createCell(footerRow6, workPerHourColIndex-1).setCellValue("ToWork(f)");
		createCell(footerRow6, workPerHourColIndex, floatNumberCellStyle, Cell.CELL_TYPE_FORMULA).setCellFormula(String.format("%s-%s", workInMonthByFormulaCellRef, sumWorkInHoursByFormulaCell));

		workbook.write(outputStream);
		workbook.close();
	}
	
	private Cell createCell(Row row, int column) {
		return createCell(row, column, null);
	}
	
	private CellStyle createWeekendCellStyle(CellStyle cellStyle, CellStyle fromCellStyle, Short bg) {
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
