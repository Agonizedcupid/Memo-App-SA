package com.aariyan.memo_app.Interface;

import com.aariyan.memo_app.Model.MessageModel;

import java.util.List;

public interface MessageListInterface {

    void messageList(List<MessageModel> list);
    void error(String error);
}
