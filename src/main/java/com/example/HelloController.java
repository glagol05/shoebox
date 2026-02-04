package com.example;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @GetMapping("/")
    String hello() {
        return "Hello World!";
    }

    @Getter
    static class Result {
        private final int left;
        private final int right;
        private final long answer;

        Result(int left, int right, long answer) {
            this.left = left;
            this.right = right;
            this.answer = answer;
        }
    }

    @GetMapping("/calc")
    Result calc(@RequestParam int left, @RequestParam int right) {
        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("left", left)
                .addValue("right", right);
        return jdbcTemplate.queryForObject(
                "SELECT :left + :right AS answer",
                source,
                (rs, rowNum) -> new Result(left, right, rs.getLong("answer"))
        );
    }
}
