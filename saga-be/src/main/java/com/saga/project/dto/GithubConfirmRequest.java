package com.saga.project.dto;

import lombok.Data;
import java.util.List;

@Data
public class GithubConfirmRequest {
    private List<String> repoUrls; // e.g., ["https://github.com/fpt-edu/saga-backend"]
}
