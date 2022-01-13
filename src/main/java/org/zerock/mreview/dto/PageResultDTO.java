package org.zerock.mreview.dto;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class PageResultDTO<DTO, EN> {

    // DTO List
    private List<DTO> dtoList;

    // 총 페이지 번호
    private Integer totalPage;

    // 현재 페이지 번호
    private Integer page;

    // 목록 사이즈
    private Integer size;

    // 시작, 끝 페이지 번호
    private Integer start, end;

    // 이전, 다음
    private boolean prev, next;

    // 페이지 번호 목록
    private List<Integer> pageList;

    public PageResultDTO(Page<EN> result, Function<EN, DTO> fn) {

        dtoList = result.stream().map(fn).collect(Collectors.toList());

        this.totalPage = result.getTotalPages();

        makePageList(result.getPageable());
    }

    private void makePageList(Pageable pageable) {
        this.page = pageable.getPageNumber() + 1;
        this.size = pageable.getPageSize();

        int tempEnd = (int)(Math.ceil(page/10.0)) * 10;

        this.start = tempEnd - 9;
        this.prev = start > 1;
        this.end = totalPage > tempEnd ? tempEnd : totalPage;
        this.next = totalPage > tempEnd;

        this.pageList = IntStream.rangeClosed(this.start, this.end).boxed().collect(Collectors.toList());

    }
}
