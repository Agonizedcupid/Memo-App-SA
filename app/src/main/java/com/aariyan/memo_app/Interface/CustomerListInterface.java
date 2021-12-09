package com.aariyan.memo_app.Interface;

import com.aariyan.memo_app.Model.CustomerModel;

import java.util.List;

public interface CustomerListInterface {

    void customerList(List<CustomerModel> list);
    void error(String message);
}
