package org.zerock.mreview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.mreview.dto.MovieDTO;
import org.zerock.mreview.dto.PageRequestDTO;
import org.zerock.mreview.dto.PageResultDTO;
import org.zerock.mreview.entity.Movie;
import org.zerock.mreview.entity.MovieImage;
import org.zerock.mreview.repository.MovieImageRepository;
import org.zerock.mreview.repository.MovieRepository;
import org.zerock.mreview.repository.ReviewRepository;

import java.util.*;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService{

    private final MovieRepository movieRepository;

    private final MovieImageRepository movieImageRepository;

    private final ReviewRepository reviewRepository;

    @Transactional
    @Override
    public Long register(MovieDTO movieDTO) {

        Map<String, Object> entityMap = dtoToEntity(movieDTO);
        Movie movie = (Movie) entityMap.get("movie");
        List<MovieImage> movieImageList = (List<MovieImage>) entityMap.get("imgList");

        movieRepository.save(movie);

//        movieImageRepository.saveAll(movieImageList);
        movieImageList.forEach(movieImage -> {
            movieImageRepository.save(movieImage);
        });

        return movie.getMno();
    }

    @Override
    public PageResultDTO<MovieDTO, Object[]> getList(PageRequestDTO requestDTO) {

        Pageable pageable = requestDTO.getPageable(Sort.by("mno").descending());

        Page<Object[]> result = movieRepository.getListPage(pageable);

        Function<Object[], MovieDTO> fn = (arr -> entitiesToDTO(
                (Movie) arr[0],
                (Arrays.asList((MovieImage)arr[1])),
                (Double) arr[2],
                (Long) arr[3]
        ));

        return new PageResultDTO<>(result, fn);
    }

    @Override
    public MovieDTO getMovie(Long mno) {

        List<Object[]> result = movieRepository.getMovieWithAll(mno);

        Movie movie = (Movie) result.get(0)[0];

        List<MovieImage> movieImageList = new ArrayList<>();

        result.forEach(arr -> {
            MovieImage movieImage = (MovieImage) arr[1];
            movieImageList.add(movieImage);
        });

        Double avg = (Double) result.get(0)[2];
        Long reviewCount = (Long) result.get(0)[3];

        return entitiesToDTO(movie, movieImageList, avg, reviewCount);
    }

    @Transactional
    @Override
    public void remove(Long mno) {

        Optional<Movie> result = movieRepository.findById(mno);

        if (result.isPresent()) {
            Movie movie = result.get();
            reviewRepository.deleteByMovie(movie);
            movieImageRepository.deleteByMovie(movie);
            movieRepository.delete(movie);
        }
    }

    @Override
    public void modify(MovieDTO movieDTO) {

        Optional<Movie> result = movieRepository.findById(movieDTO.getMno());

        if(result.isPresent()) {

            Movie movie = result.get();

            movie.changeTitle(movieDTO.getTitle());

            movieRepository.save(movie);
        }
    }
}
