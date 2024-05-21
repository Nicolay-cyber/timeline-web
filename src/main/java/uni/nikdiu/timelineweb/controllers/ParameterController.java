package uni.nikdiu.timelineweb.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.nikdiu.timelineweb.convectors.ParameterConvector;
import uni.nikdiu.timelineweb.dtos.ParameterDto;
import uni.nikdiu.timelineweb.services.ParameterService;

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
        return parameterService.getAllParameters()
                .stream()
                .map(parameter -> parameterConvector.toDto(parameter))
                .collect(Collectors.toList());
    }

    @PostMapping()
    public ResponseEntity<Map<String, String>> addParameter(@RequestBody ParameterDto parameterDto) {
        // Your logic to add the parameter here...
        System.out.println("Received parameterDto: " + parameterDto);
        // Return a response indicating success as a JSON object
        Map<String, String> response = new HashMap<>();
        response.put("message", "Parameter added successfully");

        return ResponseEntity.ok(response);
    }
}
