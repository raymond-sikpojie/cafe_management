package com.inn.cafe.controller;

import com.inn.cafe.Exceptions.InvalidInputDataException;
import com.inn.cafe.Exceptions.NotAuthorizedException;
import com.inn.cafe.Exceptions.NotFoundException;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.entities.Bill;
import com.inn.cafe.services.BillService;
import com.inn.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/bill")
public class BillController {

    @Autowired
    BillService billService;

    @PostMapping("/generateReport")
    ResponseEntity<String> generateReport(@RequestBody Map<String, Object> requestMap) {
        try {
            String fileName = billService.generateReport(requestMap);
            return CafeUtils.getResponseEntity(" \"UUID\": \"" + fileName + " ", HttpStatus.OK);
        } catch (InvalidInputDataException e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity("Invalid data provided", HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException e) {
            return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("An exception has occurred: " + e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getBills")
    ResponseEntity<List<Bill>> getAllProducts() {
        try {
            List<Bill> bills = billService.getAllBills();
            return new ResponseEntity<>(bills, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getPdf")
    ResponseEntity<byte[]> getPdf(@RequestBody Map<String, Object> requestMap) {
        byte[] bill = new byte[0];
        try {
            bill = billService.getPdf(requestMap);
            return new ResponseEntity<>(bill, HttpStatus.OK);
        } catch (InvalidInputDataException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(bill, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            log.error("An error occurred while getting bill: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(bill, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id) {
        try {
            billService.deleteBill(id);
            return CafeUtils.getResponseEntity("Document has been deleted", HttpStatus.OK);
        } catch (NotFoundException e) {
            return CafeUtils.getResponseEntity(CafeConstants.DOCUMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
