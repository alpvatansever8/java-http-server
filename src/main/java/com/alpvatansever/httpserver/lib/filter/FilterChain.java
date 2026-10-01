package com.alpvatansever.httpserver.lib.filter;

import com.alpvatansever.httpserver.lib.http.HttpRequest;
import com.alpvatansever.httpserver.lib.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

public class FilterChain {
    private final List<Filter> filters;
    private int currentPosition = 0;
    private boolean isChainCompleted = false;

    public FilterChain(List<Filter> filters) {
        this.filters = new ArrayList<>(filters);
    }

    public void doFilter(HttpRequest request, HttpResponse response) throws Exception {
        if (currentPosition < filters.size()) {
            Filter filter = filters.get(currentPosition++);
            filter.doFilter(request, response, this);
        } else {
            this.isChainCompleted = true;
        }
    }

    public boolean isFullyProcessed() {
        return isChainCompleted;
    }
}
