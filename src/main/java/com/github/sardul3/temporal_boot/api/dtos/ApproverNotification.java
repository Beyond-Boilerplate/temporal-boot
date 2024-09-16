package com.github.sardul3.temporal_boot.api.dtos;

import lombok.Data;
import java.util.List;

@Data
public class ApproverNotification {
    private List<String> approvers;
    private String bannerMessage;

}
