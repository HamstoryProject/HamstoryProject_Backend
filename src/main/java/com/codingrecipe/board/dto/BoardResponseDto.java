package com.codingrecipe.board.dto;

import com.codingrecipe.board.entity.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BoardResponseDto {

    private Long id;

    private String title;

    private String writer;

    private String createdTme;

    private Long hits;

    private String contents;

    private String category;

    private List<String> imageUrl;

    private int likes;

    public BoardResponseDto (Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.writer = board.getWriter();
        this.createdTme = board.getCreatedTime();
        this.hits = board.getHits();
        this.contents = board.getContents();
        this.category = board.getCategory();
        this.imageUrl = board.getImageUrl();
        this.likes = board.getLikers().size();
    }
}
