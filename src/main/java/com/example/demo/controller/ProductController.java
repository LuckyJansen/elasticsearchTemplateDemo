package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.dao.ProductDao;
import com.example.demo.service.ProductService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ProductController {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private ProductService productService;


    @RequestMapping("/hello")
    public String hello(){
        return "hello,this is springboot.";
    }

    @RequestMapping("/deleteById")
    public void deleteById(Long id) {
        productService.deleteById(id);
    }

    @RequestMapping("/insertOne")
    public void insertOne() {
        Product product = new Product(15L,"小米手表15", "手表",
                "小米", 8499.00, "http://image.baidu.com/13123.jpg");
        productService.insertOne(product);
    }

    @RequestMapping("/insertAll")
    public void insertAll() {
        ArrayList<Product> list = new ArrayList<>();
        list.add(new Product(1L, "小米手机6", "手机", "小米", 1299.00, "http://image.baidu.com/13123.jpg"));
        list.add(new Product(2L, "小米手机7", "手机", "小米", 3299.00, "http://image.baidu.com/13123.jpg"));
        list.add(new Product(3L, "坚果手机R1", "手机", "锤子", 3699.00, "http://image.baidu.com/13123.jpg"));
        list.add(new Product(4L, "华为META10", "手机", "华为", 4199.00, "http://image.baidu.com/13123.jpg"));
        list.add(new Product(5L, "小米Mix2S", "手机", "小米", 4199.00, "http://image.baidu.com/13123.jpg"));
        list.add(new Product(6L, "小米手机7", "手机", "小米", 3219.00, "http://image.baidu.com/13123.jpg"));
        list.add(new Product(7L, "坚果手机R1", "手机", "锤子", 13699.00, "http://image.baidu.com/13123.jpg"));
        list.add(new Product(8L, "华为META10", "手机", "华为", 41499.00, "http://image.baidu.com/13123.jpg"));
        list.add(new Product(9L, "小米Mix2S", "手机", "小米", 41299.00, "http://image.baidu.com/13123.jpg"));
        list.add(new Product(10L, "荣耀V10", "手机", "华为", 12799.00, "http://image.baidu.com/13123.jpg"));
        list.add(new Product(11L, "荣耀V10", "手机", "华为", 1279.00, "http://image.baidu.com/13123.jpg"));
// 接收对象集合，实现批量新增
        productService.insertList(list);
    }

    @RequestMapping("/queryAll")
    public Object queryAll() {
        Object all = productService.queryAll();
        return all;
    }

    @RequestMapping("/queryAggregation")
    public Object queryAggregation() {
        Object all = productService.queryAggregation();
        return all;
    }

    @RequestMapping("/querySubAggregation")
    public Object querySubAggregation() {
        Object all = productService.querySubAggregation();
        return all;
    }

    @RequestMapping("/queryMultiColumns")
    public Object queryMultiColumns() {
        Object all = productService.queryMultiColumns();
        return all;
    }

    @RequestMapping("/queryMultiAggregation")
    public Object queryMultiAggregation() {
        Object all = productService.queryMultiAggregation();
        return all;
    }




    @RequestMapping("/queryById")
    public Object queryById(Long id) {
        Object all = productService.queryById(id);
        return all;
    }

    @RequestMapping("/queryByTerm")
    public Object queryByTerm() {
        //单字段严格匹配
        Object all = productService.queryByTerm("brand","小米");
        return all;
    }


    @RequestMapping("/queryMultiTerm")
    public Object queryMultiTerm() {
        //多字段严格匹配
        HashMap<String,String> condition = new HashMap<>();
        condition.put("brand","小米");
        condition.put("category","手机");

        Object all = productService.queryByMultiTerms(condition);
        return all;
    }

    @RequestMapping("/multiMatch")
    public Object multiMatch(String name) {
        //多个字段匹配某字符串示例
        Object all = productService.multiMatch(name);
        return all;
    }

    @RequestMapping("/rangeQuery")
    public Object rangeQuery(Double minPrice,Double maxPrice) {
        Object all = productService.rangeQueryByPrice(minPrice,maxPrice);
        return all;
    }

    @RequestMapping("/queryBool")
    public Object queryBool() {
        //布尔查询示例
        Object all = productService.queryBool();
        return all;
    }

    @RequestMapping("/queryPage")
    public Object queryPage(Integer page, Integer size) {
        // 分页查询
        Object items = productService.queryPage(page,size);
        return items;
    }

    @RequestMapping("/matchQueryByTitle")
    public Object matchQueryByTitle(String name) {
        // matchQuery 只要包含字符串就匹配出来
        Object items = productService.matchQueryByTitle(name);
        return items;
    }
}
