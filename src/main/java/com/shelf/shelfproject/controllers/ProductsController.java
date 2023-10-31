package com.shelf.shelfproject.controllers;

import com.shelf.shelfproject.entities.Product;
import com.shelf.shelfproject.services.ProductService;
import com.shelf.shelfproject.support.ResponseMessage;
import com.shelf.shelfproject.support.exceptions.BarCodeAlreadyExistException;
import com.shelf.shelfproject.support.exceptions.ValidationFailed;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/products")
public class ProductsController {
    @Autowired
    private ProductService productService;
    final int THRESHOLD = 5;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Object> create(@RequestBody @Valid Product product) {
        Product p;
        try {
            p = productService.addProduct(product);
        } catch (BarCodeAlreadyExistException e) {
            return new ResponseEntity<>(new ResponseMessage("BARCODE_ALREADY_EXIST"), HttpStatus.BAD_REQUEST);
        } catch (ValidationFailed e) {
            return new ResponseEntity<>(new ResponseMessage("VALIDATION_FAILED_PLEASE_CHECK_DATA"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_ALREADY_EXIST"), HttpStatus.OK);
        }
        return new ResponseEntity<>(p, HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/updateQnt")
    public ResponseEntity<Object> updateQuantity(@RequestParam(value = "idProd", required = true) int idProd,
                                           @RequestParam(value = "newQuantity", required = true) int quantity) {
        int i = 0;
        try {
            while( i<THRESHOLD){
                try{
                    productService.updateQuantity(idProd, quantity);
                    return new ResponseEntity<>(new ResponseMessage("UPDATE_SUCCESFULLY"), HttpStatus.OK);
                } catch(OptimisticLockException e){
                    i++;
                }
            }
        } catch (ValidationFailed e) {
            return new ResponseEntity<>(new ResponseMessage("VALIDATION_FAILED_CHECK_DATA"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("INNER_ERROR_TRY_LATER"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public List<Product> getAll() {
        return productService.showAllProducts();
        //restituisce tutti i prodotti in formato json
    }

    @GetMapping("/paged")
    public ResponseEntity<Object> getAll(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                 @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        List<Product> result = productService.showAllProducts(pageNumber, pageSize, sortBy);
        if ( result.size() <= 0 ) {
            return new ResponseEntity<>(new ResponseMessage("NO_RESULT"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping("/advanced_search/name")
    public ResponseEntity<Object> getByName(@RequestParam(required = true) String name) {
        List<Product> result = productService.showProductsByName(name);
        if ( result.size() <= 0 ) {
            return new ResponseEntity<>(new ResponseMessage("NO_RESULT"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/advanced_search/name&style")
    public ResponseEntity<Object> getByNameAndStyle(@RequestParam(required = true) String name, @RequestParam(required = true) String style) {
        List<Product> result = productService.showProductsByNameAndStyle(name, style);
        if ( result.size() <= 0 ) {
            return new ResponseEntity<>(new ResponseMessage("NO_RESULT"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/advanced_search/name&category")
    public ResponseEntity<Object> getByStyleAndCategory(@RequestParam(required = true) String style, @RequestParam(required = true) String category) {
        List<Product> result = productService.showProductsByStyleAndCategory(style, category);
        if ( result.size() <= 0 ) {
            return new ResponseEntity<>(new ResponseMessage("NO_RESULT"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
