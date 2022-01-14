package org.zerock.mreview.repository;

import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.mreview.entity.Movie;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("select m, mi, avg(coalesce(r.grade, 0)), count(r) from Movie m " +
            "left outer join MovieImage mi on mi.movie = m " +
            "left outer join Review r on r.movie = m group by m ")
    Page<Object[]> getListPage(BooleanBuilder booleanBuilder, Pageable pageable);

    @Query("select m, mi, avg(coalesce(r.grade, 0)), count(r) from Movie m " +
            "left outer join MovieImage mi on mi.movie = m " +
            "left outer join Review r on r.movie = m " +
            "where m.mno = :mno group by mi ")
    List<Object[]> getMovieWithAll(Long mno);

    @Query("select m, mi, avg(coalesce(r.grade, 0)), count(r) from Movie m " +
            "left outer join MovieImage mi on mi.movie = m " +
            "left outer join Review r on r.movie = m " +
            "where m.title like %:title% " +
            "group by m ")
    Page<Object[]> getSearchListPage(String title, Pageable pageable);
}
