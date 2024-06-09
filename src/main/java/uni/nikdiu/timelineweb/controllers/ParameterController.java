package uni.nikdiu.timelineweb.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.nikdiu.timelineweb.convectors.ParameterConvector;
import uni.nikdiu.timelineweb.dtos.FunctionDto;
import uni.nikdiu.timelineweb.dtos.ParameterDto;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Parameter;
import uni.nikdiu.timelineweb.services.ParameterService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/parameters")
@RequiredArgsConstructor
public class ParameterController {
    private final ParameterService parameterService;
    private final ParameterConvector parameterConvector;

    @GetMapping
    public List<ParameterDto> getAllParameters() {
        System.out.println("Received the request for all parameters");
        List<ParameterDto> parameters= parameterService.getAllParameters()
                .stream()
                .map(parameter -> parameterConvector.toDto(parameter))
                .collect(Collectors.toList());
        System.out.println("Found parameters: " + parameters);
        return parameters;
    }


    @PostMapping()
    public ParameterDto addParameter(@RequestBody ParameterDto parameterDto) {


        Map<FunctionDto, List<Parameter>> relatedParameters = parameterDto.getFunctions().stream().collect(Collectors.toMap(f ->
                        f,
                f -> {
                    if (f.getRelatedParameterIds() != null) {
                        return f.getRelatedParameterIds().stream()
                                .map(parameterService::getParameterById)
                                .collect(Collectors.toList());
                    } else {
                        return new ArrayList<>();
                    }
                }
        ));

        Parameter parameter = parameterConvector.toEntity(parameterDto,relatedParameters);
        System.out.println("Received parameterDto: " + parameterDto);
        System.out.println("Converted entity: " + parameter);

        parameter = parameterService.save(parameter);
        System.out.println("new parameter is saved: " + parameter);
        return parameterConvector.toDto(parameter);
    }
}
