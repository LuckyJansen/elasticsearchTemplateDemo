package com.example.demo.service.impl;

import com.example.demo.dao.ProductDao;
import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDao productDao;




    public void insertOne(Product product) {
        productDao.save(product);
    }


    public void insertList(ArrayList<Product> list) {
        // 接收对象集合，实现批量新增
        productDao.saveAll(list);
    }

    public void deleteById(Long id) {
        productDao.deleteById(id);
    }


    public Object queryById(Long id) {
        Object all = productDao.findById(id);
        return all;
    }

    public Object queryAll() {
        Object all = productDao.findAll();
        return all;
    }

    //单字段匹配
    public Object queryByTerm(String key,String value) {
        //字段严格匹配
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //queryBuilder.withQuery(QueryBuilders.termQuery("price",4199));
        queryBuilder.withQuery(QueryBuilders.termQuery(key,value));
        Object all = productDao.search(queryBuilder.build());
        return all;
    }

    public Object queryAggregation(){
        //字段严格匹配
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        queryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brand"));

        // 2、查询,需要把结果强转为AggregatedPage类型
        AggregatedPage<Product> aggPage = (AggregatedPage<Product>) this.productDao.search(queryBuilder.build());
// 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3、遍历
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称
            System.out.println(bucket.getKeyAsString());
            // 3.5、获取桶中的文档数量
            System.out.println(bucket.getDocCount());
        }



        return buckets;
//
//        response = responsebuilder.setQuery(QueryBuilders.boolQuery()
//
//                .must(QueryBuilders.matchPhraseQuery("name", "中学历史")))
//                .addSort("category_id", SortOrder.ASC)
//                .addAggregation(aggregation)// .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//                .setExplain(true).execute().actionGet();



    }

    public Object querySubAggregation(){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        queryBuilder.addAggregation(
                AggregationBuilders.terms("brands").field("brand")
                        .subAggregation(AggregationBuilders.avg("priceAvg").field("price")) // 在品牌聚合桶内进行嵌套聚合，求平均值
        );
        // 2、查询,需要把结果强转为AggregatedPage类型
        AggregatedPage<Product> aggPage = (AggregatedPage<Product>) this.productDao.search(queryBuilder.build());

        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3、遍历
        List<Map<String,String>> listMap = new ArrayList<>();
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称  3.5、获取桶中的文档数量
            System.out.println(bucket.getKeyAsString() + "，共" + bucket.getDocCount() + "台");
            // 3.6.获取子聚合结果：
            InternalAvg avg = (InternalAvg) bucket.getAggregations().asMap().get("priceAvg");
            System.out.println("平均售价：" + avg.getValue());
            Map<String,String> map = new HashMap<>();
            map.put("品牌",bucket.getKeyAsString());
            Long tai = bucket.getDocCount();
            map.put("台数",tai.toString());
            double value = avg.getValue();
            map.put("均价",Double.toString(value));
            listMap.add(map);
        }
        return listMap;

    }

    public Object queryMultiAggregation(){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        queryBuilder.addAggregation(
                AggregationBuilders.terms("brands").field("brand")
                        .subAggregation(AggregationBuilders.terms("categorys").field("category")) // 在品牌聚合桶内进行嵌套聚合，求平均值
        );
        // 2、查询,需要把结果强转为AggregatedPage类型
        AggregatedPage<Product> aggPage = (AggregatedPage<Product>) this.productDao.search(queryBuilder.build());

        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms brandTerm = (StringTerms) aggPage.getAggregation("brands");
        // 3.2、获取桶
        List<StringTerms.Bucket> brandBuckets = brandTerm.getBuckets();
        // 3.3、遍历
        List<String> listString = new ArrayList<>();
        for (StringTerms.Bucket brandBucket : brandBuckets) {
            StringTerms categorysTerm = (StringTerms) brandBucket.getAggregations().asMap().get("categorys");
            List<StringTerms.Bucket> categorysBuckets = categorysTerm.getBuckets();
            for(StringTerms.Bucket categorysBucket :categorysBuckets){
                String result = brandBucket.getKey() + "品牌，" +categorysBucket.getKey() + "类型：" + categorysBucket.getDocCount() +"个商品。";
                listString.add(result);
                System.out.println(result);
            }
        }
        return listString;

    }

//    public Object queryMultiColumns(){
//        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//        // 不查询任何结果
//        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
//        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
//        queryBuilder.addAggregation(
//                AggregationBuilders.terms("brands").field("brand")
//                        .subAggregation(AggregationBuilders.terms("categorys").field("category")) // 在品牌聚合桶内进行嵌套聚合，求平均值
//        );
//        // 2、查询,需要把结果强转为AggregatedPage类型
//        AggregatedPage<Product> aggPage = (AggregatedPage<Product>) this.productDao.search(queryBuilder.build());
//
//        // 3、解析
//        // 3.1、从结果中取出名为brands的那个聚合，
//        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
//
//        StringTerms brandTerm = (StringTerms) aggPage.getAggregation("brands");
//        StringTerms categoryTerm = (StringTerms) aggPage.getAggregation("categorys");
//        // 3.2、获取桶
//        List<StringTerms.Bucket> brandBuckets = brandTerm.getBuckets();
//        List<StringTerms.Bucket> categoryBuckets = categoryTerm.getBuckets();
//        // 3.3、遍历
//
//        Map<String,List<String>> maps= new HashMap<>();
//        List<String> name = new ArrayList<>();
//        for (StringTerms.Bucket brandBucket : brandBuckets) {
//            name.add(brandBucket.getKey().toString());
//        }
//        maps.put("品牌",name);
//
//        Map<String,List<String>> mapsc= new HashMap<>();
//        List<String> namec = new ArrayList<>();
//        for (StringTerms.Bucket categoryBucket : categoryBuckets) {
//            namec.add(categoryBucket.getKey().toString());
//        }
//        maps.put("类型",namec);
//
//        return maps;
//
//    }

    //多字段匹配
    public Object queryByMultiTerms(HashMap<String, String> multiTermsMap) {
        //动态多个字段严格匹配
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        Iterator iter = multiTermsMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry =  (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            boolQueryBuilder.must(QueryBuilders.termQuery(key,val));
        }
        queryBuilder.withQuery(boolQueryBuilder);
        Object all = productDao.search(queryBuilder.build());
        return all;
    }

    public Object matchQueryByTitle(String name) {
        // matchQuery 只要包含字符串就匹配出来
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.matchQuery("title",name));
        Object items = productDao.search(queryBuilder.build());
        return items;
    }

    public Object rangeQueryByPrice(Double minPrice,Double maxPrice) {
        //rangeQuery值范围查询，from,to包含当前值
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.rangeQuery("price").from(minPrice).to(maxPrice));
        Object all = productDao.search(queryBuilder.build());
        return all;
    }

    public Object queryPage(Integer page, Integer size) {
        // 分页查询
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withPageable(PageRequest.of(page,size));
        Object items = productDao.search(queryBuilder.build());
        return items;
    }

    public Object multiMatch(String name) {
        //多个字段匹配某字符串
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.multiMatchQuery(name,"title","category"));
        Object all = productDao.search(queryBuilder.build());
        return all;
    }

    public Object queryBool() {
        /*
must代表返回的文档必须满足must子句的条件，会参与计算分值；
filter代表返回的文档必须满足filter子句的条件，但不会参与计算分值；
should代表返回的文档可能满足should子句的条件，也可能不满足，有多个should时满足任何一个就可以，通过minimum_should_match设置至少满足几个。
mustnot代表必须不满足子句的条件。
         */
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.boolQuery()
                .filter(QueryBuilders.rangeQuery("price").gt(1299))
                .must(
                        QueryBuilders.boolQuery()
                                .should(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("category","手表")).must(QueryBuilders.termQuery("brand","小米")).must(QueryBuilders.termQuery("price",8499)))
                                .should(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("category","手机")).must(QueryBuilders.termQuery("brand","小米")).must(QueryBuilders.termQuery("price",41299)))
                                .should(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("category","手机")).must(QueryBuilders.termQuery("brand","小米")).must(QueryBuilders.termQuery("price",1299)))
                )
        ).withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        // 排序
        //	    queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.ASC));
        Object all = productDao.search(queryBuilder.build());
        return all;
    }
}
