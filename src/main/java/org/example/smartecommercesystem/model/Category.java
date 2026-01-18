package org.example.smartecommercesystem.model;
public class Category {
    private int categoryId;
    private String categoryName;

    public Category() {}
    public Category(int categoryId, String name) {
        this.categoryId = categoryId;
        this.categoryName = name;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    @Override
    public String toString() {
        return categoryName;
    }
}