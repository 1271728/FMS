package com.example.fms.modules.msg.dto;

import java.util.List;

public class MsgMarkReadBatchReq {
    private List<Long> ids;

    public List<Long> getIds() { return ids; }
    public void setIds(List<Long> ids) { this.ids = ids; }
}
