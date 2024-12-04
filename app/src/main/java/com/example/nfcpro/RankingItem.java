package com.example.nfcpro;

public class RankingItem {
    private int rank;
    private String boothName;
    private String salesAmount;

    public RankingItem(int rank, String boothName, String salesAmount) {
        this.rank = rank;
        this.boothName = boothName;
        this.salesAmount = salesAmount;
    }

    public int getRank() { return rank; }
    public String getBoothName() { return boothName; }
    public String getSalesAmount() { return salesAmount; }
}