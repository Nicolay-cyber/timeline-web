package uni.nikdiu.timelineweb.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uni.nikdiu.timelineweb.convectors.LineConvector;
import uni.nikdiu.timelineweb.convectors.ParameterConvector;
import uni.nikdiu.timelineweb.dtos.LineDto;
import uni.nikdiu.timelineweb.dtos.ParameterDto;
import uni.nikdiu.timelineweb.services.GraphService;
import uni.nikdiu.timelineweb.services.ParameterService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/graph")
@RequiredArgsConstructor
public class GraphController {
    private final GraphService graphService;
    private final LineConvector lineConvector;
    @GetMapping("/{id}")
    public LineDto getLineByParameterId(@PathVariable Long id) {
        System.out.println("Received request from for line for parameter  " + id);
        return lineConvector.toDto(graphService.getLineByParameterId(id));
    }
}
