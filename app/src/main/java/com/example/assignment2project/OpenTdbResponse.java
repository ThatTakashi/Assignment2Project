package com.example.assignment2project;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OpenTdbResponse {

    @SerializedName("response_code")
    private int responseCode;

    @SerializedName("results")
    private List<QuestionItem> results;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public List<QuestionItem> getResults() {
        return results;
    }

    public void setResults(List<QuestionItem> results) {
        this.results = results;
    }
}
