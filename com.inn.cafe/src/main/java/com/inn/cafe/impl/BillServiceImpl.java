package com.inn.cafe.impl;

import com.inn.cafe.Exceptions.InvalidInputDataException;
import com.inn.cafe.Exceptions.NotFoundException;
import com.inn.cafe.entities.Bill;
import com.inn.cafe.jwt.JwtRequestFilter;
import com.inn.cafe.repository.BillRepository;
import com.inn.cafe.services.BillService;
import com.inn.cafe.utils.CafeUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Service
public class BillServiceImpl implements BillService {
    @Autowired
    JwtRequestFilter jwtRequestFilter;

    @Autowired
    BillRepository billRepository;

    String documentPathLocation = "/Users/omon/Documents/cafe_management";

    @Override
    public String generateReport(Map<String, Object> requestMap) throws InvalidInputDataException, FileNotFoundException, DocumentException, JSONException {
        String fileName;
        if (!requestMap.isEmpty()) {
            if ((requestMap.containsKey("createPdf")) && !requestMap.get("createPdf").equals("true")) {
                fileName = requestMap.get("uuid").toString();
            } else {
                fileName = CafeUtils.getUUID();
                requestMap.put("uuid", fileName);
                saveBill(requestMap);
            }

            StringBuilder data = new StringBuilder();
            data.append("Name: ");
            data.append(requestMap.get("name"));
            data.append("\n");
            data.append("Contact Number: ");
            data.append(requestMap.get("phone"));
            data.append("\n");
            data.append("Email: ");
            data.append(requestMap.get("email"));
            data.append("\n");
            data.append("Payment Method: ");
            data.append(requestMap.get("paymentMethod"));

            createPdfDocuement(fileName, data, requestMap);

            return fileName;
        }
        throw new InvalidInputDataException("Bad input data");
    }

    @Override
    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        if (jwtRequestFilter.isAdmin()) {
            log.info("Fetching all bills from database");
            bills = billRepository.getAllBills();
        } else {
            bills = billRepository.getBillByUsername(jwtRequestFilter.getCurrentUser());
        }
        return bills;
    }

    @Override
    public byte[] getPdf(Map<String, Object> requestMap) throws IOException, DocumentException, JSONException {
        log.info("Getting bill pdf document");
        byte[] bill = new byte[0];
        try {
            if (!requestMap.containsKey("uuid") && requestMap.isEmpty()) {
                throw new InvalidInputDataException("Invalid data");
            } else {
                String uuid = requestMap.get("uuid").toString();
                String filePath = documentPathLocation + "//" + uuid + ".pdf";

                if (CafeUtils.isFilePresent(filePath)) {
                    log.info("Getting already existing pdf");
                    bill = getByteArray(filePath);
                } else {
                    log.info("Generating a new pdf");
                    requestMap.put("createPdf", false);
                    generateReport(requestMap);
                    bill = getByteArray(filePath);
                }
            }
        } catch (IOException e) {
            log.error("Error returning file byte array");
        }

        return bill;
    }

    @Override
    public void deleteBill(int id) throws NotFoundException {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found"));
        billRepository.delete(bill);
        log.info("Document has been deleted");
    }

    private byte[] getByteArray(String filePath) throws IOException {
        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(initialFile);
        byte[] fileByteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return fileByteArray;
    }

    private void saveBill(Map<String, Object> requestMap) {
        try {
            log.info("Saving bill to the database");
            Bill bill = Bill.builder()
                    .uuid((String) requestMap.get("uuid"))
                    .name((String) requestMap.get("name"))
                    .email((String) requestMap.get("email"))
                    .phone((String) requestMap.get("phone"))
                    .paymentMethod((String) requestMap.get("paymentMethod"))
                    .totalAmount(Integer.parseInt(requestMap.get("totalAmount").toString()))
                    .productDetails((String) requestMap.get("productDetails"))
                    .createdBy(jwtRequestFilter.getCurrentUser())
                    .build();

            billRepository.save(bill);
        } catch (Exception e) {
            log.error("Error saving bill to database: " + e.getMessage());
        }
    }

    private Document createPdfDocuement(String fileName, StringBuilder data, Map<String, Object> requestMap) throws FileNotFoundException, DocumentException, JSONException {
        Document document = new Document();
        try {
            log.info("Creating pdf document");

            FileOutputStream fileOutputStream = new FileOutputStream(documentPathLocation + "/" + fileName + ".pdf");
            PdfWriter.getInstance(document, fileOutputStream);

            document.open();
            setRectangleInPdf(document); // Add the table which will hold the bill information

            Paragraph heading = new Paragraph("Cafe Management System", getFont("Header")); // create the paragraph
            heading.setAlignment(Element.ALIGN_CENTER);
            document.add(heading);

            Paragraph body = new Paragraph(data + "\n \n", getFont("Body")); // create the body
            document.add(body);

            PdfPTable table = new PdfPTable(5); // create the table
            table.setWidthPercentage(100);
            addTableHeader(table);

            JSONArray jsonArray = CafeUtils.getJsonArrayFromString((String) requestMap.get("productDetails"));
            for (int i = 0; i < jsonArray.length(); i++) {
                addRows(table, CafeUtils.getMapFromJson(jsonArray.getString(i)));
            }

            document.add(table); // add table to document

            Paragraph footer = new Paragraph("Total: " + requestMap.get("totalAmount") + "\n" + "Thank you for visiting. Please visit again!", getFont("Body"));
            document.add(footer);
            document.close();
            log.info("finished creating document");
            return document;
        } catch (Exception e) {
            log.error("Error while creating pdf document: " + e.getMessage());
            return document;
        }
    }

    private void addRows(PdfPTable table, Map<String, Object> data) {
        table.addCell(data.get("name").toString());
        table.addCell(data.get("category").toString());
        table.addCell(data.get("quantity").toString());
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
    }

    private Font getFont(String type) {
        switch (type) {
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Body":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            default:
                return new Font();
        }
    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        Rectangle rectangle = new Rectangle(577, 825, 18, 15);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBackgroundColor(BaseColor.WHITE);
        rectangle.setBorderWidth(1);

        document.add(rectangle);
    }
}
