package org.example.smartecommercesystem.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class ReviewNoSQLDAO {

    private final MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private final MongoDatabase db = mongoClient.getDatabase("e_commerce");
    private final MongoCollection<Document> reviews = db.getCollection("reviews");


    public void addReview(Integer sqlId, int productId, int userId, int rating, String comment) {
        Document doc = new Document()
                .append("sqlId", sqlId)
                .append("productId", productId)
                .append("userId", userId)
                .append("rating", rating)
                .append("comment", comment)
                .append("createdAt", System.currentTimeMillis());
        reviews.insertOne(doc);
    }


    public List<Document> getAllReviews() {
        List<Document> list = new ArrayList<>();
        for (Document doc : reviews.find()) list.add(doc);
        return list;
    }


    public void deleteReview(String id) {
        reviews.deleteOne(new Document("_id", new ObjectId(id)));
    }


    public void updateReview(String id, int rating, String comment) {
        reviews.updateOne(new Document("_id", new ObjectId(id)),
                new Document("$set", new Document("rating", rating).append("comment", comment)));
    }
}
