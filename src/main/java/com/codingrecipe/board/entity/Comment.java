package com.codingrecipe.board.entity;

import com.codingrecipe.board.dto.CommentRequestDto;
import com.codingrecipe.board.dto.CommentUpdateDto;
import lombok.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter   // get 메소드를 자동으로 만들어줌
@Setter   // set 메소드를 자동으로 만들어줌
@ToString  // 필드값 확인할 때 사용
@NoArgsConstructor
public class Comment {
    private Long commentId;
    private Long boardId;
    private String writer;
    private String contents;
    private int likes;
    private String createdTime;

    public Comment(CommentRequestDto commentRequestDto, String name) {
        this.boardId = Long.valueOf(commentRequestDto.getBoardId());
        this.writer = name;
        this.contents = commentRequestDto.getCommentContents();
        this.likes = 0;
        this.createdTime = new SimpleDateFormat("yyyy/MM/dd hh:mm").format(new Date(System.currentTimeMillis()));
    }

    public Comment(Comment origin, CommentUpdateDto commentUpdateDto) {
        this.commentId = origin.getCommentId();
        this.boardId = origin.getBoardId();
        this.writer = origin.getWriter();
        this.contents = commentUpdateDto.getContents();
        this.likes = origin.getLikes();
        this.createdTime = origin.getCreatedTime();
    }

}
