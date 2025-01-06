package com.lmxdawn.api.admin.req.DataBase;

import com.lmxdawn.api.admin.req.ListPageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TargetDataBaseQueryRequest extends ListPageRequest {
    private String connectName;
}
