package com.example.springboot.controllers;

import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductsModel;
import com.example.springboot.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProductController {

    @Autowired
    ProductRepository productRepository;
    @PostMapping("/products")
    public ResponseEntity<ProductsModel> saveProducts(@RequestBody @Valid ProductRecordDto productRecordDto){
    var productModel = new ProductsModel();
    BeanUtils .copyProperties(productRecordDto, productModel);
    return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
}
    @GetMapping("/products")
    public ResponseEntity<List<ProductsModel>> getAllProducts(){
        List<ProductsModel> productsList = productRepository.findAll();
        if(!productsList.isEmpty()){
            for(ProductsModel product : productsList){
                UUID id = product.getId();
                product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
            }
        }
    return ResponseEntity.status(HttpStatus.OK).body(productsList);
}
    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getOneProduct(@PathVariable(value="id") UUID id){
    Optional<ProductsModel> productO = productRepository.findById(id);
    if(productO.isEmpty()){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("product not found.");
    }
        productO.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel());
    return ResponseEntity.status(HttpStatus.OK).body(productO.get());
}

@PutMapping("/products/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value="id")UUID id,
                                                @RequestBody  @Valid ProductRecordDto productRecordDto){
     Optional<ProductsModel> productO = productRepository.findById(id);
     if(productO.isEmpty()){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not faund.");
    }
     var productModel = productO.get();
     BeanUtils.copyProperties(productRecordDto, productModel);
     return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
}
@DeleteMapping("/products/{id}")
    public ResponseEntity<Object> deletProduct(@PathVariable(value="id") UUID id){
        Optional<ProductsModel> productO = productRepository.findById(id);
        if(productO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not faund.");
        }
        productRepository.delete(productO.get());
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully.");
}

}
