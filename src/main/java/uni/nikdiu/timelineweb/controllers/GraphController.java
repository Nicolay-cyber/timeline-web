package uni.nikdiu.timelineweb.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uni.nikdiu.timelineweb.convectors.LineConvector;
import uni.nikdiu.timelineweb.convectors.ParameterConvector;
import uni.nikdiu.timelineweb.dtos.LineDto;
import uni.nikdiu.timelineweb.dtos.ModelDto;
import uni.nikdiu.timelineweb.dtos.ParameterDto;
import uni.nikdiu.timelineweb.entities.Model;
import uni.nikdiu.timelineweb.services.GraphService;
import uni.nikdiu.timelineweb.services.ModelService;
import uni.nikdiu.timelineweb.services.ParameterService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/graph")
@RequiredArgsConstructor
public class GraphController {
    private final GraphService graphService;
    private final LineConvector lineConvector;
    private final ModelService modelService;
    @GetMapping("/{id}")
    public LineDto getLineByParameterId(@PathVariable Long id) {
        System.out.println("Received request from for line for parameter  " + id);
        return lineConvector.toDto(graphService.getLineByParameterId(id));
    }
    @GetMapping("/model/{id}")
    public List<LineDto> getModelChart(@PathVariable Long id) {
        Model model = modelService.getModelById(id);
        System.out.println("Received request for lines of model: " + model);

        List<LineDto> lineDtos = model.getParameters().stream()
                .map(parameter -> lineConvector.toDto(graphService.getLineByParameterId(parameter.getId())))
                .collect(Collectors.toList());

        return lineDtos;
    }
}
