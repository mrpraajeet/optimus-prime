package dev.praajeet.optimus.controller

import io.swagger.v3.oas.annotations.Hidden
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {
    @GetMapping("/")
    @Hidden
    fun home(): String = "redirect:/swagger-ui.html"
}
