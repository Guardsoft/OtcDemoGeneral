package com.otc.model.request;

public class InitializeRequest {

    private Header header;
    private Device device;

    public void setHeader(Header header){
        this.header = header;
    }

    public Header getHeader(){
        return header;
    }

    public void setDevice(Device device){
        this.device = device;
    }

    public Device getDevice(){
        return device;
    }

    @Override
    public String toString(){
        return
                "Request{" +
                        "header = '" + header + '\'' +
                        ",device = '" + device + '\'' +
                        "}";
    }

}
