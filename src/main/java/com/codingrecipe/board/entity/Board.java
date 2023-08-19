package com.codingrecipe.board.entity;

import com.codingrecipe.board.dto.BoardRequestDto;
import lombok.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Getter   // get 메소드를 자동으로 만들어줌
@Setter   // set 메소드를 자동으로 만들어줌
@ToString  // 필드값 확인할 때 사용
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자
public class Board {
    private Long id;
    private String title;
    private String writer;
    private String createdTime;  // 글 작성시간
    private Long hits;  // 조회수
    private Long likes; // 좋아요수
    private String contents;
    private String category; //카테고리
    private List<String> imageUrl;
    //private LocalDateTime boardUpdatedTime;  // 글 수정시간

    public Board (BoardRequestDto boardRequestDto, String name) {
        this.title = boardRequestDto.getBoardTitle();
        this.writer = name;
        this.createdTime = new SimpleDateFormat("yyyy/MM/dd hh:mm").format(new Date(System.currentTimeMillis()));
        this.hits = 0L;
        this.likes = 0L;
        this.contents = boardRequestDto.getBoardContents();
        this.category = "자유게시판";
    }
}
