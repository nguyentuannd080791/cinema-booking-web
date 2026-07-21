package com.springboot.cinema.controller;

import com.springboot.cinema.entity.Category;
import com.springboot.cinema.entity.Movie;
import com.springboot.cinema.service.CategoryService;
import com.springboot.cinema.service.MovieService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {
    private MovieService movieService;
    private CategoryService categoryService;

    public HomeController(MovieService movieService, CategoryService categoryService) {
        this.movieService = movieService;
        this.categoryService = categoryService;
    }

    @GetMapping("/home")
    public String home(HttpSession session,
                       Model model,
                       @RequestParam(value = "movie", required = false) String rawMovieName,
                       @RequestParam(value = "category", required = false) String rawCategoryName,
                       @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                       @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        String movieName = (rawMovieName == null || rawMovieName.trim().isEmpty()) ? null : rawMovieName;
        String categoryName = (rawCategoryName == null || rawCategoryName.trim().isEmpty()) ? null : rawCategoryName;

        // Giới hạn page/size hợp lệ để tránh URL bất thường gây lỗi hoặc tải danh sách quá lớn
        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 1), 50);

        Page<Movie> moviePage = movieService.getMovieList(movieName, categoryName, page, size);
        List<Category> categoryList = categoryService.getCategoryList();

        int current = page;
        int total = moviePage.getTotalPages();
        int startPage = 0;
        int endPage = 0;
        if(total > 0)
        {
            startPage = Math.max(0, current - 2);
            endPage = Math.min(current + 2, total - 1);

            if (current <= 2) {
                endPage = Math.min(total - 1, 4);
            } else if (current >= total - 3) {
                startPage = Math.max(0, total - 5);
            }
        }

        model.addAttribute("movieList", moviePage.getContent());
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("movieSearch", movieName);
        model.addAttribute("moviePage", moviePage);
        model.addAttribute("categorySelected", categoryName);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("user", session.getAttribute("user"));
        return "home";
    }
}
