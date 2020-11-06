package com.renjie;

import com.alibaba.fastjson.JSON;
import com.renjie.pojo.User;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class EsApiApplicationTests {

	@Autowired
	@Qualifier("restHighLevelClient")
	private RestHighLevelClient client;

	//测试索引的创建
	@Test
	void testCreateIndex() throws IOException {
		//创建索引请求
		CreateIndexRequest request = new CreateIndexRequest("renjie");
		//客户端执行请求 IndicesClient，请求获得响应
		CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
		System.out.println(createIndexResponse);
	}

	//测试获取索引 判断其是否存在
	@Test
	void testExistIndex() throws IOException {
		GetIndexRequest request = new GetIndexRequest("renjie");
		boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
		System.out.println(exists);
	}
	//测试删除索引
	@Test
	void  testDeleteIndex() throws IOException{
		DeleteIndexRequest request = new DeleteIndexRequest("demo");
		AcknowledgedResponse delete = client.indices().delete(request,RequestOptions.DEFAULT);
		System.out.println(delete.isAcknowledged());
	}


	//测试添加文档
	@Test
	void testAddDocument() throws IOException {
		//创建对象
		User user = new User("renjie",21);
		//创建请求
		IndexRequest renjie = new IndexRequest("renjie");
		//规则 put /renjie/_doc/1
		renjie.id("1");
		renjie.timeout(TimeValue.timeValueSeconds(1));
		renjie.timeout("1s");

		//将我们的数据放入请求
		renjie.source(JSON.toJSONString(user), XContentType.JSON);
		//客户端发送请求,获取响应的结果
		IndexResponse indexResponse = client.index(renjie, RequestOptions.DEFAULT);
		System.out.println(indexResponse.toString());
		System.out.println(indexResponse.status());

	}

	//测试获取文档
	@Test
	void testIsExists() throws IOException {
		GetRequest getRequest = new GetRequest("renjie","1");
		//不获取返回的 _source 的上下文
		getRequest.fetchSourceContext(new FetchSourceContext(false));
		getRequest.storedFields("_none_");

		boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
		System.out.println(exists);
	}

	//测试获取文档的信息
	@Test
	void testGetDocument() throws IOException {
		GetRequest getRequest = new GetRequest("renjie","1");
		GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
		String s = getResponse.getSourceAsString();//打印文档的内容
		System.out.println(s);
		System.out.println(getResponse);
	}

	//测试更新文档的信息
	@Test
	void testUpdateRequest() throws IOException {
		UpdateRequest updateRequest = new UpdateRequest("renjie","1");
		updateRequest.timeout("1s");
		//要更新进去的数据
		User user = new User("人介", 22);
		//放到doc中,XContentType.xxx表明传入的类型
		updateRequest.doc(JSON.toJSONString(user),XContentType.JSON);
		//执行更新
		UpdateResponse update = client.update(updateRequest, RequestOptions.DEFAULT);
		System.out.println(update.status());

	}
	//删除文档记录
	@Test
	void testDeleteRequest() throws IOException {
		DeleteRequest request = new DeleteRequest("renjie","1");
		request.timeout("1s");
		DeleteResponse delete = client.delete(request, RequestOptions.DEFAULT);
		System.out.println(delete.status());

	}

	//批量插入数据
	@Test
	void testBulkRequest() throws IOException {
		BulkRequest bulkRequest = new BulkRequest();
		bulkRequest.timeout("1s");

		ArrayList<User> userList = new ArrayList<>();
		userList.add(new User("renjie",21));
		userList.add(new User("老王",22));
		userList.add(new User("珂朵绿",23));
		userList.add(new User("张三",24));
		userList.add(new User("李四",25));
		userList.add(new User("王五",26));

		//批处理请求
		for (int i = 0; i < userList.size(); i++) {
			bulkRequest.add(new IndexRequest("renjie")
					.id(""+(i+1))
					.source(JSON.toJSONString(userList.get(i)),XContentType.JSON));
		}
		BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
		System.out.println(bulk.hasFailures());
	}

	//查询
	@Test
	void testSearch() throws IOException {
		SearchRequest searchRequest = new SearchRequest("renjie");
		//构建搜索文件
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		//查询条件，可以使用QueryBuilders工具类来实现
		MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", "王");
		sourceBuilder.query(matchQueryBuilder);
//		//分页
//		sourceBuilder.from(0);
//		sourceBuilder.size(1);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

		//放到请求中
		searchRequest.source(sourceBuilder);

		//执行请求
		SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
		String s = JSON.toJSONString(search.getHits());
		System.out.println(s);

		for (SearchHit documentFields : search.getHits().getHits()){
			System.out.println(documentFields.getSourceAsMap());
		}


	}






















}
