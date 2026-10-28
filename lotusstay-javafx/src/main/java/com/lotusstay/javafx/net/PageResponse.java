package com.lotusstay.javafx.net;

import java.util.Collections;
import java.util.List;

public class PageResponse<T> {
    public int pageNo;
    public int pageSize;
    public long totalElements;
    public int totalPages;
    public boolean last;
    public List<T> content;

    public List<T> getContent() {
        return content != null ? content : Collections.emptyList();
    }
}
