package com.example.geoshare;

public class PurchaseOption {
    private String id;
    private String name;
    private String price;

    public PurchaseOption(String id, String name, String price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    // Optionally, override equals(), hashCode(), and toString() if needed.
}
