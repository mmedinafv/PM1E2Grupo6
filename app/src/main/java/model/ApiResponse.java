package model;

import java.util.Date;

public class ApiResponse<T> {
    public String message;
    public boolean success;
    private String status;
    private int statusCode;
    private Data<Contacto> data;
    private Date timestamp;


    public Data<Contacto> getData() {
        return data;
    }

    public void setData(Data<Contacto> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
