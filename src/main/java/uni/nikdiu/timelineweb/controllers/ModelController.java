package uni.nikdiu.timelineweb.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uni.nikdiu.timelineweb.convectors.ModelConvector;
import uni.nikdiu.timelineweb.dtos.FullModelDto;
import uni.nikdiu.timelineweb.dtos.ModelDto;
import uni.nikdiu.timelineweb.entities.Model;
import uni.nikdiu.timelineweb.services.GraphService;
import uni.nikdiu.timelineweb.services.ModelService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
public class ModelController {
    private final ModelService modelService;
    private final GraphService graphService;
    private final ModelConvector modelConvector;

    @GetMapping
    public List<ModelDto> getAllModels() {
        return modelService.getAllModels()
                .stream()
                .map(model -> modelConvector.toDto(model))
                .collect(Collectors.toList());
    }
    @GetMapping("{id}")
    public FullModelDto getModelById(@PathVariable Long id) {
        Model model = modelService.getModelById(id);
        FullModelDto fullModelDto = modelConvector.toFullDto(model);
        fullModelDto.setModelGraphDto(graphService.getModelGraph(model));
        return fullModelDto;
    }

}
