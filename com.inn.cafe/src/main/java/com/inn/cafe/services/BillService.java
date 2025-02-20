package com.inn.cafe.services;

import com.inn.cafe.Exceptions.NotFoundException;
import com.inn.cafe.entities.Bill;
import com.itextpdf.text.DocumentException;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface BillService {
    String generateReport(Map<String, Object> requestMap) throws NotFoundException, FileNotFoundException, DocumentException, JSONException;

    List<Bill> getAllBills();

    byte[] getPdf(Map<String, Object> requestMap) throws IOException, DocumentException, JSONException;

    void deleteBill(int id) throws NotFoundException;
}
