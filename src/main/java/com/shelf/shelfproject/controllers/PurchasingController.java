package com.shelf.shelfproject.controllers;

import com.shelf.shelfproject.entities.Purchase;
import com.shelf.shelfproject.services.PurchasingService;
import com.shelf.shelfproject.support.ResponseMessage;
import com.shelf.shelfproject.support.exceptions.*;
import com.shelf.shelfproject.support.purchasing.PurchaseDetailsDTO;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/purchase")
public class PurchasingController {
    @Autowired
    private PurchasingService purchasingService;
    final int MAX_TENTATIVE = 5;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<Object> createPurchase(@RequestBody List<PurchaseDetailsDTO> pipDetailsList) {
        Purchase purchase;
        int i = 0;
        try {
            while( i<MAX_TENTATIVE ){
                try{
                    purchase = purchasingService.buy(pipDetailsList);
                    return new ResponseEntity<>(purchase, HttpStatus.OK); //Ritorno al client un resoconto dell'acquisto
                } catch(OptimisticLockException e){
                    i++;
                }
            }
        } catch (NotAvailableQuantityException e) {
            return new ResponseEntity<>(new ResponseMessage("PRODUCT" + e.getName() + "_QUANTITY_UNAVAILABLE"), HttpStatus.BAD_REQUEST);
        } catch (ProductNotFoundException | InconsistentCartException e){
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USER_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        } catch (PriceChangedException e){
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_" + e.getName() + "_PRICE_UNAVAILABLE"),HttpStatus.BAD_REQUEST);
        } catch (ConstraintViolationException e){
            return new ResponseEntity<>(new ResponseMessage("INTERNAL_ERROR_TRY_LATER"),HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage("INNER_ERROR_TRY_LATER"), HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/purchases")
    public ResponseEntity<Object> getPurchases(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                       @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                       @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        try {
            List<Purchase> result = purchasingService.getPurchasesByUser(pageNumber,pageSize,sortBy);
            if ( result.isEmpty() ) {
                return new ResponseEntity<>(new ResponseMessage("NO_RESULT"), HttpStatus.OK);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(new ResponseMessage("USER_NOT_FOUND"), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/purchases/date")
    public ResponseEntity getPurchasesInPeriod(@RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy")  Date start,
                                               @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date end) {
        try {
            List<Purchase> result = purchasingService.getPurchasesByUserInPeriod(start, end);
            if ( result.isEmpty() ) {
                return new ResponseEntity<>(new ResponseMessage("NO_RESULT"), HttpStatus.OK);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "USER_NOT_FOUND", e); //catturiamo le specifiche eccezioni -> la buona prassi infatti ci dice di creare e sollevare nei service le specifiche eccezioni
        } catch (WrongTimelineException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "START_DATE_MUST_BE_PREVIUS_END_DATE", e);
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllPurchases() {
        //admin dashboard
        try {
            return new ResponseEntity<>(purchasingService.getAllPurchases(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseMessage("INTERNAL_ERROR"), HttpStatus.BAD_REQUEST);
        }
    }


}
