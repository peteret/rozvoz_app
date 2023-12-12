package eu.webprofik.rozvoz;


public class Record {
    private String address;
    private String phoneNumber;
    private boolean isPaid;
    private double price;

    public Record(String address, String phoneNumber, boolean isPaid, double price) {
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.isPaid = isPaid;
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Getters and setters
}
