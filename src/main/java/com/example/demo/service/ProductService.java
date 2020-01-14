package com.example.demo.service;

import com.example.demo.entity.Product;

import java.util.ArrayList;
import java.util.HashMap;

public interface ProductService {

    void insertOne(Product product);

    void insertList(ArrayList<Product> list);

    void deleteById(Long id);

    Object queryById(Long id);

    Object queryAll();

    Object queryByTerm(String key,String value);

    Object queryAggregation();

    Object querySubAggregation();

    Object queryMultiAggregation();

//    Object queryMultiColumns(); 想得出这样的结果{"品牌":[小米，华为，苹果],"类型":[手机，手表]}

    Object queryByMultiTerms(HashMap<String,String> multiTermsMap);

    Object matchQueryByTitle(String name);

    Object rangeQueryByPrice(Double minPrice,Double maxPrice);

    Object queryPage(Integer page, Integer size);

    Object multiMatch(String name);

    Object queryBool();
}
