package com.example.debtbook_backend.service;

import com.example.debtbook_backend.entity.BotUser;
import com.example.debtbook_backend.projection.DebtorProjection;
import com.example.debtbook_backend.projection.UserProjection;
import com.example.debtbook_backend.repository.DebtorRepository;
import com.example.debtbook_backend.repository.UserRepository;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

@Service
public class ExcelFileCreator {

    private final DebtorRepository debtorRepository;
    private final UserRepository userRepository;


    private Workbook workbook = new XSSFWorkbook();

    private String[] columnsUz = {"Ismi", "Tel", "Qarzi"};

    private String[] storeHeader = {"Owner", "Phone number", "Debt","store name"};

    @Autowired
    public ExcelFileCreator(DebtorRepository debtorRepository, UserRepository userRepository) {
        this.debtorRepository = debtorRepository;
        this.userRepository = userRepository;
    }


    @SneakyThrows
    public ByteArrayInputStream createDebtorsExcelFile(List<DebtorProjection> debtorList) {
        UUID uuid = UUID.randomUUID();
        Sheet sheet = workbook.createSheet(String.valueOf(uuid));

        for (int i = 0; i < debtorList.size(); i++) {
            Row row = sheet.createRow(0);
            Row row1 = sheet.createRow(i + 1);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setColor(IndexedColors.RED.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            DebtorProjection debtor = debtorList.get(i);

            for (int i1 = 0; i1 < columnsUz.length; i1++) {
                Cell cell = row.createCell(i1);
                cell.setCellValue(columnsUz[i1]);
                cell.setCellStyle(headerCellStyle);
            }

            Cell cell = row1.createCell(0);
            cell.setCellValue(debtor.getFullName());

            Cell cell1 = row1.createCell(1);
            cell1.setCellValue(debtor.getPhoneNumber());

            Cell cell2 = row1.createCell(2);
            cell2.setCellValue(debtor.getDebt());


            for (int j = 0; j < columnsUz.length; j++) {
                sheet.autoSizeColumn(j);
            }

        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        byte[] bytes = outputStream.toByteArray();
        return new ByteArrayInputStream(bytes);

    }



    @SneakyThrows
    public ByteArrayInputStream createStoresExcelFile(List<UserProjection> storesList) {
        UUID uuid = UUID.randomUUID();
        Sheet sheet = workbook.createSheet(String.valueOf(uuid));

        for (int i = 0; i < storesList.size(); i++) {
            Row row = sheet.createRow(0);
            Row row1 = sheet.createRow(i + 1);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setColor(IndexedColors.RED.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            UserProjection userStore = storesList.get(i);
            for (int i1 = 0; i1 < storeHeader.length; i1++) {
                Cell cell = row.createCell(i1);
                cell.setCellValue(storeHeader[i1]);
                cell.setCellStyle(headerCellStyle);
            }

            Cell cell = row1.createCell(0);
            cell.setCellValue(userStore.getFullName());

            Cell cell1 = row1.createCell(1);
            cell1.setCellValue(userStore.getPhoneNumber());

            Cell cell2 = row1.createCell(2);
            cell2.setCellValue(userStore.getDebt());

            Cell cell3 = row1.createCell(3);
            cell2.setCellValue(userStore.getStoreName());


            for (int j = 0; j < columnsUz.length; j++) {
                sheet.autoSizeColumn(j);
            }

        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        byte[] bytes = outputStream.toByteArray();
        return new ByteArrayInputStream(bytes);
    }


}
