package com.qhc.jsonp.beans;

import org.springframework.stereotype.Component;

@Component
public class FlightBean {
    private String code;
    private String price;
    private String tickets;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTickets() {
        return tickets;
    }

    public void setTickets(String tickets) {
        this.tickets = tickets;
    }
}
