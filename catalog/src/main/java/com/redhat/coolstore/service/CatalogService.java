package com.redhat.coolstore.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.redhat.coolstore.client.InventoryClient;
import com.redhat.coolstore.model.Product;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private InventoryClient inventoryClient;

    public Product read(String id) {
        Product product = repository.findById(id);
        JSONArray jsonArray = new JSONArray(inventoryClient.getInventoryStatus(product.getItemId()));
        List<String> quantity = IntStream.range(0, jsonArray.length())
            .mapToObj(index -> ((JSONObject)jsonArray.get(index))
            .optString("quantity")).collect(Collectors.toList());
        product.setQuantity(Integer.parseInt(quantity.get(0)));
        return product;
    }

    public List<Product> readAll() {
        List<Product> productList = repository.readAll();
                productList.forEach(p -> {
        JSONArray jsonArray = new JSONArray(this.inventoryClient.getInventoryStatus(p.getItemId()));
        List<String> quantity = IntStream.range(0, jsonArray.length())
            .mapToObj(index -> ((JSONObject)jsonArray.get(index))
            .optString("quantity")).collect(Collectors.toList());
          p.setQuantity(Integer.parseInt(quantity.get(0)));
        });
        return productList;
    }

}