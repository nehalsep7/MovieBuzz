package com.example.android.moviebuzzapp;

import android.graphics.Movie;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kumnehal on 05/21/16.
 */
public class MovieResponse {
    @SerializedName("page")
    private int page;
    @SerializedName("results")
    private List<MovieParams> results;
    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("total_pages")
    private int totalPages;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<MovieParams> getResults() {
        return results;
    }

    public void setResults(List<MovieParams> results) {
        this.results = results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
