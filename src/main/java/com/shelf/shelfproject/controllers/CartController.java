package com.shelf.shelfproject.controllers;

import com.shelf.shelfproject.services.CartService;
import com.shelf.shelfproject.support.ResponseMessage;
import com.shelf.shelfproject.support.exceptions.NotAvailableQuantityException;
import com.shelf.shelfproject.support.exceptions.ProductInPurchaseNotFoundException;
import com.shelf.shelfproject.support.exceptions.ProductNotFoundException;
import com.shelf.shelfproject.support.exceptions.UserNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;
    final int MAX_TENTATIVE = 5;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/get")
    public ResponseEntity<Object> getCart(){
        int i = 0;
        try{
            while(i<MAX_TENTATIVE){
                try{
                    return new ResponseEntity<>(cartService.getCart(), HttpStatus.OK);
                } catch(OptimisticLockException e){
                    i++;
                }
            }
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USER_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("INNER_ERROR_TRY_LATER"), HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/addProd")
    public ResponseEntity<Object> addProductInCart(@RequestParam(value = "idProd", required = true) int idProd){
        int i = 0;
        try{
            while( i<MAX_TENTATIVE ){
                try{
                    cartService.addProduct(idProd);
                    return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
                } catch(OptimisticLockException e){
                    i++;
                }
            }
        } catch (ProductNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USER_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        } catch (NotAvailableQuantityException e){
            return new ResponseEntity<>(new ResponseMessage("NOT_AVAILABLE_QUANTITY"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("INNER_ERROR_TRY_LATER"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/removeProduct")
    public ResponseEntity<Object> removeProductInCart(@RequestParam(value = "idProd", required = true) int idProd){
        try{
            cartService.removeProduct(idProd);
        } catch (ProductInPurchaseNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_IN_PURCHASE_NOT_EXIST_IN_CART"),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/removeAllProduct")
    public ResponseEntity<Object> removeAllProductInCart(){
        try{
            cartService.removeAllProduct();
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/plusOne")
    public ResponseEntity<Object> plusQntProductInCart(@RequestParam(value = "idProd", required = true) int idProd){
        int i = 0;
        try{
            while(i < MAX_TENTATIVE){
                try{
                    cartService.plusOne(idProd);
                    return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
                } catch(OptimisticLockException e){
                    i++;
                }
            }
        } catch (ProductInPurchaseNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_IN_PURCHASE_NOT_EXIST_IN_CART"),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("INNER_ERROR_TRY_LATER"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/minusOne")
    public ResponseEntity<Object> minusQntProductInCart(@RequestParam(value = "idProd", required = true) int idProd){
        int i = 0;
        try{
            while(i < MAX_TENTATIVE){
                try{
                    cartService.minusOne(idProd);
                    return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
                } catch(OptimisticLockException e){
                    i++;
                }
            }
        } catch (ProductInPurchaseNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_IN_PURCHASE_NOT_EXIST_IN_CART"),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("INNER_ERROR_TRY_LATER"), HttpStatus.OK);
    }

}
