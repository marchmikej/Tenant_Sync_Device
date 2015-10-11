package com.tenantsync.testproject3;

/**
 * Created by Dad on 9/27/2015.
 */
public class MaintenaceRequest {
    public void setRequest(String request) {
        this.request = request;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String request;

    public String status;

    public String appointment;

    public String id;

    public String getResponse() {
        return response;
    }

    public String getRequest() {
        return request;
    }

    public String response;

    public MaintenaceRequest(String request, String response, String status, String appointment, String id) {
        this.request = request;
        this.response = response;
        this.status = status;
        this.appointment = appointment;
        this.id = id;
    }
}
