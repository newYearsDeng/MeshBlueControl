package com.northmeter.meshbluecontrol.bluetooth.bluetooth.data;




import com.northmeter.meshbluecontrol.bluetooth.bluetooth.tools.DateTool;

import java.util.Date;

;

/**
 * Created by benjamin on 16/5/25.
 */
public class MessageEntity {
    private String mMessage;
    private Date time;
    private MessageType mType;

    public MessageEntity() {
    }

    public MessageEntity(String message, Date time, MessageType type) {
        mMessage = message;
        this.time = time;
        mType = type;
    }

    @Override
    public String toString() {
        return "[" + DateTool.getSfMinutes().format(time) +
                "]  " + mMessage + "";
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public MessageType getType() {
        return mType;
    }

    public void setType(MessageType type) {
        mType = type;
    }


}
